package com.optile.recritment.scheduler.repository;

import com.optile.recritment.scheduler.model.JobDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface JobDefinitionRepository extends JpaRepository<JobDefinition, Long> {
    @Query("from JobDefinition j where j.schedule between :start and :end and " +
            "j.jobId not in (select s.primaryKey.jobId from JobSchedule s where s.primaryKey.jobId = j.jobId and " +
            "s.primaryKey.schedule = j.schedule)")
    List<JobDefinition> findJobsToSchedule(Timestamp start, Timestamp end);
}
