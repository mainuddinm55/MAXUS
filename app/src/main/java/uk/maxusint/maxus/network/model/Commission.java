package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Commission {

    @SerializedName("comm_id")
    @Expose
    private Integer commId;
    @SerializedName("comm_rate")
    @Expose
    private Double commRate;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("from_user_id")
    @Expose
    private Integer fromUserId;
    @SerializedName("bet_id")
    @Expose
    private Integer betId;
    @SerializedName("comm_date")
    @Expose
    private String commDate;
    @SerializedName("purpose")
    @Expose
    private String purpose;

    public Integer getCommId() {
        return commId;
    }

    public void setCommId(Integer commId) {
        this.commId = commId;
    }

    public Double getCommRate() {
        return commRate;
    }

    public void setCommRate(Double commRate) {
        this.commRate = commRate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Integer getBetId() {
        return betId;
    }

    public void setBetId(Integer betId) {
        this.betId = betId;
    }

    public String getCommDate() {
        return commDate;
    }

    public void setCommDate(String commDate) {
        this.commDate = commDate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}