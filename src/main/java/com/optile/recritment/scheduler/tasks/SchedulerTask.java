package com.optile.recritment.scheduler.tasks;

import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.service.JobManagementEngine;
import com.optile.recritment.scheduler.service.JobManagementService;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.TimerTask;

/**
 * This Task Runs as Timer unit, which Runs at specified period and checks to see
 * if there are any jobs to schedule. If there are any jobs to schedule, then it
 * schedules the same.
 */

@Log4j2
public class SchedulerTask extends TimerTask {

    JobManagementEngine engine;
    JobManagementService service;

    public void setEngine(JobManagementEngine engine) {
        this.engine = engine;
    }

    public void setService(JobManagementService service) {
        this.service = service;
    }

    @Override
    public void run() {
        log.debug("Scheduler Job running, Checking for New Jobs!");
        try {
            List<JobDefinition> jobDefinitions = service.findJobsToSchedule();
            if (!jobDefinitions.isEmpty()) {
                log.info("Fetched " + jobDefinitions.size() + " to schedule!");
            }
            jobDefinitions.forEach(j -> engine.scheduleJobById(j.getJobId()));
        } catch (Exception e) {
            log.error("Exception while checking for New Jobs to Schedule!", e);
        }
        log.info("Scheduler finished checking for Jobs!");
    }
}
