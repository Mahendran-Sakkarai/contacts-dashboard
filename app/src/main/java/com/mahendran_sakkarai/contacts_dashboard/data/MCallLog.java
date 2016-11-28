package com.mahendran_sakkarai.contacts_dashboard.data;

public class MCallLog {
    private String name;

    private String contactNumber;

    private String eMail;

    private long lastContactTime;

    private long totalTalkTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public long getLastContactTime() {
        return lastContactTime;
    }

    public void setLastContactTime(long lastContactTime) {
        this.lastContactTime = lastContactTime;
    }

    public long getTotalTalkTime() {
        return totalTalkTime;
    }

    public void setTotalTalkTime(long totalTalkTime) {
        this.totalTalkTime = totalTalkTime;
    }
}
