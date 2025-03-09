package com.example.mywallet.Models;

public class Transaction {
    private int id;
    private int user_id;
    private int account_id;
    private int categoryId;
    private double amount;
    private String date;
    private String dueDate;
    private String note;
    private String category_type;
    private String categoryName;

    public Transaction(int user_id, int account_id, int categoryId, double amount, String date, String dueDate, String note) {
        this.user_id = user_id;
        this.account_id = account_id;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.dueDate = dueDate;
        this.note = note;
    }
    public Transaction(int id, String categoryName, double amount, String note, String date) {
        this.id = id;
        this.categoryName = categoryName;
        this.amount = amount;
        this.note = note;
        this.date = date;
    }

    // Constructor đầy đủ
    public Transaction(int id, int user_id, int account_id, int categoryId, double amount, String date, String dueDate, String note) {
        this.id = id;
        this.user_id = user_id;
        this.account_id = account_id;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.dueDate = dueDate;
        this.note = note;
    }


    public Transaction(String categoryName, double amount, String note, String date) {
        this.categoryName = categoryName;
        this.amount = amount;
        this.note = note;
        this.date = date;
    }

    public String getCategoryType() {
        return category_type;
    }

    public void setCategory_type(String categoryType) {
        this.category_type = categoryType;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTransactionId() {
        return id;
    }

    public void setTransactionId(int transactionId) {
        this.id = transactionId;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public int getAccountId() {
        return account_id;
    }

    public void setAccountId(int accountId) {
        this.account_id = accountId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
