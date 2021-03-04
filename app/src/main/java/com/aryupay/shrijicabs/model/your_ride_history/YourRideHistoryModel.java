package com.aryupay.shrijicabs.model.your_ride_history;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YourRideHistoryModel extends ArrayList<YourRideHistoryModel.Success> {

    @SerializedName("success")
    @Expose
    private ArrayList<Success> success = null;


    public ArrayList<Success> getSuccess() {
        return success;
    }

    public void setSuccess(ArrayList<Success> success) {
        this.success = success;
    }

    public class Success {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("payment_gateway_transaction_id")
        @Expose
        private Integer paymentGatewayTransactionId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

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

        public Integer getPaymentGatewayTransactionId() {
            return paymentGatewayTransactionId;
        }

        public void setPaymentGatewayTransactionId(Integer paymentGatewayTransactionId) {
            this.paymentGatewayTransactionId = paymentGatewayTransactionId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
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

    }
}
