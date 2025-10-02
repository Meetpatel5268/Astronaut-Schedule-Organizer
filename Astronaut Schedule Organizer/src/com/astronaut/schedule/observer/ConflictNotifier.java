package com.astronaut.schedule.observer;

public class ConflictNotifier implements Observer {
    @Override
    public void update(String message) {
        System.out.println("NOTIFICATION: " + message);
    }
}