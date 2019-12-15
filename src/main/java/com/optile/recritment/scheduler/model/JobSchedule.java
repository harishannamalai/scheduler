package com.optile.recritment.scheduler.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.sql.Timestamp;

/**
 * This Data Model represents all the Jobs that are currently scheduled.
 * The Primary Keys are Job ID and Schedule.
 */

@Getter
@Setter
@Entity
@ToString
public class JobSchedule {
    @EmbeddedId
    JobSchedulePrimaryKey primaryKey;

    private Timestamp updateOrCreatedAt;
    private String status;

}
