package com.optile.recritment.scheduler.service;


import com.optile.recritment.scheduler.constants.JobStatus;
import com.optile.recritment.scheduler.constants.SchedulerConstants;
import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.model.JobExecution;
import com.optile.recritment.scheduler.model.JobSchedule;
import com.optile.recritment.scheduler.model.JobSchedulePrimaryKey;
import com.optile.recritment.scheduler.repository.JobDefinitionRepository;
import com.optile.recritment.scheduler.repository.JobExecutionRepository;
import com.optile.recritment.scheduler.repository.JobScheduleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This Service class provides services for all Job Management, such as creation of
 * job definition, job schedule, job execution history etc.
 */
@Service
@Log4j2
public class JobManagementService {

    @Value("${schedule.check.interval:300000}")
    private long scheduleCheckInterval;

    @Autowired
    JobDefinitionRepository jobDefinitionRepository;
    @Autowired
    JobExecutionRepository jobExecutionRepository;
    @Autowired
    JobScheduleRepository jobScheduleRepository;

    public List<JobExecution> getAllExecutions(Long jobId) {
        log.debug("Getting execution history for job id: " + jobId);
        if (jobId != null) {
            return jobExecutionRepository.findAllByJobId(jobId);
        } else {
            log.debug("No execution exists for job id: " + jobId);
            return Collections.emptyList();
        }
    }

    public boolean checkIfJobDefinitionAlreadyExists(JobDefinition job) {
        if (job.getJobId() == null) {
            return false;
        } else {
            return jobDefinitionRepository.existsById(job.getJobId());
        }
    }

    public JobDefinition updateJobDefinition(JobDefinition job) {
        if (job.getJobId() != null) {
            return jobDefinitionRepository.save(job);
        } else {
            throw new IllegalArgumentException("Job Id is null, cannot perform update!");
        }
    }

    public JobDefinition getJobDefinition(Long id) {
        return jobDefinitionRepository.findById(id).orElse(null);
    }

    public boolean checkIfAlreadyQueuedOrRunning(JobDefinition job) {
        JobSchedulePrimaryKey key = new JobSchedulePrimaryKey(job.getJobId(), job.getSchedule());
        JobSchedule schedule = jobScheduleRepository.findById(key).orElse(null);
        if (schedule == null) {
            return false;
        } else {
            return schedule.getStatus().equalsIgnoreCase(JobStatus.QUEUED.name()) ||
                    schedule.getStatus().equalsIgnoreCase(JobStatus.RUNNING.name());
        }
    }

    public JobDefinition createNewJobDefinition(JobDefinition job) {

        if (job.getJobId() != null) {
            log.error("Cannot create a new Job Definition with assigned Job Definition!");
            throw new IllegalArgumentException("Cannot specify Job Id!");
        } else if (this.checkIfJobDefinitionAlreadyExists(job)) {
            log.error("Cannot create a Definition, Already Job Id exists!");
            throw new IllegalArgumentException("Cannot create Job Definition, already exists!");
        } else {
            return jobDefinitionRepository.save(job);
        }
    }

    public JobExecution createNewJobExecution(JobDefinition job) {
        JobExecution execution = new JobExecution();
        BeanUtils.copyProperties(job, execution, SchedulerConstants.EXECUTION_ID);
        execution.setExecutionId(UUID.randomUUID().toString());
        log.debug("Created new execution: " + execution);
        return execution;
    }

    public JobExecution updateJobScheduleAndExecution(JobExecution execution, JobStatus status) {
        switch (status) {
            case FAILED:
            case SUCCESS:
                execution.setStatus(status.name());
                execution.setEndAt(new Timestamp(System.currentTimeMillis()));
                this.deleteSchedule(execution);
                break;
            case QUEUED:
                execution.setStatus(status.name());
                execution.setQueuedAt(new Timestamp(System.currentTimeMillis()));
                this.createSchedule(execution, status);
                break;
            case RUNNING:
                execution.setStatus(status.name());
                execution.setEndAt(new Timestamp(System.currentTimeMillis()));
                this.updateSchedule(execution, status);
                break;
            default:
                log.error("Unknown Job Status!" + status.name());
        }
        return jobExecutionRepository.save(execution);
    }

    public void deleteSchedule(JobExecution execution) {
        JobSchedulePrimaryKey key = new JobSchedulePrimaryKey(execution.getJobId(), execution.getSchedule());
        JobSchedule schedule = jobScheduleRepository.findById(key).orElse(null);
        if (schedule != null) {
            jobScheduleRepository.delete(schedule);
            log.debug("Schedule Deleted! " + schedule);
        } else {
            log.warn("Unable to delete Schedule for execution: " + execution);
        }
    }

    public void updateSchedule(JobExecution execution, JobStatus status) {
        JobSchedulePrimaryKey key = new JobSchedulePrimaryKey(execution.getJobId(), execution.getSchedule());
        JobSchedule schedule = jobScheduleRepository.findById(key).orElse(null);
        if (schedule != null) {
            jobScheduleRepository.save(schedule);
            schedule.setStatus(status.name());
            schedule.setUpdateOrCreatedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            log.warn("Unable to Update Job Schedule for Execution : " + execution);
        }
    }

    public JobSchedule createSchedule(JobExecution execution, JobStatus status) {
        JobSchedulePrimaryKey key = new JobSchedulePrimaryKey(execution.getJobId(), execution.getSchedule());
        JobSchedule schedule = new JobSchedule();
        schedule.setPrimaryKey(key);
        schedule.setStatus(status.name());
        schedule.setUpdateOrCreatedAt(new Timestamp(System.currentTimeMillis()));
        log.info("Creating new Job Schedule: " + schedule);
        return jobScheduleRepository.save(schedule);
    }

    public List<JobDefinition> findJobsToSchedule() {
        Timestamp start = new Timestamp(System.currentTimeMillis());
        Timestamp end = new Timestamp(System.currentTimeMillis() + scheduleCheckInterval);
        log.info("Checking for Jobs between: " + start + " and " + end);
        return jobDefinitionRepository.findJobsToSchedule(start, end);
    }

    public List<JobDefinition> getAllDefinitions() {
        return jobDefinitionRepository.findAll();
    }

}
