package com.vvenn.timecard.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * time_card_recordsと紐づいたエンティティ
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_card_records")
public class TimeCardRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "duty_date", nullable = false)
    private Date dutyDate;

    @Column(name = "start_time", nullable = false)
    private int startTime;

    @Column(name = "end_time", nullable = false)
    private int endTime;

    @Column(name = "break_time", nullable = false)
    private int breakTime;

    @Column(name = "remarks", length = 40, nullable = true)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    @Column(name = "over_time", nullable = false)
    private int overTime;
}