package com.aryupay.shrijicabs.model.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationResponse {

    @SerializedName("success")
    @Expose
    private List<NotificationData> success = null;

    public List<NotificationData> getSuccess() {
        return success;
    }

    public void setSuccess(List<NotificationData> success) {
        this.success = success;
    }
}
