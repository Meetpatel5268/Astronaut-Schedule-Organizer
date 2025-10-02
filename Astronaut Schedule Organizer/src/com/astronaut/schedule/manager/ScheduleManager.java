package com.astronaut.schedule.manager;

import com.astronaut.schedule.exception.TaskConflictException;
import com.astronaut.schedule.exception.TaskNotFoundException;
import com.astronaut.schedule.model.Priority;
import com.astronaut.schedule.model.Task;
import com.astronaut.schedule.observer.Observer;
import com.astronaut.schedule.observer.Subject;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ScheduleManager implements Subject {
    private static ScheduleManager instance;
    private final List<Task> tasks;
    private final List<Observer> observers;
    private static final Logger LOGGER = Logger.getLogger(ScheduleManager.class.getName());

    private ScheduleManager() {
        tasks = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public static synchronized ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    // Helper method to check for time conflicts, excluding a specific task ID (for editing)
    private boolean hasConflict(Task taskToCheck, int idToExclude) {
        for (Task existingTask : tasks) {
            if (existingTask.getId() == idToExclude) {
                continue; // Skip checking against itself
            }
            if (taskToCheck.getStartTime().isBefore(existingTask.getEndTime()) && taskToCheck.getEndTime().isAfter(existingTask.getStartTime())) {
                String conflictMessage = String.format("Error: Task conflicts with existing task \"%s\".", existingTask);
                notifyObservers(conflictMessage);
                return true;
            }
        }
        return false;
    }

    public void addTask(Task newTask) throws TaskConflictException {
        if (hasConflict(newTask, -1)) { // -1 means don't exclude any task
            throw new TaskConflictException("Task conflicts with an existing task.");
        }
        tasks.add(newTask);
        LOGGER.info("Task added successfully: " + newTask.getDescription());
    }

    public void removeTask(int id) throws TaskNotFoundException {
        boolean removed = tasks.removeIf(task -> task.getId() == id);
        if (!removed) {
            throw new TaskNotFoundException("Error: Task with ID " + id + " not found.");
        }
        LOGGER.info("Task with ID " + id + " removed successfully.");
    }

    public Optional<Task> findTaskById(int id) {
        return tasks.stream().filter(task -> task.getId() == id).findFirst();
    }

    public void editTask(int id, String newDescription, LocalTime newStartTime, LocalTime newEndTime, Priority newPriority) throws TaskNotFoundException, TaskConflictException {
        Task task = findTaskById(id).orElseThrow(() -> new TaskNotFoundException("Error: Task with ID " + id + " not found."));

        // Create a temporary task with new details to check for conflicts
        Task tempTask = new Task(newDescription, newStartTime, newEndTime, newPriority);
        if (hasConflict(tempTask, id)) {
            throw new TaskConflictException("Edited task conflicts with an existing task.");
        }

        // If no conflict, apply the changes
        task.setDescription(newDescription);
        task.setStartTime(newStartTime);
        task.setEndTime(newEndTime);
        task.setPriority(newPriority);
        LOGGER.info("Task with ID " + id + " edited successfully.");
    }

    public void markTaskAsCompleted(int id, boolean isCompleted) throws TaskNotFoundException {
        Task task = findTaskById(id).orElseThrow(() -> new TaskNotFoundException("Error: Task with ID " + id + " not found."));
        task.setCompleted(isCompleted);
        LOGGER.info("Task ID " + id + " marked as " + (isCompleted ? "completed." : "pending."));
    }

    public List<Task> getTasks() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByPriority(Priority priority) {
        return tasks.stream()
                .filter(task -> task.getPriority() == priority)
                .sorted(Comparator.comparing(Task::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        observers.forEach(observer -> observer.update(message));
    }
}