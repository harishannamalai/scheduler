package com.optile.recritment.scheduler.config;

import com.optile.recritment.scheduler.actions.Action;
import com.optile.recritment.scheduler.constants.JobStatus;
import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.model.JobExecution;
import com.optile.recritment.scheduler.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;

/**
 * Define all the processors. A processor definition can be as below. The only requirement
 * for processor is to use the Job Definition to get all the information.
 */

@Configuration
@Log4j2
public class Processors {

    /**
     * A simple shell processor. Which will execute a shell command.
     *
     * @return
     */

    @Bean("shellProcessor")
    public Action shellProcessor() {
        return new Action() {
            @Override
            public JobExecution execute(JobDefinition job, JobExecution execution) {
                try {
                    Timestamp start = new Timestamp(System.currentTimeMillis());
                    execution.setStartAt(start);
                    log.info("Running Job " + job.getJobName() + " scheduled At: " + job.getSchedule()
                            + " actual start: " + start);
                    Process process = Runtime.getRuntime().exec(job.getCmd());
                    process.waitFor();
                    if (process.exitValue() == 0) {
                        execution.setStatus(JobStatus.SUCCESS.name());
                        execution.setOutput(Utils.getStringFromStream(process.getInputStream()));
                    } else {
                        execution.setStatus(JobStatus.FAILED.name());
                        execution.setOutput(Utils.getStringFromStream(process.getErrorStream()));
                    }
                } catch (Exception e) {
                    log.error(e);
                    execution.setStatus(JobStatus.FAILED.name());
                    execution.setOutput(e.getMessage());
                } finally {
                    execution.setEndAt(new Timestamp(System.currentTimeMillis()));
                    log.info("Running Job completed with Status: " + execution.getStatus());
                }
                return execution;
            }
        };
    }
}
