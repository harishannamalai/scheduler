package com.optile.recritment.scheduler.tasks;

import com.optile.recritment.scheduler.actions.Action;
import com.optile.recritment.scheduler.constants.JobStatus;
import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.model.JobExecution;
import com.optile.recritment.scheduler.service.JobManagementService;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.TimerTask;

/**
 * This is the Task which is created for every Job and it runs at the specified "Schedule"
 * It invokes execute method of action.
 * The Implementation of action must handle the execution logic for each Job Type.
 */


@Setter
@Log4j2
public class JobExecutorTask extends TimerTask {

    private JobDefinition job;
    private JobExecution execution;
    private JobManagementService service;
    private Action action;

    @Override
    public void run() {
        log.info("Running Job " + job.getJobId() + " scheduled at " + job.getSchedule());
        execution.setStartAt(new Timestamp(System.currentTimeMillis()));
        service.updateJobScheduleAndExecution(execution, JobStatus.RUNNING);

        try {
            execution = action.execute(job, execution);
            execution.setEndAt(new Timestamp(System.currentTimeMillis()));
            service.updateJobScheduleAndExecution(execution, JobStatus.SUCCESS);
        } catch (Exception e) {
            execution.setEndAt(new Timestamp(System.currentTimeMillis()));
            service.updateJobScheduleAndExecution(execution, JobStatus.FAILED);
        }

        log.info("Completed Running job id: " + job.getJobId() + " at " + execution.getEndAt());
    }
}
