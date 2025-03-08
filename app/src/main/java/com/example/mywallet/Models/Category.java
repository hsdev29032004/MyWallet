package com.example.mywallet.Models;

public class Category {
    private int id;
    private int user_id;
    private String name;
    private String type;

    public Category(int id, int user_id, String name, String type) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.type = type;
    }

    public Category(int user_id, String name, String type) {
        this.user_id = user_id;
        this.name = name;
        this.type = type;
    }

    public Category(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
