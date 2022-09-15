package com.vvenn.timecard.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.vvenn.timecard.entity.User;
import com.vvenn.timecard.form.UserForm;
import com.vvenn.timecard.form.UserFormAction;
import com.vvenn.timecard.service.Helper;
import com.vvenn.timecard.service.SectionService;
import com.vvenn.timecard.service.UserService;
import com.vvenn.timecard.service.YmParameterService;

import java.text.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ユーザー管理処理コントローラー
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private YmParameterService ymparam;

    /**
     * "/user/new"へのアクセス時に、新規ユーザー登録画面に遷移します。<br>
     * 管理者権限を持つユーザーのみアクセス可能です。<br>
     * ユーザー一覧画面のユーザー登録ボタンを押すとアクセスします。<br>
     * 新規ユーザーを登録するためのhtmlを出力します。
     *
     * @param model :Model htmlに渡す値
     * @param user  :User ログイン中のユーザー情報
     * @return 非管理者の場合、"redirect:/"
     * @return "user/detail.html"
     */
    @RequestMapping("/new")
    public String newUser(Model model, @AuthenticationPrincipal User user) {

        // 管理者権限の有無を判定
        // 非管理者の場合、勤務表画面へリダイレクト
        if (!user.isAdminFlag()) {
            return "redirect:/";
        }

        // 初回アクセス時にフォームに初期値をセット
        if (!model.containsAttribute("userForm")) {
            UserForm userForm = new UserForm();

            // 登録画面フォームに初期値に現在年月日をセット
            userForm.setJoinedAt(Helper.timestampToDateString(Helper.getNowTimestamp()));

            model.addAttribute("userForm", userForm);
        }

        // 共通header用
        model.addAttribute("displayName", user.getDisplayName());
        // 戻るボタンリンク先指定
        model.addAttribute("selectBack", "/user/list");

        model.addAttribute("sectionList", sectionService.getSectionList());
        model.addAttribute("cardtitle", "ユーザー登録");
        model.addAttribute("action", "create");
        model.addAttribute("buttontext", "登録");
        model.addAttribute("passwordFormTitle", "パスワード:");

        return "user/detail";

    }

    /**
     * "/user/edit/{id}"へのアクセス時に、パスパラメータで取得するユーザー編集画面へ遷移します。<br>
     * 管理者権限を持つユーザーのみアクセス可能です。<br>
     * ユーザー一覧の変更ボタンを押すとアクセスします。<br>
     *
     * @param model :Model htmlに渡す値
     * @param user  :User ログイン中のユーザー情報
     * @param id    編集対象のユーザーid
     * @return "user/detail.html"
     */
    @RequestMapping("/edit/{id}")
    public String edit(Model model, @AuthenticationPrincipal User user, @PathVariable Long id) {

        // 管理者権限の有無を判定
        // 非管理者の場合、勤務表画面へリダイレクト
        if (!user.isAdminFlag()) {
            return "redirect:/";
        }

        // パスパラメータで受け取ったidを元に編集対象のユーザーデータを取得
        User editUser = this.userService.getData(id);

        UserForm userForm = null;

        // 初回アクセス時の処理
        if (!model.containsAttribute("userForm")) {
            userForm = new UserForm();
            model.addAttribute("userForm", userForm);
        } else {
            Map<String, Object> modelMap = model.asMap();
            userForm = (UserForm) modelMap.get("userForm");
        }

        // フォームに対象ユーザーの現在のデータをセット
        userForm.setId(editUser.getId());
        userForm.setSectionCode(editUser.getSectionCode());
        userForm.setDisplayName(editUser.getDisplayName());
        userForm.setUsername(editUser.getUsername());
        // DBより取得した Timestamp 型の joinedAt を String 型に変換
        userForm.setJoinedAt(Helper.timestampToDateString(editUser.getJoinedAt()));
        userForm.setAdminFlag(editUser.isAdminFlag());

        // 共通header用
        model.addAttribute("displayName", user.getDisplayName());
        // 戻るボタンリンク先指定
        model.addAttribute("selectBack", "/user/list");

        model.addAttribute("sectionList", sectionService.getSectionList());
        model.addAttribute("userForm", userForm);
        model.addAttribute("cardtitle", "ユーザー変更");
        model.addAttribute("passwordFormTitle", "パスワード変更:");
        model.addAttribute("action", "update");
        model.addAttribute("buttontext", "変更");

        return "user/detail";

    }

    /**
     * "/user/password"へのアクセス時に、ログインユーザーのパスワード変更画面へ遷移します。<br>
     * 勤務表画面のパスワード変更を押すとアクセスします。
     * 
     * @param model :Model htmlに渡す値
     * @param user  :User ログイン中のユーザー情報
     * @return "user/detail.html"
     */
    @GetMapping("/password")
    public String password(Model model, @AuthenticationPrincipal User user) {

        UserForm userForm = null;

        // 初回アクセス時の処理
        if (!model.containsAttribute("userForm")) {
            userForm = new UserForm();
            model.addAttribute("userForm", userForm);
        } else {
            Map<String, Object> modelMap = model.asMap();
            userForm = (UserForm) modelMap.get("userForm");
        }

        // フォームにユーザーのデータを表示
        userForm.setSectionCode(user.getSectionCode());
        userForm.setDisplayName(user.getDisplayName());
        userForm.setUsername(user.getUsername());
        // DBより取得した Timestamp 型の joinedAt を String 型に変換
        userForm.setJoinedAt(Helper.timestampToDateString(user.getJoinedAt()));
        userForm.setAdminFlag(user.isAdminFlag());

        // 共通header用
        model.addAttribute("displayName", user.getDisplayName());
        // 戻るボタンリンク先指定
        model.addAttribute("selectBack", "/");

        model.addAttribute("sectionList", sectionService.getSectionList());
        model.addAttribute("userForm", userForm);
        model.addAttribute("cardtitle", "パスワード変更");
        model.addAttribute("action", "password");
        model.addAttribute("passwordFormTitle", "パスワード変更:");
        model.addAttribute("buttontext", "変更");
        // パスワード以外は変更不可のため、disabledを付ける
        model.addAttribute("disabled", true);

        return "user/detail";

    }

    /**
     * "/user/create"へのPOSTアクセス時、userFormに入力された情報を元に、新規ユーザーをDBに登録します。
     *
     * @param model              :Model htmlに渡す値
     * @param redirectAttributes :RedirectAttributes リダイレクト時の値の保持
     * @param userForm           :UserForm フォームの内容
     * @param bindingResult      :BindingResult エラー情報
     * @param request            :HttpServletRequest リダイレクト先を元居たページに設定
     * @return エラー時 :入力を保持し"redirect:" + referer
     * @return 登録成功時 :"redirect:/user/list/"
     * @throws ParseException Helper.javaに含まれる日付型変換メソッド実行時のエラー
     */
    @PostMapping("/create")
    public String registerPost(Model model, RedirectAttributes redirectAttributes,
            @Validated(UserFormAction.Create.class) UserForm userForm, BindingResult bindingResult,
            HttpServletRequest request) throws ParseException {

        String referer = request.getHeader("Referer");

        // UserForm.java に設定した入力値検証エラー有無の判定
        if (!bindingResult.hasErrors()) {
            // DB登録処理
            try {
                userService.registerUser(userForm.getUsername(), userForm.getPassword(), userForm.getDisplayName(),
                        userForm.getSectionCode(), userForm.getJoinedAt(), userForm.getAdminFlag(), false);
                return "redirect:/user/list/";
            } catch (DataIntegrityViolationException e) {
                FieldError fieldError = new FieldError(bindingResult.getObjectName(), "username", "このメールアドレスは使用できません");
                bindingResult.addError(fieldError);
            }
        }

        // リダイレクト時にフォームの入力データとBindingResultのエラーを保持
        redirectAttributes.addFlashAttribute("userForm", userForm);
        redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + Conventions.getVariableName(userForm),
                bindingResult);

        return "redirect:" + referer;
    }

    /**
     * "/user/update"へのPOSTアクセス時、userFormに入力された情報を元に、対象ユーザーの情報を変更します。
     *
     * @param model              :Model htmlに渡す値
     * @param user               :User ログイン中のユーザー情報
     * @param redirectAttributes :RedirectAttributes リダイレクト時の値の保持
     * @param userForm           :UserForm フォームの内容
     * @param bindingResult      :BindingResult エラー情報
     * @param request            :HttpServletRequest リダイレクト先を元居たページに設定
     * @return "redirect:" + referer
     * @throws ParseException Helper.javaに含まれる日付型変換メソッド実行時のエラー
     */
    @PostMapping("/update")
    public String upDatePost(Model model, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes,
            @Validated(UserFormAction.Update.class) UserForm userForm, BindingResult bindingResult,
            HttpServletRequest request) throws ParseException {

        String referer = request.getHeader("Referer");

        // UserForm.java に設定した入力値検証エラー有無の判定
        if (!bindingResult.hasErrors()) {
            try {
                User editUser = this.userService.getData(userForm.getId());

                // パスワードを入力せずに変更処理を行う場合は、既存のパスワードを再設定
                String password = null;
                if (userForm.getPassword().equals("")) {
                    password = editUser.getPassword();
                } else {
                    password = userForm.getPassword();
                }
                userService.updateUser(editUser, userForm.getUsername(), password, userForm.getDisplayName(),
                        userForm.getSectionCode(), userForm.getJoinedAt(), userForm.getAdminFlag());

                return "redirect:" + referer;
            } catch (DataIntegrityViolationException e) {

            }
        }

        // リダイレクト時にフォームの入力データとBindingResultのエラーを保持
        redirectAttributes.addFlashAttribute("userForm", userForm);
        redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + Conventions.getVariableName(userForm),
                bindingResult);

        return "redirect:" + referer;

    }

    /**
     * "/user/password"へのPOSTアクセス時、userFormに入力された情報を元に、パスワードを変更します。
     *
     * @param model              :Model htmlに渡す値
     * @param user               :User ログイン中のユーザー情報
     * @param redirectAttributes :RedirectAttributes リダイレクト時の値の保持
     * @param userForm           :UserForm フォームの内容
     * @param bindingResult      :BindingResult エラー情報
     * @param request            :HttpServletRequest リダイレクト先を元居たページに設定
     * @return "redirect:" + referer
     */
    @PostMapping("/password")
    public String passwordPost(Model model, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes,
            @Validated(UserFormAction.Password.class) UserForm userForm, BindingResult bindingResult,
            HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        // UserForm.java に設定した入力値検証エラー有無の判定
        if (!bindingResult.hasErrors()) {
            // PW変更処理
            try {
                userService.updatePassword(user, userForm.getPassword());
                return "redirect:" + referer;
            } catch (DataIntegrityViolationException e) {

            }
        }

        // リダイレクト時にフォームの入力データと BindingResult のエラーを保持
        redirectAttributes.addFlashAttribute("userForm", userForm);
        redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + Conventions.getVariableName(userForm),
                bindingResult);

        return "redirect:" + referer;

    }

    /**
     * list.htmlへのコントローラーです。勤務表画面のユーザー管理ボタンを押すと遷移されます。
     * 
     * @param model    viewに渡すモデルデータ
     * @param request  httpリクエスト
     * @param user     ログインユーザー
     * @param pageable ページャー
     * @param ym       年月情報
     * @return 管理者でない場合 "redirect:/"
     * @return "user/list.html"
     */
    @GetMapping("/list")
    public String userList(Model model, HttpServletRequest request, @AuthenticationPrincipal User user,
            Pageable pageable, @RequestParam Optional<String> ym) {

        // 管理者ではない時の処理
        if (!user.isAdminFlag()) {
            return "redirect:/";
        }

        // Page<User> userPager = this.userService.findActiveUserAll(pageable);
        // model.addAttribute("list", userPager.getContent());
        // model.addAttribute("pager", userPager);

        List<User> userList = this.userService.findActiveUserAll();
        model.addAttribute("list", userList);

        SimpleDateFormat customDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        model.addAttribute("customDateFormat", customDateFormat);
        // 表示対象年月
        this.ymparam.setParam(ym);

        // 共通header用
        model.addAttribute("displayName", user.getDisplayName());
        model.addAttribute("ym", ymparam.getYm());
        model.addAttribute("ymselect", ymparam.getSelectOptionsForCsv());
        model.addAttribute("action", "csvExport");

        return "user/list";
    }

    /**
     * パスパラメータで指定されたidのユーザーを論理削除します。
     * 
     * @param request httpリクエスト
     * @param id      削除対象ユーザーのid
     * @return "redirect:" + referer
     */
    @RequestMapping("/delete/{id}")
    public String deleteUser(HttpServletRequest request, @PathVariable Long id) {

        userService.softDeleteUserById(id);
        // リクエスト実行時のブラウザのURLへリダイレクトします。
        // これにより表示年月とページのパラメータは保持されたままとなります。
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}