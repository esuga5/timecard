package com.vvenn.timecard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "timecard.constants")
@Getter
@Setter
public class TimeCardConfig {
    private int maxEndTime;
    private int timePeriod;
}