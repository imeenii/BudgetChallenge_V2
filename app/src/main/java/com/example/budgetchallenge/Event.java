package com.example.budgetchallenge;

public class Event {

    private String title;
    private String description;
    private double cost;

    public Event(String title, String description, double cost) {
        this.title = title;
        this.description = description;
        this.cost = cost;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}