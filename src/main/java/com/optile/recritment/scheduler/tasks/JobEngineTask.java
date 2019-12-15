package com.optile.recritment.scheduler.tasks;

import com.optile.recritment.scheduler.actions.ActionFactory;
import com.optile.recritment.scheduler.constants.JobStatus;
import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.model.JobExecution;
import com.optile.recritment.scheduler.service.JobManagementService;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This task is the Job Engine's Task. Which runs in a infinite loop checking the Priority
 * Queue for the next job to schedule.
 * <p>
 * As and when the Job is extracted from the Queue, it submits to the jobTimer, which
 * executes the job at the specified time.
 */

@Log4j2
public class JobEngineTask implements Runnable {

    private volatile boolean runEngine = true;

    private PriorityBlockingQueue<JobDefinition> queue;
    private Timer jobTimer;
    private JobManagementService service;
    private ActionFactory actionFactory;

    public void setQueue(PriorityBlockingQueue<JobDefinition> queue) {
        this.queue = queue;
    }

    public void setJobTimer(Timer jobTimer) {
        this.jobTimer = jobTimer;
    }

    public void setService(JobManagementService service) {
        this.service = service;
    }

    public void setActionFactory(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    public void stop() {
        log.info("Stopping Job Engine");
        runEngine = false;
    }

    /**
     * This method runs in a Loop, using Priority Blocking Queue, we wait for any Job is asked to be scheduled.
     * As soon as a Job is inserted into the Queue, based on the schedule and priority, jobs are queued.
     */

    @Override
    public void run() {
        log.info("Starting the Job Management Engine at " + new Timestamp(System.currentTimeMillis()));

        while (runEngine) {
            try {
                JobDefinition jobDefinition = queue.take();

                JobExecutorTask task = new JobExecutorTask();
                JobExecution execution = service.createNewJobExecution(jobDefinition);
                service.updateJobScheduleAndExecution(execution, JobStatus.QUEUED);

                task.setAction(actionFactory.getAction(jobDefinition.getProcessor()));
                task.setExecution(execution);
                task.setJob(jobDefinition);
                task.setService(service);

                /*
                Use the Job Timer to schedule the job.
                 */

                jobTimer.schedule(task, jobDefinition.getSchedule());

                log.info("Job has been scheduled to Execute and submitted to Engine");
            } catch (Exception e) {
                log.error("Error while scheduling job ", e);
            }
        }

        log.info("Job Management Engine as been stopped at " + new Timestamp(System.currentTimeMillis()));
    }
}
