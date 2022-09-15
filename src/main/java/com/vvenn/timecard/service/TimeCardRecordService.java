package com.vvenn.timecard.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.vvenn.timecard.entity.TimeCardRecord;
import com.vvenn.timecard.entity.TimeCardRecordView;
import com.vvenn.timecard.repository.TimeCardRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeCardRecordService {

    SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat eeeSdf = new SimpleDateFormat("EEE", Locale.JAPANESE);
    SimpleDateFormat yyyyMMddSdf = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat yyyyMMSdf = new SimpleDateFormat("yyyyMM");

    private java.sql.Date firstDay;
    private java.sql.Date lastDay;

    @Autowired
    private TimeCardRecordRepository timeCardRecordRepository;

    @Autowired
    private YmParameterService ymParameterService;

    /**
     * DBに勤務表データを登録します。
     * 
     * @param id        勤務表シリアル
     * @param userId    ユーザーシリアル
     * @param dutyDate  勤務日付
     * @param startTime 出勤時間
     * @param endTime   退勤時間
     * @param breakTime 休憩時間
     * @param remarks   備考
     */
    public void addDate(Long id, int userId, java.sql.Date dutyDate, int startTime, int endTime, int breakTime,
            String remarks, int overTime) {
        Timestamp now = Helper.getNowTimestamp();
        timeCardRecordRepository.save(
                new TimeCardRecord(id, userId, dutyDate, startTime, endTime, breakTime, remarks, now, now, overTime));
    }

    /**
     * ユーザーIDと日付の一致したデータを取得し、その勤務表シリアルをもとに物理削除します。
     * 
     * @param userId   ユーザーシリアル
     * @param dutyDate 勤務日付
     */
    public void deleteDate(int userId, java.sql.Date dutyDate) {
        TimeCardRecord timeCardRecord = timeCardRecordRepository.findByUserIdAndDutyDate(userId, dutyDate);
        timeCardRecordRepository.deleteById(timeCardRecord.getId());
    }

    /**
     * 引数として渡されたユーザーIDと、コントローラーからセットされた対象月をもとに、<br>
     * 勤務表データを取得します。
     * 
     * @param id ユーザーID
     * @return 対象ユーザーの対象月の勤務データリスト
     */
    public List<TimeCardRecordView> findTimeCardRecordViewAll(int id) {

        List<TimeCardRecord> timeCardRecords = timeCardRecordRepository
                .findByUserIdAndDutyDateBetweenOrderByDutyDate(id, firstDay, lastDay);
        List<TimeCardRecordView> timeCardRecordViews = new ArrayList<>();

        TimeCardRecordView.totalDate = timeCardRecords.size();
        TimeCardRecordView.totalWorkingHours = 0;
        TimeCardRecordView.totalActualWorkingHours = 0;

        // ループ用カレンダー
        Calendar cal = Calendar.getInstance();
        cal.set(this.ymParameterService.getYear(), this.ymParameterService.getMonth() - 1, 1);
        // 対象月の日数
        int dayCount = cal.getActualMaximum(Calendar.DATE);
        // 祝日をJson形式で取得する
        JsonNode holidayJson = Helper.getHoliday();

        TimeCardRecord timeCardRecord = null;
        String day = "";
        String weekDay = "";
        String holidayName = "";
        int index = 0;
        int workingHours = 0;
        int actualWorkingHours = 0;
        boolean isWorkDay;

        for (int i = 0; i < dayCount; i++, cal.add(Calendar.DAY_OF_MONTH, 1)) {

            // 比較用対象月日付ループ変数
            // 条件一致データのリストインデックス番号
            isWorkDay = false;

            if (index < timeCardRecords.size()) {
                if (yyyyMMddSdf.format(cal.getTime())
                        .equals(yyyyMMddSdf.format(timeCardRecords.get(index).getDutyDate()))) {
                    isWorkDay = true;
                }
            }

            day = this.dateSdf.format(cal.getTime());
            weekDay = this.eeeSdf.format(cal.getTime());
            holidayName = "";

            if (holidayJson.has(day)) {
                weekDay += "･祝";
                holidayName = holidayJson.get(day).asText();
            }

            if (!isWorkDay) {
                timeCardRecord = null;
                workingHours = 0;
                actualWorkingHours = 0;
            } else {
                timeCardRecord = timeCardRecords.get(index);
                workingHours = (timeCardRecords.get(index).getEndTime() - timeCardRecords.get(index).getStartTime());
                actualWorkingHours = (workingHours - timeCardRecords.get(index).getBreakTime());
                TimeCardRecordView.totalWorkingHours += workingHours;
                TimeCardRecordView.totalActualWorkingHours += actualWorkingHours;
                index++;
            }
            TimeCardRecordView timeCardRecordView = new TimeCardRecordView(timeCardRecord, day, weekDay, workingHours,
                    actualWorkingHours, holidayName);
            timeCardRecordViews.add(timeCardRecordView);
        }
        return timeCardRecordViews;
    }

    /**
     * 月初と月末の日付をセットします。
     */
    public void setFirstAndLastDay() {

        Date DayDate = new Date();
        try {
            DayDate = this.yyyyMMSdf.parse(this.ymParameterService.getYm());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date firstDayDate = betweenDate(DayDate, true);
        Date lastDayDate = betweenDate(DayDate, false);

        this.firstDay = new java.sql.Date(firstDayDate.getTime());
        this.lastDay = new java.sql.Date(lastDayDate.getTime());
    }

    /**
     * 月初、月末日をDate型で返します。
     * 
     * @param ym          yyyyMM形式の日付
     * @param firstDayFlg 月初か月末か
     * @return Date型の月初 or 月末日
     */
    private static Date betweenDate(Date ym, boolean firstDayFlg) {
        Calendar cal = Calendar.getInstance();

        // 年月をセットする
        cal.setTime(ym);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = 1;

        if (!firstDayFlg) {
            date = cal.getActualMaximum(Calendar.DATE);
        }

        // 月初 or 月末日を登録
        cal.set(year, month, date);

        return cal.getTime();
    }

    /**
     * デフォルト設定のプルダウン用text,value作成を作成します。
     * 
     * @return ブルダウン用valueのリスト
     */
    public List<Integer> getTimeList() {
        int maxEndTime = Helper.getMaxEndTime();
        int timePeriod = Helper.getTimePeriod();
        IntStream stream = IntStream.range(0, maxEndTime / timePeriod);
        return stream.map(i -> i * timePeriod).boxed().collect(Collectors.toList());
    }

    public int overTimeCheck(int startTime, int endTime, int breakTime, int defaultStartTime, int defaultEndTime,
            int defaultBreakTime) {
        int overTimeNumber = 0;
        if (startTime != defaultStartTime) {
            overTimeNumber += 1;
        }
        if (endTime != defaultEndTime) {
            overTimeNumber += 20;
        }
        if (breakTime != defaultBreakTime) {
            overTimeNumber += 300;
        }
        return overTimeNumber;
    }

}
