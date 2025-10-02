package com.astronaut.schedule.factory;

import com.astronaut.schedule.model.Priority;
import com.astronaut.schedule.model.Task;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

public class TaskFactory {

    public static final DateTimeFormatter FLEXIBLE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("[HH:mm][H:mm]")
            .toFormatter();

    public static Task createTask(String description, String startTimeStr, String endTimeStr, String priorityStr) throws DateTimeParseException, IllegalArgumentException {
        LocalTime startTime = LocalTime.parse(startTimeStr, FLEXIBLE_TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse(endTimeStr, FLEXIBLE_TIME_FORMATTER);

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Error: End time must be after start time.");
        }

        // Convert priority string to enum, handling potential invalid values gracefully
        try {
            Priority priority = Priority.valueOf(priorityStr.toUpperCase());
            return new Task(description, startTime, endTime, priority);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error: Invalid priority. Please use High, Medium, or Low.");
        }
    }
}