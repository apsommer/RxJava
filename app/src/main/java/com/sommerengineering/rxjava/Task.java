package com.sommerengineering.rxjava;

/**
 * Simple POJO for a task in a tod0 list
 */
public class Task {

    private String description;
    private boolean isComplete;
    private int priority;

    public Task (String description, boolean isComplete, int priority) {
        this.description = description;
        this.isComplete = isComplete;
        this.priority = priority;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isComplete() { return isComplete; }
    public void setComplete(boolean isComplete) { this.isComplete = isComplete; }

    public int getPriority() { return priority; }
    private void setPriority(int priority) { this.priority = priority; }
}
