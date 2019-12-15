package com.optile.recritment.scheduler.controller;

import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.service.JobManagementEngine;
import com.optile.recritment.scheduler.service.JobManagementService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * This Controller is the main scheduler controller. The following APIs enable to:
 * 1. Create a new Job Definition
 * 2. Get a Job Definition
 * 3. Schedule a Job by Id
 * 4. List All Job Definitions.
 */


@RestController
@RequestMapping("/api")
@Log4j2
public class SchedulerController {

    @Autowired
    JobManagementService service;
    @Autowired
    JobManagementEngine engine;

    @GetMapping("/jobs/{id}")
    public JobDefinition getJobDefinition(@PathVariable("id") Long id) {
        log.debug("Fetching Job with id: " + id);
        JobDefinition job = service.getJobDefinition(id);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job id: " + id + " not found!");
        } else {
            log.debug("Fetched Job " + job);
            return job;
        }
    }

    @PostMapping("/jobs/{id}")
    public JobDefinition scheduleJobById(@PathVariable("id") Long id) {
        log.debug("Scheduling Job with id: " + id);
        return engine.scheduleJobById(id);
    }

    @PostMapping("/jobs")
    public JobDefinition createJobDefinition(@RequestBody JobDefinition job) {
        try {
            JobDefinition createdJobDefinition = engine.createNewJobDefinition(job);
            if (createdJobDefinition == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create Job Definition" + job);
            }
            return createdJobDefinition;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to create Job Definition" + job);
        }
    }

    @GetMapping("/jobs")
    public List<JobDefinition> listAllDefinitions() {
        return service.getAllDefinitions();
    }
}
