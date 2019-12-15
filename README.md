# A Simple Scheduler cum Job Management Engine.
-
This Application relies on the features of `java.util.Timer` and `java.util.concurrent.PriorityBlockingQueue`.
1. `Timer` provides the features of Scheduling a `TimerTask` to either run at a specified Time (Schedule)
or at any Fixed Duration repeated Execution.
2. `PriorityBlockingQueue` provides the features of extracting items with a defined priority.

## Application Functions:
The application exposes APIs for
1. Creating a Job Definition.
2. Scheduling a Job by id.
3. Job Execution History.

## Application Working.
The Application has 2 Logical Threads 
1. `SchedulerThread` which looks for Jobs to be scheduled in the next 5 minutes (This can be adjusted).
2. `JobEngineThread`  this looks at the PriorityBlockingQueue and picks jobs based on the Priority.

### Application Flows
1. Create a New Job using the API `POST $hostname:8080/api/jobs`.
2. If the created job schedule is within 5 mins (Configurable) from now, it is added to execution Queue.
3. If the schedule is after 5 mins, it is added to Job Definition Table.
4. `JobEngineTask` picks up jobs to be executed from the execution queue and schedules it to be executed.
5. `JobExecutionTask` runs exactly at the specified time and updates the Job Execution Table after completion of Job.
6. The Job Schedule is cleaned Up.
7. Steps `4 - 6` are run in an infinite loop.

Scheduler Workflow:
1. It runs at every 10 seconds (Configurable), to check for jobs to be executed in the next 5 mins(Configurable).
2. If there are jobs to be scheduled, then it is submitted to Job Engine for Scheduling.

## DATABASE
`H2` an in-memory database is used to store
1. `JOB_DEFINITION` - definition of all jobs.
2. `JOB_EXECUTION` - stores history of all Job Execution, a job can have more than one execution.
3. `JOB_SCHEDULED` - stores information on all jobs that are QUEUED and RUNNING. 