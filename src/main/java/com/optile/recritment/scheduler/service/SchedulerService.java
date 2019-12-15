package com.optile.recritment.scheduler.service;

import com.optile.recritment.scheduler.tasks.SchedulerTask;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Timer;

/**
 * This Service is responsible for Running the Scheduler at every specific Interval.
 * This manages the life cycle of the Scheduler Task.
 */

@Service
@Log4j2
public class SchedulerService {

    @Value("${scheduler.initial.delay:0}")
    private long initialDelay;
    @Value("${scheduler.scheduler.run.interval:10000}")
    private long schedulerRunInterval;

    @Autowired
    @Qualifier("schedulerTimer")
    private Timer schedulerTimer;
    @Autowired
    private JobManagementService service;
    @Autowired
    private JobManagementEngine engine;

    private SchedulerTask schedulerTask;

    @PostConstruct
    public void init() {
        log.info("Initializing Scheduler Engine");

        schedulerTask = new SchedulerTask();
        schedulerTask.setEngine(engine);
        schedulerTask.setService(service);

        schedulerTimer.schedule(schedulerTask, initialDelay, schedulerRunInterval);

        log.info("Scheduler Engine is set to Run at " + schedulerRunInterval + " after initial" + schedulerRunInterval);
    }

    @PreDestroy
    public void close() {
        log.info("Closing the Scheduler Engine");
        schedulerTimer.purge();
        schedulerTimer.cancel();
    }
}
