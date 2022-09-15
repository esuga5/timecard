package com.vvenn.timecard.repository;

import java.sql.Date;
import java.util.List;

import com.vvenn.timecard.entity.VTimeCardRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 「v_time_card_records」ビューの操作を行う
 */
@Repository
public interface VTimeCardRecordsRepository extends JpaRepository<VTimeCardRecord, Long> {

    /**
     * v_time_card_recordsビューから指定範囲のデータリストを取得します。
     * 
     * @param start 月初日
     * @param end   月末日
     * @return VTimeCardRecordリスト
     */
    public List<VTimeCardRecord> findByDutyDateBetweenOrderByDutyDateAscSectionCodeAscUserIdAsc(Date start, Date end);
}