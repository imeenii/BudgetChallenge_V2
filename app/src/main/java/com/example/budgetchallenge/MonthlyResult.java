package com.example.budgetchallenge;

public class MonthlyResult {

    private int month;
    private double income;
    private double expenses;
    private double balance;
    private double monthlySavings;
    private double cumulativeSavings;
    private String decisions;

    public MonthlyResult(int month, double income,
                         double expenses, double balance,
                         double monthlySavings,
                         double cumulativeSavings,
                         String decisions) {

        this.month = month;
        this.income = income;
        this.expenses = expenses;
        this.balance = balance;
        this.monthlySavings = monthlySavings;
        this.cumulativeSavings = cumulativeSavings;
        this.decisions = decisions;
    }

    public int getMonth() {
        return month;
    }

    public double getIncome() {
        return income;
    }

    public double getExpenses() {
        return expenses;
    }

    public double getBalance() {
        return balance;
    }

    public double getMonthlySavings() {
        return monthlySavings;
    }

    public double getCumulativeSavings() {
        return cumulativeSavings;
    }

    public String getDecisions() {
        return decisions;
    }
}