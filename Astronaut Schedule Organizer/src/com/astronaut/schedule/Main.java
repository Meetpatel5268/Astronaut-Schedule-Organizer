package com.astronaut.schedule;

import com.astronaut.schedule.exception.TaskConflictException;
import com.astronaut.schedule.exception.TaskNotFoundException;
import com.astronaut.schedule.factory.TaskFactory;
import com.astronaut.schedule.manager.ScheduleManager;
import com.astronaut.schedule.model.Priority;
import com.astronaut.schedule.model.Task;
import com.astronaut.schedule.observer.ConflictNotifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final ScheduleManager manager = ScheduleManager.getInstance();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


    public static void main(String[] args) {
        manager.registerObserver(new ConflictNotifier());

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": addTask(); break;
                case "2": removeTask(); break;
                case "3": viewTasks(); break;
                case "4": editTask(); break;
                case "5": markTaskStatus(); break;
                case "6": viewTasksByPriority(); break;
                case "7":
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;
                default:
                    System.out.println("Error: Invalid option. Please try again.");
                    break;
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- Astronaut Daily Schedule Organizer ---");
        System.out.println("1. Add Task");
        System.out.println("2. Remove Task");
        System.out.println("3. View All Tasks");
        System.out.println("4. Edit Task");
        System.out.println("5. Mark Task as Completed/Pending");
        System.out.println("6. View Tasks by Priority");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addTask() {
        try {
            System.out.print("Enter task description: ");
            String description = scanner.nextLine();
            System.out.print("Enter start time (e.g., 07:00 or 7:00): ");
            String startTime = scanner.nextLine();
            System.out.print("Enter end time (e.g., 08:30 or 8:30): ");
            String endTime = scanner.nextLine();
            System.out.print("Enter priority (High, Medium, Low): ");
            String priority = scanner.nextLine();

            Task task = TaskFactory.createTask(description, startTime, endTime, priority);
            manager.addTask(task);
            System.out.println("✅ Task added successfully. No conflicts.");
        } catch (DateTimeParseException e) {
            System.out.println("Error: Invalid time format. Please use HH:mm or H:mm.");
            LOGGER.log(Level.WARNING, "Invalid time format.", e);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            LOGGER.log(Level.WARNING, "Invalid argument for task.", e);
        } catch (TaskConflictException e) {
            // Observer already printed the detailed message.
            LOGGER.log(Level.INFO, "A task conflict occurred during add.", e);
        }
    }

    private static int getTaskIdFromUser() {
        while (true) {
            try {
                System.out.print("Enter Task ID: ");
                int id = Integer.parseInt(scanner.nextLine());
                return id;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid ID. Please enter a number.");
            }
        }
    }

    private static void removeTask() {
        viewTasks();
        if (manager.getTasks().isEmpty()) return;

        try {
            int id = getTaskIdFromUser();
            manager.removeTask(id);
            System.out.println("✅ Task removed successfully.");
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            LOGGER.log(Level.INFO, "Attempted to remove non-existent task.", e);
        }
    }

    private static void viewTasks() {
        List<Task> tasks = manager.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("\nNo tasks scheduled for the day.");
        } else {
            System.out.println("\n--- All Scheduled Tasks ---");
            tasks.forEach(System.out::println);
            System.out.println("---------------------------");
        }
    }

    private static void editTask() {
        viewTasks();
        if (manager.getTasks().isEmpty()) return;

        try {
            int id = getTaskIdFromUser();
            Task task = manager.findTaskById(id)
                    .orElseThrow(() -> new TaskNotFoundException("Error: Task with ID " + id + " not found."));

            System.out.printf("Editing Task %d: '%s'. Press Enter to keep current value.\n", id, task.getDescription());

            System.out.printf("Enter new description (%s): ", task.getDescription());
            String newDesc = scanner.nextLine();
            if (newDesc.isEmpty()) newDesc = task.getDescription();

            System.out.printf("Enter new start time (%s): ", task.getStartTime().format(TIME_FORMATTER));
            String newStartTimeStr = scanner.nextLine();
            LocalTime newStartTime = newStartTimeStr.isEmpty() ? task.getStartTime() : LocalTime.parse(newStartTimeStr, TaskFactory.FLEXIBLE_TIME_FORMATTER);

            System.out.printf("Enter new end time (%s): ", task.getEndTime().format(TIME_FORMATTER));
            String newEndTimeStr = scanner.nextLine();
            LocalTime newEndTime = newEndTimeStr.isEmpty() ? task.getEndTime() : LocalTime.parse(newEndTimeStr, TaskFactory.FLEXIBLE_TIME_FORMATTER);

            System.out.printf("Enter new priority (%s): ", task.getPriority());
            String newPriorityStr = scanner.nextLine();
            Priority newPriority = newPriorityStr.isEmpty() ? task.getPriority() : Priority.valueOf(newPriorityStr.toUpperCase());

            manager.editTask(id, newDesc, newStartTime, newEndTime, newPriority);
            System.out.println("✅ Task updated successfully.");

        } catch (TaskNotFoundException | TaskConflictException | DateTimeParseException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            LOGGER.log(Level.WARNING, "Failed to edit task.", e);
        }
    }

    private static void markTaskStatus() {
        viewTasks();
        if (manager.getTasks().isEmpty()) return;

        try {
            int id = getTaskIdFromUser();
            System.out.print("Mark as completed? (yes/no): ");
            String choice = scanner.nextLine();
            boolean isCompleted = choice.equalsIgnoreCase("yes");
            manager.markTaskAsCompleted(id, isCompleted);
            System.out.println("✅ Task status updated.");
        } catch (TaskNotFoundException e) {
            System.out.println(e.getMessage());
            LOGGER.log(Level.INFO, "Attempted to mark status of a non-existent task.", e);
        }
    }

    private static void viewTasksByPriority() {
        try {
            System.out.print("Enter priority to view (High, Medium, Low): ");
            String priorityStr = scanner.nextLine();
            Priority priority = Priority.valueOf(priorityStr.toUpperCase());

            List<Task> tasks = manager.getTasksByPriority(priority);

            if (tasks.isEmpty()) {
                System.out.printf("\nNo tasks found with priority: %s\n", priority);
            } else {
                System.out.printf("\n--- Tasks with Priority: %s ---\n", priority);
                tasks.forEach(System.out::println);
                System.out.println("---------------------------------");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid priority level.");
            LOGGER.log(Level.WARNING, "Invalid priority entered for viewing.", e);
        }
    }
}