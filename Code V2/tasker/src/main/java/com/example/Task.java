package com.example;

public class Task {
    private String name;
    private String duration;
    private String priority;

    public Task(String name, String duration, String priority) {
        this.name = name;
        this.duration = duration;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public String getPriority() {
        return priority;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}

