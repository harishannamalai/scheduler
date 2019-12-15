package com.optile.recritment.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Timer;

/**
 * Timer configurations. There are two timer configurations, one for Scheduler
 * and one for Job Executions.
 */

@Configuration
public class TimerConfig {
    @Bean("schedulerTimer")
    public Timer getSchedulerTimer() {
        return new Timer("SchedulerTimer");
    }

    @Bean("jobTimer")
    public Timer getJobTimer() {
        return new Timer("JobTimer");
    }
}
