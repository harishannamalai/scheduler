package com.optile.recritment.scheduler.service;

import com.optile.recritment.scheduler.actions.ActionFactory;
import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.tasks.JobEngineTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.Comparator;
import java.util.Timer;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This is responsible for all of the Job Engine's functions, including
 * 1. Creation of Job Engine Task.
 * 2. Creation of New Jobs, if the Newly created Job is less than the poll period, add it to execution queue.
 * 3. Schedule the Jobs.
 * 4. Stop the Job Engine Task.
 */

@Service
@Log4j2
public class JobManagementEngine {

    @Value("${schedule.check.interval:300000}")
    private long scheduleCheckInterval;
    @Value("${job.engine.name:JobEngine}")
    private String jobEngineName;

    private PriorityBlockingQueue<JobDefinition> executionQueue;
    private volatile boolean started = false;

    @Autowired
    @Qualifier("jobTimer")
    private Timer jobTimer;
    @Autowired
    private ActionFactory actionFactory;

    private Thread jobEngine;
    private JobEngineTask jobEngineTask;

    @Autowired
    JobManagementService service;

    @PostConstruct
    public void init() {
        Comparator<JobDefinition> jobDefinitionComparator = Comparator
                .comparing(JobDefinition::getSchedule)
                .thenComparing(JobDefinition::getSchedule);
        executionQueue = new PriorityBlockingQueue<>(1000, jobDefinitionComparator);

        this.startEngine();
    }

    public JobDefinition scheduleJobById(Long id) {
        JobDefinition job = service.getJobDefinition(id);
        this.scheduleJob(job);
        return job;
    }

    private void startEngine() {
        if (!started) {
            log.info("Starting the Job Management Engine!");
            jobEngineTask = new JobEngineTask();
            jobEngineTask.setActionFactory(actionFactory);
            jobEngineTask.setJobTimer(jobTimer);
            jobEngineTask.setQueue(executionQueue);
            jobEngineTask.setService(service);

            jobEngine = new Thread(jobEngineTask);
            jobEngine.setName(jobEngineName);
            jobEngine.setPriority(Thread.MAX_PRIORITY);
            started = true;
            jobEngine.start();

            log.info("Job Engine has been started!");
        } else {
            log.info("Job Engine has already been started!");
        }
    }

    private void scheduleJob(JobDefinition job) {
        if (service.checkIfAlreadyQueuedOrRunning(job)) {
            log.info("Job already Queued!");
        } else {
            if (job.getSchedule().toInstant().isBefore(Instant.now().plusMillis(scheduleCheckInterval))) {
                log.info("Scheduling Job: " + job);
                executionQueue.add(job);
                log.info("Queued Job for execution! " + job);
            } else {
                log.info("Job Id: " + job.getJobId() + " will be picked by Scheduler Engine!");
            }
        }
    }

    public JobDefinition createNewJobDefinition(JobDefinition jobDefinition) {

        jobDefinition = service.createNewJobDefinition(jobDefinition);

        if (jobDefinition.getSchedule().toInstant().isBefore(Instant.now().plusMillis(scheduleCheckInterval))) {
            this.scheduleJob(jobDefinition);
            log.info("Newly created Job is added to Queue!");
        } else {
            log.info("New Job id: " + jobDefinition.getJobId() + " Will be executed at : " + jobDefinition.getSchedule());
        }
        return jobDefinition;
    }

    @PreDestroy
    public void stopEngine() {
        log.info("Stopping the Job Management Engine");
        jobEngineTask.stop();
        executionQueue.clear();
        started = false;
        log.info("Job Management Engine has been stopped.");
    }
}
