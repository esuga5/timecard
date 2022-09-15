package com.vvenn.timecard.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vvenn.timecard.service.ExportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

import com.vvenn.timecard.entity.User;
import com.vvenn.timecard.export.ExcelView;
import com.vvenn.timecard.service.TimeCardRecordService;
import com.vvenn.timecard.service.UserService;
import com.vvenn.timecard.entity.TimeCardRecordView;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 外部出力処理コントローラー
 */
@Controller
@RequestMapping("/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private TimeCardRecordService timeCardRecordService;

    @Autowired
    private UserService userService;

    /**
     * "/export/csv"へのGETアクセス時にCSVファイルをエクスポートします。<br>
     * 管理者権限を持つユーザーのみ使用可能です。<br>
     * クエリストリングで受け取った年月を元に、対象期間内の各ユーザーの勤務時間関係データを、<br>
     * 一つのCSVファイルに纏めてダウンロードします。
     *
     * @param user ログイン中のユーザー情報を保持するUserクラス
     * @param ym   クエリストリングから取得する年月情報
     * @return CSVファイル
     * @throws JsonProcessingException      CSV作成時エラー
     * @throws UnsupportedEncodingException 文字コードのエンコード実行時エラー
     * @throws ParseException               SimpleDateFormat実行時エラー
     */
    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(@AuthenticationPrincipal User user, @RequestParam Optional<String> ym)
            throws JsonProcessingException, UnsupportedEncodingException, ParseException {

        // 管理者権限の有無を判定
        // 非管理者の場合、404エラーを表示
        if (!user.isAdminFlag()) {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }

        return exportService.createCsv(ym);

    }

    /**
     * ログインユーザーのExcelデータを出力します。
     * 
     * @param excelView 出力するエクセルデータmodel
     * @param user      ログインユーザー
     * @param ym        年月
     * @return ログインユーザーの月別勤務表Excelファイル
     */
    @GetMapping("/excel")
    public ExcelView exportExcel(ExcelView excelView, @AuthenticationPrincipal User user,
            @RequestParam Optional<String> ym) {

        List<TimeCardRecordView> timeCardRecordViews = timeCardRecordService
                .findTimeCardRecordViewAll(user.getId().intValue());
        return exportService.getXlsxData(excelView, user, timeCardRecordViews, ym);
    }

    /**
     * 照会ユーザーのExcelデータを出力します。
     * 
     * @param excelView 出力するエクセルデータmodel
     * @param user      ログインユーザー
     * @param ym        年月
     * @param id        照会ユーザーid
     * @return 照会ユーザーの月別勤務表Excelファイル
     */
    @GetMapping("/excel/{id}")
    public ExcelView exportReferenceExcel(ExcelView excelView, @AuthenticationPrincipal User user,
            @RequestParam Optional<String> ym, @PathVariable Long id) {

        if (!user.isAdminFlag()) {
            return excelView;
        }

        List<TimeCardRecordView> timeCardRecordViews = timeCardRecordService.findTimeCardRecordViewAll(id.intValue());
        return exportService.getXlsxData(excelView, userService.getData(id), timeCardRecordViews, ym);
    }
}