package com.vvenn.timecard.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvenn.timecard.config.TimeCardConfig;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class Helper {

    @Getter
    private static int maxEndTime;

    @Getter
    private static int timePeriod;

    public Helper(TimeCardConfig config) {
        Helper.maxEndTime = config.getMaxEndTime();
        Helper.timePeriod = config.getTimePeriod();
    }

    public static boolean checkTimeRange(int time, boolean isOffset) {
        int maxEndTime = Helper.getMaxEndTime();
        int timePeriod = Helper.getTimePeriod();
        int offset = isOffset ? 0 : timePeriod;
        return ((time >= timePeriod - offset) && (time <= maxEndTime - offset));
    }

    /**
     * 終了時刻が開始時刻を超過しているか判定
     * 
     * @param startTime 開始時刻（分）
     * @param endTime   終了時刻（分）
     * @return boolean 終了時刻が開始時刻を超過しているか
     */
    public static boolean checkEndTime(int startTime, int endTime) {
        return (startTime < endTime);
    }

    /**
     * 開始時刻～終了時刻が休憩時間を超過しているか判定
     * 
     * @param startTime 開始時刻（分）
     * @param endTime   終了時刻（分）
     * @param breakTime 休憩時間（分）
     * @return boolean 開始時刻～終了時刻が休憩時間を超過しているか
     */
    public static boolean checkTotalWorkingHour(int startTime, int endTime, int breakTime) {
        return ((endTime - startTime) > breakTime);
    }

    /**
     * 分を時:分に変換する
     * 
     * @param minutes
     * @return String
     */
    public static String timeFormat(int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }

    /**
     * java.sql.TimestampをISO_LOCAL_DATE形式の文字列（yyyy-mm-dd）へ変換する
     * 
     * @param timestamp
     * @return String
     */
    public static String timestampToDateString(Timestamp timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return formatter.format(timestamp.toLocalDateTime());
    }

    /**
     * ISO_LOCAL_DATE形式の文字列（yyyy-mm-dd）をjava.sql.Timestampへ変換する
     * 
     * @param dateString
     * @return Timestamp
     */
    public static Timestamp dateStringToTimestamp(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDateTime dateTime = LocalDate.parse(dateString, formatter).atStartOfDay();
        return Timestamp.valueOf(dateTime);
    }

    /**
     * 現在日時のjava.sql.Timestampを取得する
     * 
     * @return Timestamp
     */
    public static Timestamp getNowTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    /**
     * 当年と前後一年分の祝日をJson形式で取得します。
     * 
     * @return Json形式の祝日データ
     */
    public static JsonNode getHoliday() {
        String result = "";
        JsonNode root = null;
        try {
            URL url = new URL("https://holidays-jp.github.io/api/v1/date.json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.connect(); // URL接続
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String tmp = "";

            while ((tmp = in.readLine()) != null) {
                result += tmp;
            }

            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(result);
            in.close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

}
