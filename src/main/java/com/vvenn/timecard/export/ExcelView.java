package com.vvenn.timecard.export;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vvenn.timecard.entity.TimeCardRecordView;
import com.vvenn.timecard.service.Helper;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

/**
 * 出力エクセルデータmodel
 */
public class ExcelView extends AbstractXlsxView {

    private static final String BASE_FILE_NAME = "勤務表";
    private static final String DOT_XLSX = ".xlsx";
    private static final String FILE_PATH = "static/export/勤務表30分単位.xlsx";

    /**
     * filePathで指定したExcelテンプレートからWorkbookを作成します。
     * このメソッドで返却したWorkbookのオブジェクトが、buildExcelDocumentメソッドの引数として渡されます。
     * 
     * @param model
     * @param request
     * @return workbook
     */
    @Override
    protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {

        Workbook workbook = null;

        try (InputStream inputStream = new ClassPathResource(FILE_PATH).getInputStream()) {
            workbook = WorkbookFactory.create(inputStream);
        } catch (IOException | EncryptedDocumentException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /**
     * 出力するエクセルデータを作成します。
     * 
     * @param model    addStaticAttributeで設定された値を保持するmodel
     * @param workbook createWorkbookの戻り値
     * @param request  httpリクエスト
     * @param response httpレスポンス
     * @throws Exception
     */
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // 出力ファイル名
        String fileName = BASE_FILE_NAME + "_" + model.get("ym") + "_" + model.get("userName");
        fileName += DOT_XLSX;

        // responseヘッダにファイル名を設定
        String encodedFilename = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);

        // シートの選択
        Sheet sheet = workbook.getSheetAt(0);
        Row row = null;

        @SuppressWarnings("unchecked")
        List<TimeCardRecordView> timeCardRecordViews = (List<TimeCardRecordView>) model.get("timeCardRecordViews");

        // セルにデータを設定
        this.getCell(sheet, row, 0, 1).setCellValue(model.get("yearMonth").toString());
        this.getCell(sheet, row, 4, 3).setCellValue(model.get("sectionName").toString());
        this.getCell(sheet, row, 5, 3).setCellValue(model.get("userName").toString());

        int count = 0;
        int totalWorkingHours = 0;
        int totalActualWorkingHours = 0;

        for (int i = 0; i < timeCardRecordViews.size(); i++) {

            // 日をセット
            this.getCell(sheet, row, 11 + i, 1).setCellValue(i + 1);
            // 曜日をセット
            this.getCell(sheet, row, 11 + i, 2).setCellValue(timeCardRecordViews.get(i).getWeekDay());

            // 出勤情報dtoが存在するか
            if (timeCardRecordViews.get(i).getTimeCardRecordDto() != null) {
                // 出勤時間をセット
                this.getCell(sheet, row, 11 + i, 3).setCellValue(
                        Helper.timeFormat(timeCardRecordViews.get(i).getTimeCardRecordDto().getStartTime()));
                // 退勤時間をセット
                this.getCell(sheet, row, 11 + i, 5).setCellValue(
                        Helper.timeFormat(timeCardRecordViews.get(i).getTimeCardRecordDto().getEndTime()));
                // 備考をセット
                this.getCell(sheet, row, 11 + i, 11)
                        .setCellValue(timeCardRecordViews.get(i).getTimeCardRecordDto().getRemarks());
                // 就労時間をセット
                this.getCell(sheet, row, 11 + i, 7)
                        .setCellValue(Helper.timeFormat(timeCardRecordViews.get(i).getWorkingHours()));
                // 実就労時間をセット
                this.getCell(sheet, row, 11 + i, 9)
                        .setCellValue(Helper.timeFormat(timeCardRecordViews.get(i).getActualWorkingHours()));
                count++;
                totalWorkingHours += timeCardRecordViews.get(i).getWorkingHours();
                totalActualWorkingHours += timeCardRecordViews.get(i).getActualWorkingHours();
            }
        }

        // 出勤日数をセット
        this.getCell(sheet, row, 7, 3).setCellValue(count);
        this.getCell(sheet, row, 8, 3).setCellValue(Helper.timeFormat(totalWorkingHours));
        this.getCell(sheet, row, 8, 7).setCellValue(Helper.timeFormat(totalActualWorkingHours));
    }

    /**
     * sheetの行番号、列番号で指定したセルを取得して返却します。
     * 
     * @param sheet    シート
     * @param row      行
     * @param rowIndex 行番号
     * @param colIndex 列番号
     * @return セル
     */
    private Cell getCell(Sheet sheet, Row row, int rowIndex, int colIndex) {
        row = sheet.getRow(rowIndex);
        return row.getCell(colIndex);
    }
}