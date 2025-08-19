package com.example.pomodorotimer.model;

public class Todo {
    private long id;
    private String title;
    private String description;
    private boolean isCompleted;
    private String dateCreated;

    public Todo() {
    }

    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
    }

    public Todo(long id, String title, String description, boolean isCompleted, String dateCreated) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.dateCreated = dateCreated;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }
}
