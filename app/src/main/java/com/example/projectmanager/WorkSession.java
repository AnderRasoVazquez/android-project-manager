package com.example.projectmanager;

import java.util.Date;

public class WorkSession {
    private Date init, end;

    public WorkSession(Date init, Date end) {
        this.init = init;
        this.end = end;
    }

    public Date getInit() {
        return init;
    }

    public void setInit(Date init) {
        this.init = init;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getSecondsPassed() {
        return (int) ((end.getTime() - init.getTime()) / 1000);
    }
}
