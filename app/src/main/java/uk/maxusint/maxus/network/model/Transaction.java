package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("from_username")
    private String fromUsername;
    @SerializedName("to_username")
    private String toUsername;
    @SerializedName("trans_date")
    private String transDate;
    @SerializedName("amount")
    private double amount;
    @SerializedName("trans_type")
    private String transType;
    @SerializedName("trans_charge")
    private double transCharge;
    @SerializedName("status")
    private String status;
    @SerializedName("from_user_seen")
    private int fromUserSeen;
    @SerializedName("to_user_seen")
    private int toUserSeen;

    public Transaction(int id, String fromUsername, String toUsername, String transDate, double amount, String transType, double transCharge, String status) {
        this.id = id;
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.transDate = transDate;
        this.amount = amount;
        this.transType = transType;
        this.transCharge = transCharge;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public double getTransCharge() {
        return transCharge;
    }

    public void setTransCharge(double transCharge) {
        this.transCharge = transCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFromUserSeen() {
        return fromUserSeen;
    }

    public void setFromUserSeen(int fromUserSeen) {
        this.fromUserSeen = fromUserSeen;
    }

    public int getToUserSeen() {
        return toUserSeen;
    }

    public void setToUserSeen(int toUserSeen) {
        this.toUserSeen = toUserSeen;
    }
}
