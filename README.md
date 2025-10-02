
# 🚀 Astronaut Daily Schedule Organizer

A **console-based Java application** designed to help astronauts manage their daily tasks efficiently.  
This project demonstrates **core Java, OOP, SOLID principles, and software design patterns** while focusing on **intelligent scheduling without conflicts**.

---

## ✨ Features

- ➕ **Add New Tasks** – Schedule with description, start time, end time, and priority (High, Medium, Low).
- ⚠️ **Conflict Detection** – Prevents overlapping tasks automatically.
- 📋 **View All Tasks** – Displays a sorted list of scheduled tasks.
- ✏️ **Edit Tasks** – Modify details interactively using task ID.
- ✅ **Mark Completed** – Toggle tasks between **PENDING** and **COMPLETED**.
- 🎯 **Filter by Priority** – View tasks by priority level.
- ❌ **Remove Tasks** – Securely delete tasks using task ID.
- 🕒 **Flexible Time Input** – Accepts both `7:45` and `07:45`.
- 🔒 **Robust Error Handling** – Clear messages for invalid inputs, conflicts, or errors.

---

## 🛠️ Tech Stack

- **Language:** Java (JDK 17+)
- **Core Libraries:**
  - `java.time` → modern date/time manipulation
  - `java.util.logging` → application and error logging

---

## 🎨 Design Patterns Implemented

1. **Singleton Pattern**  
   - `ScheduleManager` ensures a single global instance for managing tasks.

2. **Factory Pattern**  
   - `TaskFactory` encapsulates task creation logic (time parsing, priority validation).

3. **Observer Pattern**  
   - `ScheduleManager` (Subject) notifies `ConflictNotifier` (Observer) on scheduling conflicts.  
   - Enables extensible notifications (console, logging, alerts).

---

## 📂 Project Structure

```

astronaut-schedule/
└── src/
└── com/
└── astronaut/
└── schedule/
├── exception/
│   ├── TaskConflictException.java
│   └── TaskNotFoundException.java
├── factory/
│   └── TaskFactory.java
├── manager/
│   └── ScheduleManager.java
├── model/
│   ├── Priority.java
│   └── Task.java
├── observer/
│   ├── ConflictNotifier.java
│   ├── Observer.java
│   └── Subject.java
└── Main.java

````

---

## ⚙️ Getting Started

### Prerequisites
- Install **JDK 17+**  
  [Download from Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or [Adoptium](https://adoptium.net/).

### Compile & Run
```bash
# Navigate to source
cd astronaut-schedule/src

# Compile all classes
javac -d . com/astronaut/schedule/*/*.java com/astronaut/schedule/Main.java

# Run the program
java com.astronaut.schedule.Main
````

---

## 📋 How to Use

When running, you’ll see a menu of options:

1. **Add Task** – Enter description, start/end times, and priority.
2. **Remove Task** – Enter task ID to delete.
3. **View All Tasks** – Sorted by start time.
4. **Edit Task** – Update details by entering task ID.
5. **Toggle Completion** – Mark task as `COMPLETED` or `PENDING`.
6. **Filter by Priority** – View only High/Medium/Low priority tasks.
7. **Exit** – Quit the application.

---

## 🧑‍💻 Principles & Best Practices

* **OOP Concepts:** Encapsulation, inheritance (custom exceptions), polymorphism.
* **SOLID Principles:**

  * **S**ingle Responsibility – Each class has one purpose (Task, Manager, Factory).
  * **O**pen/Closed – Observer allows extensible notifications.
  * **DRY** – Shared logic (e.g., conflict checks) extracted into helper methods.

---

