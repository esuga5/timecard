package com.vvenn.timecard.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 「v_time_card_records」ビューと紐づいたエンティティ
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "v_time_card_records")
public class VTimeCardRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    private Long id;

    @Column(name = "section_code", nullable = false)
    private String sectionCode;

    @Column(name = "section_name", nullable = false)
    private String sectionName;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "duty_date", nullable = false)
    private Date dutyDate;

    @Column(name = "start_time", nullable = false)
    private int startTime;

    @Column(name = "end_time", nullable = false)
    private int endTime;

    @Column(name = "break_time", nullable = false)
    private int breakTime;

    @Column(name = "working_hours", nullable = false)
    private int workingHours;

    @Column(name = "actual_working_hours", nullable = false)
    private int actualWorkingHours;

    @Column(nullable = false)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
}