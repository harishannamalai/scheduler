package com.optile.recritment.scheduler.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * This is the Job Definition Data model.
 * 1. Job Id is the Primary Key.
 */

@Getter
@Setter
@Entity
@ToString
public class JobDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;
    private String jobName;
    private String groupId;
    private Timestamp schedule;
    private short priority;
    private String processor;
    private String cmd;
    private String node;

}
