package com.optile.recritment.scheduler.repository;

import com.optile.recritment.scheduler.model.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecutionRepository extends JpaRepository<JobExecution, String> {
    List<JobExecution> findAllByJobId(Long jobId);
}
