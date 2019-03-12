package com.example.projectmanager.model;

import java.util.ArrayList;
import java.util.Date;

public class Task {

    private int id;
    private int progress;
    private String name;
    private String desc;
    private Date due;
    private Date init;
    private double expected;
    private ArrayList<WorkSession> workSessions;

    public Task(int id, int progress, String name, String desc, Date due, Date init, double expected, ArrayList<WorkSession> workSessions) {
        this.id = id;
        this.progress = progress;
        this.name = name;
        this.desc = desc;
        this.due = due;
        this.init = init;
        this.expected = expected;
        this.workSessions = workSessions;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public double getExpected() {
        return expected;
    }

    public void setExpected(double expected) {
        this.expected = expected;
    }

    public double getTotalTime() {
        double result = 0;
        for (WorkSession workSession : workSessions) {
            result += workSession.getTime();
        }
        return result;
    }

    public Date getInit() {
        return init;
    }

    public int getId() {
        return id;
    }

    public void setInit(Date init) {
        this.init = init;
    }

}
