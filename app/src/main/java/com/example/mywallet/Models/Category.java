package com.example.mywallet.Models;

public class Category {
    private int id;
    private int icon;
    private String name;
    private String type;

    public Category() {
    }

    public Category(int id, int icon, String name, String type) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.type = type;
    }

    public Category(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public Category(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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
