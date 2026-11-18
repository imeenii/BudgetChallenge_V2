package com.example.budgetchallenge;

public class Budget {

    private double income;
    private double rent;
    private double food;
    private double transport;
    private double bills;
    private double other;
    private double goal;
    private int duration;

    public Budget(double income, double rent, double food,
                  double transport, double bills, double other,
                  double goal, int duration) {

        this.income = income;
        this.rent = rent;
        this.food = food;
        this.transport = transport;
        this.bills = bills;
        this.other = other;
        this.goal = goal;
        this.duration = duration;
    }

    public double calculateTotalExpenses() {
        return rent + food + transport + bills + other;
    }

    public double calculateBalance() {
        return income - calculateTotalExpenses();
    }

    public double getIncome() {
        return income;
    }

    public double getGoal() {
        return goal;
    }

    public int getDuration() {
        return duration;
    }

    public double getRent() {
        return rent;
    }

    public double getFood() {
        return food;
    }

    public double getTransport() {
        return transport;
    }

    public double getBills() {
        return bills;
    }

    public double getOther() {
        return other;
    }
}