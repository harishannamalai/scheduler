package com.optile.recritment.scheduler.model;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Primary Key for Job Schedule.
 */

@Embeddable
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class JobSchedulePrimaryKey implements Serializable {

    public JobSchedulePrimaryKey() {
        // no args constructor required by hibernate
    }

    @NonNull
    private Long jobId;
    @NonNull
    private Timestamp schedule;
}
