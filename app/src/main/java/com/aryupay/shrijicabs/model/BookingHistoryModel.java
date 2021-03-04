package com.aryupay.shrijicabs.model;

/**
 * Created by Ravi Thakur on 11,September,2019
 * AryuPay Technologies,
 * India.
 */
public class BookingHistoryModel {
    private String date, source, destination, amount;
    private int status;

    public BookingHistoryModel(String date, String source, String destination, String amount, int status) {
        this.date = date;
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
