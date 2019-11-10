package com.lpu.capstone.smartselfattendance.model;

public class AttendanceDetails {
    private String id;
    private String name;
    private String fid;
    private String status;

    public AttendanceDetails() {
    }

    public AttendanceDetails(String id, String name, String fid, String status) {
        this.id = id;
        this.name = name;
        this.fid = fid;
        this.status = status;
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
}
