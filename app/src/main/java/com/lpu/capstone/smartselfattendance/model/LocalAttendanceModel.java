package com.lpu.capstone.smartselfattendance.model;

public class LocalAttendanceModel {
    private String id;
    private String name;
    private String fid;
    private String status;
    private String date;
    private String time;

    public LocalAttendanceModel(String id, String name, String fid, String status, String date,String time) {
        this.id = id;
        this.name = name;
        this.fid = fid;
        this.status = status;
        this.date = date;
        this.time = time;
    }

    public LocalAttendanceModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
