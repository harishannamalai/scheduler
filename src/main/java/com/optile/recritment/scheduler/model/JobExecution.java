package com.optile.recritment.scheduler.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Data model to store the job execution history.
 * Primary Key is a UUID field denoted by executionId.
 */

@Getter
@Setter
@Entity
@ToString
public class JobExecution {
    @Id
    private String executionId;

    private long jobId;
    private String jobName;
    private String groupId;
    private Timestamp schedule;
    private Timestamp queuedAt;
    private Timestamp startAt;
    private Timestamp endAt;
    private String status;
    private String output;
}
