package com.aryupay.shrijicabs.model.bookings_model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vendor {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("vehicle_detail")
    @Expose
    private Integer vehicleDetail;
    @SerializedName("year_of_exp")
    @Expose
    private Integer yearOfExp;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("pincode")
    @Expose
    private Object pincode;
    @SerializedName("aadhar")
    @Expose
    private Object aadhar;
    @SerializedName("voter_id")
    @Expose
    private Object voterId;
    @SerializedName("pan")
    @Expose
    private Object pan;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("bank_account_name")
    @Expose
    private String bankAccountName;
    @SerializedName("bank_account_number")
    @Expose
    private Integer bankAccountNumber;
    @SerializedName("bank_ifsc")
    @Expose
    private String bankIfsc;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("beam_no")
    @Expose
    private Object beamNo;
    @SerializedName("beam_expiry_date")
    @Expose
    private Object beamExpiryDate;
    @SerializedName("vehicle_company")
    @Expose
    private String vehicleCompany;
    @SerializedName("vehicle_model")
    @Expose
    private String vehicleModel;
    @SerializedName("ac_type")
    @Expose
    private Object acType;
    @SerializedName("user")
    @Expose
    private User_ user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getVehicleDetail() {
        return vehicleDetail;
    }

    public void setVehicleDetail(Integer vehicleDetail) {
        this.vehicleDetail = vehicleDetail;
    }

    public Integer getYearOfExp() {
        return yearOfExp;
    }

    public void setYearOfExp(Integer yearOfExp) {
        this.yearOfExp = yearOfExp;
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

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public Object getPincode() {
        return pincode;
    }

    public void setPincode(Object pincode) {
        this.pincode = pincode;
    }

    public Object getAadhar() {
        return aadhar;
    }

    public void setAadhar(Object aadhar) {
        this.aadhar = aadhar;
    }

    public Object getVoterId() {
        return voterId;
    }

    public void setVoterId(Object voterId) {
        this.voterId = voterId;
    }

    public Object getPan() {
        return pan;
    }

    public void setPan(Object pan) {
        this.pan = pan;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public Integer getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(Integer bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankIfsc() {
        return bankIfsc;
    }

    public void setBankIfsc(String bankIfsc) {
        this.bankIfsc = bankIfsc;
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

    public Object getBeamNo() {
        return beamNo;
    }

    public void setBeamNo(Object beamNo) {
        this.beamNo = beamNo;
    }

    public Object getBeamExpiryDate() {
        return beamExpiryDate;
    }

    public void setBeamExpiryDate(Object beamExpiryDate) {
        this.beamExpiryDate = beamExpiryDate;
    }

    public String getVehicleCompany() {
        return vehicleCompany;
    }

    public void setVehicleCompany(String vehicleCompany) {
        this.vehicleCompany = vehicleCompany;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public Object getAcType() {
        return acType;
    }

    public void setAcType(Object acType) {
        this.acType = acType;
    }

    public User_ getUser() {
        return user;
    }

    public void setUser(User_ user) {
        this.user = user;
    }

}