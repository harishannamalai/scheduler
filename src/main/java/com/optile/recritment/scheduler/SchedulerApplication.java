package com.optile.recritment.scheduler;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@SpringBootApplication
@Log4j2
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }

    @PreDestroy
    public void onExit() {
        log.info("Stopping the Application!");

        log.info("Application Stop Complete.");
    }


}
