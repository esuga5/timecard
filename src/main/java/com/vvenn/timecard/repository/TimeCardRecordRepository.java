package com.vvenn.timecard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.vvenn.timecard.entity.TimeCardRecord;

/**
 * JpaRepository というテーブル操作を定義したインターフェースを継承した、<br>
 * 「time_card_records」テーブルの操作を行うリポジトリのインターフェースです。
 */
@Repository
public interface TimeCardRecordRepository extends JpaRepository<TimeCardRecord, Long> {

    /**
     * 選択されたユーザー、年月と一致する勤務データを取得します。
     * 
     * @param userId   ユーザーシリアル
     * @param firstDay 月初日
     * @param lastDay  月末日
     * @return
     */
    List<TimeCardRecord> findByUserIdAndDutyDateBetweenOrderByDutyDate(int userId, java.sql.Date firstDay,
            java.sql.Date lastDay);

    /**
     * 選択されたユーザー、勤務日付と一致する勤務データを取得します。
     * 
     * @param userId   ユーザーシリアル
     * @param dutyDate 勤務日付
     * @return
     */
    TimeCardRecord findByUserIdAndDutyDate(int userId, java.sql.Date dutyDate);
}