package com.example.projectmanager.model;

import java.util.ArrayList;
import java.util.Date;

public class Task {

    private int id;
    private int progress;
    private String name;
    private String desc;
    private Date due;
    private int expected;
    private ArrayList<WorkSession> workSessions;

    public Task(int id, int progress, String name, String desc, Date due, int expected, ArrayList<WorkSession> workSessions) {
        this.id = id;
        this.progress = progress;
        this.name = name;
        this.desc = desc;
        this.due = due;
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

    public int getExpected() {
        return expected;
    }

    public void setExpected(int expected) {
        this.expected = expected;
    }

    public double getTotalTime() {
        double result = 0;
        for (WorkSession workSession : workSessions) {
            result += workSession.getTime();
        }
        return result;
    }

}
