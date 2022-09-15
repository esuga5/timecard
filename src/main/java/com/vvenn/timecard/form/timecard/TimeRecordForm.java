package com.vvenn.timecard.form.timecard;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.vvenn.timecard.service.Helper;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出勤データ登録フォームの入力値をチェックするmodel
 */
@Data
@NoArgsConstructor
public class TimeRecordForm {

    private int id;

    @Pattern(regexp = "^(?!([02468][1235679]|[13579][01345789])00-02-29)(([0-9]{4}-(01|03|05|07|08|10|12)-(0[1-9]|[12][0-9]|3[01]))|([0-9]{4}-(04|06|09|11)-(0[1-9]|[12][0-9]|30))|([0-9]{4}-02-(0[1-9]|1[0-9]|2[0-8]))|([0-9]{2}([02468][048]|[13579][26])-02-29))$", message = "日付が指定可能な日付の範囲外です。")
    private String dutyDate;

    @Size(max = 30, message = "備考欄は30文字以内で入力して下さい。")
    private String remarks;

    private int startTime;
    private int endTime;
    private int breakTime;

    @AssertTrue(message = "出勤時間が指定可能な時間の範囲外です。")
    public boolean isCheckStartTimeRange() {
        return Helper.checkTimeRange(this.startTime, false);
    }

    @AssertTrue(message = "退勤時間が指定可能な時間の範囲外です。")
    public boolean isCheckEndTimeRange() {
        return Helper.checkTimeRange(this.endTime, true);
    }

    @AssertTrue(message = "休憩時間が指定可能な時間の範囲外です。")
    public boolean isCheckBreakTimeRange() {
        return Helper.checkTimeRange(this.breakTime, false);
    }

    @AssertTrue(message = "退勤時間が出勤時間以前となっています。")
    public boolean isCheckEndTime() {
        return Helper.checkEndTime(this.startTime, this.endTime);
    }

    @AssertTrue(message = "実就労時間が0以下となっています。")
    public boolean isCheckTotalWorkingHour() {
        return Helper.checkTotalWorkingHour(this.startTime, this.endTime, this.breakTime);
    }
}