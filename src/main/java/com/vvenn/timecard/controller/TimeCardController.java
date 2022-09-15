package com.vvenn.timecard.controller;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.Valid;

import com.vvenn.timecard.entity.TimeCardRecord;
import com.vvenn.timecard.entity.TimeCardRecordView;
import com.vvenn.timecard.entity.User;
import com.vvenn.timecard.form.ResponseJson;
import com.vvenn.timecard.form.timecard.DefaultTimeForm;
import com.vvenn.timecard.form.timecard.TimeRecordForm;
import com.vvenn.timecard.service.Helper;
import com.vvenn.timecard.service.TimeCardRecordService;
import com.vvenn.timecard.service.UserService;
import com.vvenn.timecard.service.YmParameterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 勤務表管理処理コントローラー
 */
@Controller
public class TimeCardController {

    @Autowired
    private TimeCardRecordService timeCardRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private YmParameterService ymparam;

    /**
     * timecard.htmlへのコントローラーです。 ログイン成功時にアクセスされます。
     * 
     * @param model viewへ渡すためのmodel
     * @param user  ログインユーザー
     * @param ym    表示年月クエリストリング
     * @return "timecard/timecard.html"
     */
    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal User user, @RequestParam Optional<String> ym) {

        // 共通header用
        model.addAttribute("displayName", user.getDisplayName());

        // 表示対象年月
        this.ymparam.setParam(ym);
        model.addAttribute("ym", ymparam.getYm());
        model.addAttribute("ymselect", ymparam.getSelectOptions());

        // 勤務表データ
        int id = user.getId().intValue();

        this.timeCardRecordService.setFirstAndLastDay();
        List<TimeCardRecordView> timeCardRecordViews = timeCardRecordService.findTimeCardRecordViewAll(id);
        model.addAttribute("timeCardRecordViews", timeCardRecordViews);

        // 共通ヘッダー用
        model.addAttribute("adminFlag", user.isAdminFlag());
        model.addAttribute("displayName", user.getDisplayName());

        // ユーザーデータ
        model.addAttribute("user", user);

        // 時間・時刻プルダウン用データ
        List<Integer> timeList = timeCardRecordService.getTimeList();
        model.addAttribute("timeList", timeList);
        model.addAttribute("timePeriod", Helper.getTimePeriod());

        // デフォルト時間データ
        DefaultTimeForm defaultTimeForm = new DefaultTimeForm(user.getDefaultStartTime(), user.getDefaultEndTime(),
                user.getDefaultBreakTime());

        model.addAttribute("defaultTimeForm", defaultTimeForm);

        return "timecard/timecard";
    }

    /**
     * 「/save」へのPOSTアクセスに対して、TimeCardRecordデータのDB登録処理をし<br>
     * 成否フラグとエラー時のメッセージをJson形式で返します。
     * 
     * @param user           ログインユーザー
     * @param timeRecordForm Formバリデーションを通過したTimeCardRecordデータ
     * @param bindingResult  フォームからの送信データとFormクラスのバインド結果
     * @return Json形式の登録成否フラグとエラーメッセージ
     */
    @ResponseBody
    @PostMapping("/save")
    public ResponseJson save(@AuthenticationPrincipal User user, @RequestBody @Valid TimeRecordForm timeRecordForm,
            BindingResult bindingResult) {

        ResponseJson responseJson = new ResponseJson();

        // バリデーションエラー時
        if (bindingResult.hasErrors()) {
            String message = "";
            for (ObjectError error : bindingResult.getAllErrors()) {
                message += error.getDefaultMessage() + "\n";
            }

            responseJson.setResultFlag(false);
            responseJson.setMessage(message);
            return responseJson;
        }

        // バリデーション通過時
        Long id = null;
        if (timeRecordForm.getId() != 0) {
            id = (long) timeRecordForm.getId();
        }
        int userId = user.getId().intValue();
        java.sql.Date dutyDate = java.sql.Date.valueOf(timeRecordForm.getDutyDate());
        int startTime = timeRecordForm.getStartTime();
        int endTime = timeRecordForm.getEndTime();
        int breakTime = timeRecordForm.getBreakTime();
        String remarks = timeRecordForm.getRemarks();

        int overTime = this.timeCardRecordService.overTimeCheck(startTime, endTime, breakTime,
                user.getDefaultStartTime(), user.getDefaultEndTime(), user.getDefaultBreakTime());

        this.timeCardRecordService.addDate(id, userId, dutyDate, startTime, endTime, breakTime, remarks, overTime);
        responseJson.setResultFlag(true);
        return responseJson;
    }

    /**
     * 「/delete」へのPOSTアクセスに対して、TimeCardRecordデータのDB物理削除処理をします。
     * 
     * @param user           ログインユーザー
     * @param timeCardRecord timeCardRecordデータ
     */
    @ResponseBody
    @PostMapping("/delete")
    public void delete(@AuthenticationPrincipal User user, @RequestBody TimeCardRecord timeCardRecord) {

        int id = user.getId().intValue();
        java.sql.Date dutyDate = timeCardRecord.getDutyDate();

        this.timeCardRecordService.deleteDate(id, dutyDate);
    }

    /**
     * 「/user/default」へのPOSTアクセスに対して、ログインユーザーのデフォルト時間のDB更新処理をし<br>
     * 成否フラグとエラー時のメッセージをJson形式で返します。
     * 
     * @param user            ログインユーザー
     * @param defaultTimeForm Formバリデーションを通過したデフォルト時間データ
     * @param bindingResult   フォームからの送信データとFormクラスのバインド結果
     * @return Json形式の登録成否フラグとエラーメッセージ
     */
    @ResponseBody
    @PostMapping("/user/default")
    public ResponseJson defaultTime(@AuthenticationPrincipal User user,
            @RequestBody @Valid DefaultTimeForm defaultTimeForm, BindingResult bindingResult) {

        int defaultStartTime = defaultTimeForm.getDefaultStartTime();
        int defaultEndTime = defaultTimeForm.getDefaultEndTime();
        int defaultBreakTime = defaultTimeForm.getDefaultBreakTime();
        ResponseJson responseJson = new ResponseJson();

        if (bindingResult.hasErrors()) {
            String message = "";
            for (ObjectError error : bindingResult.getAllErrors()) {
                message += error.getDefaultMessage() + "\n";
            }

            responseJson.setResultFlag(false);
            responseJson.setMessage(message);
            return responseJson;
        }

        // デフォルトタイム変更
        this.userService.updateDefaultTime(user, defaultStartTime, defaultEndTime, defaultBreakTime);
        responseJson.setResultFlag(true);
        return responseJson;
    }

    /**
     * 「/show/{id}」へのGETアクセスに対して、管理者権限がない場合はリダイレクトします。<br>
     * 管理者権限がある場合、IDパラメータの該当ユーザーの勤務データを渡し<br>
     * 「timecard.html」を出力します。
     * 
     * @param model viewへ渡すためのmodel
     * @param user  ログインユーザー
     * @param ym    表示年月クエリストリング
     * @param id    照会対象ユーザーのID
     * @return "timecard/timecard.html"
     */
    @GetMapping("/show/{id}")
    public String show(Model model, @AuthenticationPrincipal User user, @RequestParam Optional<String> ym,
            @PathVariable Long id) {

        if (!ym.isPresent()) {
            return "redirect:/logout";
        }

        if (!user.isAdminFlag()) {
            return "redirect:/";
        }

        User referenceUser = userService.getData(id);

        if (referenceUser == null) {
            return "redirect:/logout";
        }

        // 共通header用
        model.addAttribute("displayName", "照会： " + referenceUser.getDisplayName());
        model.addAttribute("adminFlag", user.isAdminFlag());

        // 表示対象年月
        this.ymparam.setParam(ym);
        model.addAttribute("ym", ymparam.getYm());
        model.addAttribute("ymselect", ymparam.getSelectOptions());

        // 勤務表データ
        this.timeCardRecordService.setFirstAndLastDay();
        List<TimeCardRecordView> timeCardRecordViews = timeCardRecordService.findTimeCardRecordViewAll(id.intValue());
        model.addAttribute("timeCardRecordViews", timeCardRecordViews);

        // ユーザーデータ
        model.addAttribute("user", referenceUser);

        // 照会モード設定
        model.addAttribute("action", "show");

        return "timecard/timecard";
    }
}
