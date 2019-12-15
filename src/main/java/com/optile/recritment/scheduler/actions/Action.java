package com.optile.recritment.scheduler.actions;

import com.optile.recritment.scheduler.model.JobDefinition;
import com.optile.recritment.scheduler.model.JobExecution;

/**
 * This is a functional Interface which is used for Executing the Jobs.
 * the execute method will be invoked by the processor.
 */

@FunctionalInterface
public interface Action {
    /**
     * This is the method that a processor will call. A processor will be generated for action types
     * which can be HTTP, CMD or any other action which can defined.
     *
     * @param definition
     * @param execution
     * @return
     */
    JobExecution execute(JobDefinition definition, JobExecution execution);
}
