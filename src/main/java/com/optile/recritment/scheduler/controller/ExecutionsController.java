package com.optile.recritment.scheduler.controller;

import com.optile.recritment.scheduler.model.JobExecution;
import com.optile.recritment.scheduler.service.JobManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * This controller deals with all the executions of a Job.
 * This controller can be used to look up all the status of executions and get
 * execution history.
 */

@RestController
@RequestMapping("/api")
public class ExecutionsController {

    @Autowired
    JobManagementService service;

    @GetMapping("/jobs/{id}/history")
    public List<JobExecution> getExecutionHistory(@PathVariable("id") Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Job Id cannot be null!");
        } else {
            List<JobExecution> history = service.getAllExecutions(id);
            if (history.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Executions for id: " + id + " Not found!");
            } else {
                return history;
            }
        }
    }
}
