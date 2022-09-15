package com.vvenn.timecard.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.vvenn.timecard.entity.CsvExport;
import com.vvenn.timecard.entity.VTimeCardRecord;
import com.vvenn.timecard.repository.VTimeCardRecordsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.vvenn.timecard.entity.TimeCardRecordView;
import com.vvenn.timecard.entity.User;
import com.vvenn.timecard.export.ExcelView;

@Service
public class ExportService {

    @Autowired
    VTimeCardRecordsRepository vTimeCardRecordsRepository;

    private String ym;

    /**
     * 受け取った年月を元に、CSVファイルを作成します。
     *
     * @param ym ExportControllerより受け取った年月データ
     * @return ResponseEntity<> :CSVファイル出力処理
     * @throws JsonProcessingException      CSV作成時エラー
     * @throws UnsupportedEncodingException 文字コードのエンコード実行エラー
     * @throws ParseException               SimpleDateFormat実行時エラー
     */
    public ResponseEntity<byte[]> createCsv(Optional<String> ym)
            throws JsonProcessingException, UnsupportedEncodingException, ParseException {

        // VTimeCardRecordsRepositoryに対して選択期間で取得
        if (ym.isPresent()) {
            this.ym = ym.get();
        } else {
            this.ym = "";
        }

        // 年を取得
        String yearString = this.ym.substring(0, 4);
        int year = Integer.parseInt(yearString);

        // 月を取得
        String monthString = this.ym.substring(4, 6);
        int month = Integer.parseInt(monthString);

        // 検索範囲
        LocalDate localStartDate = LocalDate.of(year, month, 1);
        LocalDate localEndDate = localStartDate.with(TemporalAdjusters.lastDayOfMonth());

        // Date 型に変換
        Date startDate = Date.valueOf(localStartDate);
        Date endDate = Date.valueOf(localEndDate);

        // 「VTimeCardRecord」リストを作成
        List<VTimeCardRecord> vTimeCardRecords = vTimeCardRecordsRepository
                .findByDutyDateBetweenOrderByDutyDateAscSectionCodeAscUserIdAsc(startDate, endDate);

        // CSV出力対象データ
        List<CsvExport> csvExports = new ArrayList<CsvExport>();

        vTimeCardRecords.stream().forEach(record -> {
            csvExports.add(new CsvExport(record.getId(), record.getSectionCode(), record.getSectionName(),
                    record.getUserId(), record.getUserName(), record.getDutyDate(),
                    Helper.timeFormat(record.getStartTime()), Helper.timeFormat(record.getEndTime()),
                    Helper.timeFormat(record.getBreakTime()), Helper.timeFormat(record.getWorkingHours()),
                    Helper.timeFormat(record.getActualWorkingHours()), record.getRemarks(), record.getCreatedAt()));
        });

        // CSVマッパー
        CsvMapper mapper = new CsvMapper();
        mapper.configure(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, true);
        // CSVヘッダ
        CsvSchema schema = mapper.schemaFor(CsvExport.class).withHeader();
        String csv = mapper.writer(schema).writeValueAsString(csvExports);

        // ファイル名
        HttpHeaders header = new HttpHeaders();
        String fileName = yearString + "年" + monthString + "月";
        String fileValue = UriUtils.encode(fileName, StandardCharsets.UTF_8.name());
        header.setContentDispositionFormData("filename", fileValue + ".csv");

        // 文字コードをSJISに変換
        return new ResponseEntity<>(csv.getBytes("SJIS"), header, HttpStatus.OK);

    }

    /**
     * 出力するエクセルデータを作成します。
     * 
     * @param excelView           エクセルデータmodel
     * @param user                ユーザーエンティティ
     * @param timeCardRecordViews 勤務データリスト
     * @param ym                  年月
     * @return 勤務データが反映されたエクセルデータmodel
     */
    public ExcelView getXlsxData(ExcelView excelView, User user, List<TimeCardRecordView> timeCardRecordViews,
            Optional<String> ym) {

        String yearMonth = ym.isPresent() ? ym.get() : YearMonth.now().format(DateTimeFormatter.ofPattern("uuuuMM"));
        StringBuilder sb = new StringBuilder(yearMonth);
        // Excel出力用に「yyyy/MM」に変換
        sb.insert(4, "/");

        excelView.addStaticAttribute("ym", yearMonth);
        excelView.addStaticAttribute("yearMonth", new String(sb));
        excelView.addStaticAttribute("userName", user.getDisplayName());
        excelView.addStaticAttribute("sectionName", user.getSection().getSectionName());
        excelView.addStaticAttribute("timeCardRecordViews", timeCardRecordViews);

        return excelView;
    }
}