package com.aryupay.shrijicabs.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("client_id")
    @Expose
    private Integer clientId;
    @SerializedName("vendor_id")
    @Expose
    private Integer vendorId;
    @SerializedName("vendor_category")
    @Expose
    private Integer vendorCategory;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("booking_status_id")
    @Expose
    private Integer bookingStatusId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("schedule_at")
    @Expose
    private String scheduleAt;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("drop_latitude")
    @Expose
    private Double dropLatitude;
    @SerializedName("drop_longitude")
    @Expose
    private Double dropLongitude;
    @SerializedName("landmark")
    @Expose
    private Object landmark;
    @SerializedName("remarks")
    @Expose
    private Object remarks;
    @SerializedName("starts_at")
    @Expose
    private Object startsAt;
    @SerializedName("ends_at")
    @Expose
    private Object endsAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("way")
    @Expose
    private Integer way;
    @SerializedName("client")
    @Expose
    private Client client;
    @SerializedName("vendor")
    @Expose
    private Vendor vendor;

    public BookingData(Integer id, Integer clientId, Integer vendorId, Integer vendorCategory, Integer otp, Integer bookingStatusId, String amount, String scheduleAt, Double latitude, Double longitude, Double dropLatitude, Double dropLongitude, Object landmark, Object remarks, Object startsAt, Object endsAt, String createdAt, String updatedAt, Integer way, Client client, Vendor vendor) {
        this.id = id;
        this.clientId = clientId;
        this.vendorId = vendorId;
        this.vendorCategory = vendorCategory;
        this.otp = otp;
        this.bookingStatusId = bookingStatusId;
        this.amount = amount;
        this.scheduleAt = scheduleAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dropLatitude = dropLatitude;
        this.dropLongitude = dropLongitude;
        this.landmark = landmark;
        this.remarks = remarks;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.way = way;
        this.client = client;
        this.vendor = vendor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public Integer getVendorCategory() {
        return vendorCategory;
    }

    public void setVendorCategory(Integer vendorCategory) {
        this.vendorCategory = vendorCategory;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Integer getBookingStatusId() {
        return bookingStatusId;
    }

    public void setBookingStatusId(Integer bookingStatusId) {
        this.bookingStatusId = bookingStatusId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getScheduleAt() {
        return scheduleAt;
    }

    public void setScheduleAt(String scheduleAt) {
        this.scheduleAt = scheduleAt;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDropLatitude() {
        return dropLatitude;
    }

    public void setDropLatitude(Double dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public Double getDropLongitude() {
        return dropLongitude;
    }

    public void setDropLongitude(Double dropLongitude) {
        this.dropLongitude = dropLongitude;
    }

    public Object getLandmark() {
        return landmark;
    }

    public void setLandmark(Object landmark) {
        this.landmark = landmark;
    }

    public Object getRemarks() {
        return remarks;
    }

    public void setRemarks(Object remarks) {
        this.remarks = remarks;
    }

    public Object getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Object startsAt) {
        this.startsAt = startsAt;
    }

    public Object getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Object endsAt) {
        this.endsAt = endsAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getWay() {
        return way;
    }

    public void setWay(Integer way) {
        this.way = way;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
