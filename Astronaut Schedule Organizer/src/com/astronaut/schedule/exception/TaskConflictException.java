package com.astronaut.schedule.exception;

public class TaskConflictException extends Exception {
    public TaskConflictException(String message) {
        super(message);
    }
}