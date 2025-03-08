package com.example.mywallet.Models;

public class Budget {
    private int id;
    private String name;
    private double amountLimit;
    private double amountSpent;
    private String startDate;
    private String endDate;
    private int categoryId; // Thêm categoryId

    public Budget() {
    }

    public Budget(int id, String name, double amountLimit, double amountSpent) {
        this.id = id;
        this.name = name;
        this.amountLimit = amountLimit;
        this.amountSpent = amountSpent;
    }

    public Budget(int id, String name, double amountLimit, double amountSpent, String startDate, String endDate, int categoryId) {
        this.id = id;
        this.name = name;
        this.amountLimit = amountLimit;
        this.amountSpent = amountSpent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categoryId = categoryId; // Khởi tạo categoryId
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getAmountLimit() { return amountLimit; }
    public double getAmountSpent() { return amountSpent; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getCategoryId() { return categoryId; } // Getter cho categoryId

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAmountLimit(double amountLimit) { this.amountLimit = amountLimit; }
    public void setAmountSpent(double amountSpent) { this.amountSpent = amountSpent; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; } // Setter cho categoryId
}
