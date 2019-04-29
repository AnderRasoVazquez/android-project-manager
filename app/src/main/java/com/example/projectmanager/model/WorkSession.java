package com.example.projectmanager.model;

import java.util.Date;

/**
 * Representa un dia trabajado.
 */
public class WorkSession {
    private String id;
    private Date date;
    private double time;

    public WorkSession(String id, Date date, double time) {
        this.id = id;
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

    public String getId() {
        return id;
    }

}
