package com.astronaut.schedule.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {
    // A thread-safe way to generate unique IDs for tasks
    private static final AtomicInteger idGenerator = new AtomicInteger(1);

    private final int id;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private Priority priority;
    private boolean isCompleted;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public Task(String description, LocalTime startTime, LocalTime endTime, Priority priority) {
        this.id = idGenerator.getAndIncrement(); // Assign a unique, auto-incrementing ID
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.isCompleted = false; // Tasks are pending by default
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // --- Setters for Edit Functionality ---
    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    @Override
    public String toString() {
        String status = isCompleted ? "COMPLETED" : "PENDING";
        return String.format("ID: %d | %s - %s | %s [%s] - %s",
                id,
                startTime.format(TIME_FORMATTER),
                endTime.format(TIME_FORMATTER),
                description,
                priority,
                status);
    }
}