package com.aryupay.shrijicabs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookingResponse {

    @SerializedName("success")
    @Expose
    private List<BookingData> success = null;

    public List<BookingData> getSuccess() {
        return success;
    }

    public void setSuccess(List<BookingData> success) {
        this.success = success;
    }
}
