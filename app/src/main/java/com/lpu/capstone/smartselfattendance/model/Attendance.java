package com.lpu.capstone.smartselfattendance.model;

import java.util.List;

public class Attendance {
    private String afID;
    private String section;
    private String subject;
    private String lectureDate;
    private String lectureTime;
    private List<AttendanceDetails> detailsList;
    private String otp;

    public Attendance() {
    }

    public Attendance(String afID, String section, String subject, String lectureDate, String lectureTime, List<AttendanceDetails> detailsList,String otp) {
       this.afID = afID;
        this.section = section;
        this.subject = subject;
        this.lectureDate = lectureDate;
        this.lectureTime = lectureTime;
        this.detailsList = detailsList;
        this.otp = otp;
    }

    public String getAfID() {
        return afID;
    }

    public void setAfID(String afID) {
        this.afID = afID;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLectureDate() {
        return lectureDate;
    }

    public void setLectureDate(String lectureDate) {
        this.lectureDate = lectureDate;
    }

    public String getLectureTime() {
        return lectureTime;
    }

    public void setLectureTime(String lectureTime) {
        this.lectureTime = lectureTime;
    }

    public List<AttendanceDetails> getDetailsList() {
        return detailsList;
    }

    public void setDetailsList(List<AttendanceDetails> detailsList) {
        this.detailsList = detailsList;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
