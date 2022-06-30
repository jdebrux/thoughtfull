package com.example.thoughtfull2.models;

public class ModelThoughtPost {

    private String pId, title, description, startTime, endTime, uid, uName, uEmail, uPp;
    private String pLocked, pRemind;

    public ModelThoughtPost() {
    }

    public ModelThoughtPost(String pId, String title, String description, String startTime, String endTime, String uid, String uName, String uEmail, String uPp, String pLocked, String pRemind) {
        this.pId = pId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.uid = uid;
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPp = uPp;
        this.pLocked = pLocked;
        this.pRemind = pRemind;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuPp() {
        return uPp;
    }

    public void setuPp(String uPp) {
        this.uPp = uPp;
    }

    public String getpLocked() {
        return pLocked;
    }

    public void setpLocked(String pLocked) {
        this.pLocked = pLocked;
    }

    public String getpRemind() {
        return pRemind;
    }

    public void setpRemind(String pRemind) {
        this.pRemind = pRemind;
    }
}
