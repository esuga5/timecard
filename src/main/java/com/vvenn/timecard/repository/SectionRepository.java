package com.vvenn.timecard.repository;

import com.vvenn.timecard.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository というテーブル操作を定義したインターフェースを継承した、<br>
 * 「sections」テーブルの操作を行うリポジトリのインターフェースです。
 */
@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
}