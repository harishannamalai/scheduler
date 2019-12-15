package com.optile.recritment.scheduler.error;

/**
 * This Error is thrown when a Specified Action type/Action Mapping is not found in application Context.
 */

public class InvalidActionException extends RuntimeException {
    public InvalidActionException(String message) {
        super(message);
    }

    public InvalidActionException(String message, Exception e) {
        super(message, e);
    }
}
