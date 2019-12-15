package com.optile.recritment.scheduler.repository;

import com.optile.recritment.scheduler.model.JobSchedule;
import com.optile.recritment.scheduler.model.JobSchedulePrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobScheduleRepository extends JpaRepository<JobSchedule, JobSchedulePrimaryKey> {
}
