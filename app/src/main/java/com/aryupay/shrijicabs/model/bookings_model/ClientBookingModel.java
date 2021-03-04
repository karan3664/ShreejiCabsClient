package com.aryupay.shrijicabs.model.bookings_model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClientBookingModel {

    @SerializedName("success")
    @Expose
    private List<Success> success = null;

    public List<Success> getSuccess() {
        return success;
    }

    public void setSuccess(List<Success> success) {
        this.success = success;
    }

}