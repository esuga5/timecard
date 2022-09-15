package com.vvenn.timecard.entity;

import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CSVエクスポートファイルのデータmodel
 */
@JsonPropertyOrder({ "ID", "部門コード", "部門名称", "ユーザーID", "ユーザー名", "勤務日付", "出勤時間", "退勤時間", "休憩時間", "就労時間", "実就労時間", "備考",
        "登録日時" })
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CsvExport {

    @JsonProperty("ID")
    private Long id;

    @JsonProperty("部門コード")
    private String sectionCode;

    @JsonProperty("部門名称")
    private String sectionName;

    @JsonProperty("ユーザーID")
    private int userId;

    @JsonProperty("ユーザー名")
    private String userName;

    @JsonProperty("勤務日付")
    @JsonFormat(pattern = "yyyy/MM/dd", timezone = "Asia/Tokyo")
    private Date dutyDate;

    @JsonProperty("出勤時間")
    private String startTime;

    @JsonProperty("退勤時間")
    private String endTime;

    @JsonProperty("休憩時間")
    private String breakTime;

    @JsonProperty("就労時間")
    private String workingHours;

    @JsonProperty("実就労時間")
    private String actualWorkingHours;

    @JsonProperty("備考")
    private String remarks;

    @JsonProperty("登録日時")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
    private Timestamp createdAt;
}