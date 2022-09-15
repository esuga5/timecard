package com.vvenn.timecard.form.timecard;

import javax.validation.constraints.AssertTrue;

import com.vvenn.timecard.service.Helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * デフォルト設定フォームの入力値をチェックするmodel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultTimeForm {

    private int defaultStartTime;
    private int defaultEndTime;
    private int defaultBreakTime;

    @AssertTrue(message = "出勤時間が指定可能な時間の範囲外です。")
    public boolean isCheckStartTimeRange() {
        return Helper.checkTimeRange(this.defaultStartTime, false);
    }

    @AssertTrue(message = "退勤時間が指定可能な時間の範囲外です。")
    public boolean isCheckEndTimeRange() {
        return Helper.checkTimeRange(this.defaultEndTime, true);
    }

    @AssertTrue(message = "休憩時間が指定可能な時間の範囲外です。")
    public boolean isCheckBreakTimeRange() {
        return Helper.checkTimeRange(this.defaultBreakTime, false);
    }

    @AssertTrue(message = "退勤時間が出勤時間以前となっています。")
    public boolean isCheckEndTime() {
        return Helper.checkEndTime(this.defaultStartTime, this.defaultEndTime);
    }

    @AssertTrue(message = "実就労時間が0以下となっています。")
    public boolean isCheckTotalWorkingHour() {
        return Helper.checkTotalWorkingHour(this.defaultStartTime, this.defaultEndTime, this.defaultBreakTime);
    }
}