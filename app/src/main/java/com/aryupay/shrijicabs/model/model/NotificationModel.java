package com.aryupay.shrijicabs.model.model;

/**
 * Created by Ravi Thakur on 11,September,2019
 * AryuPay Technologies,
 * India.
 */
public class NotificationModel {
    private String title, subtitle, description;
    private String timeAndDate;


    public NotificationModel(String title, String subtitle, String description, String timeAndDate) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.timeAndDate = timeAndDate;
    }

    public String getTimeAndDate() {
        return timeAndDate;
    }

    public void setTimeAndDate(String timeAndDate) {
        this.timeAndDate = timeAndDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
