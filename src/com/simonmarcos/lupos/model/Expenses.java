package com.simonmarcos.lupos.model;

import java.sql.Date;

public class Expenses {

    private int idExpenses;
    private String category;
    private String type;
    private java.sql.Date date;
    private String description;
    private double value;

    public Expenses(int idExpenses, String category, String type, Date date, String description, double value) {
        this.idExpenses = idExpenses;
        this.category = category;
        this.type = type;
        this.date = date;
        this.description = description;
        this.value = value;
    }

    public Expenses() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIdExpenses() {
        return idExpenses;
    }

    public void setIdExpenses(int idExpenses) {
        this.idExpenses = idExpenses;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Expenses{" + "idExpenses=" + idExpenses + ", type=" + type + ", description=" + description + ", value=" + value + '}';
    }

}
