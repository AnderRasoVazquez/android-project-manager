package com.example.projectmanager.model;

import java.util.Date;

public class WorkSession {
    private int id;

    private Date date;
    private double time;

    public WorkSession(int id, Date date, double time) {
        this.date = date;
        this.time = time;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

}
