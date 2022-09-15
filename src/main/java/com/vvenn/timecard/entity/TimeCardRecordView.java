package com.vvenn.timecard.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * timecard.html用のview model
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeCardRecordView {

    /** time_card_records DTO */
    private TimeCardRecord timeCardRecordDto;
    /** 日付 */
    private String day;
    /** 曜日 */
    private String weekDay;
    /** 就労時間 */
    private int workingHours;
    /** 実就労時間 */
    private int actualWorkingHours;
    /** 祝日名 */
    private String holidayName;

    public static int totalDate = 0;

    public static int totalWorkingHours = 0;

    public static int totalActualWorkingHours = 0;

}