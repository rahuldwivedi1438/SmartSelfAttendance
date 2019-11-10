package com.lpu.capstone.smartselfattendance.model;

public class User {
    private String id;
    private String name;
    private String imageLink;
    private String email;
    private String gender;
    private String section;
    private String subject;
    private String level;
    private String fID;
    private String password;

    public User(String id, String name, String imageLink, String email, String gender, String section, String subject, String level,String fID,String password) {
        this.id = id;
        this.name = name;
        this.imageLink = imageLink;
        this.email = email;
        this.gender = gender;
        this.section = section;
        this.subject = subject;
        this.level = level;
        this.fID = fID;
        this.password = password;
    }

    public User() {
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

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getfID() {
        return fID;
    }

    public void setfID(String fID) {
        this.fID = fID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
