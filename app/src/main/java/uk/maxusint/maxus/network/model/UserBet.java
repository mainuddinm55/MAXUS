package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserBet {
    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("bet_id")
    @Expose
    private int betId;
    @SerializedName("bet_option_id")
    @Expose
    private int betOptionId;
    @SerializedName("bet_rate")
    @Expose
    private double betRate;
    @SerializedName("bet_amount")
    @Expose
    private double betAmount;
    @SerializedName("bet_return_amount")
    @Expose
    private double betReturnAmount;
    @SerializedName("bet_mode_id")
    @Expose
    private int betModeId;
    @SerializedName("bet_date")
    @Expose
    private String betDate;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("question")
    @Expose
    private String question;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBetId() {
        return betId;
    }

    public void setBetId(int betId) {
        this.betId = betId;
    }

    public int getBetOptionId() {
        return betOptionId;
    }

    public void setBetOptionId(int betOptionId) {
        this.betOptionId = betOptionId;
    }

    public double getBetRate() {
        return betRate;
    }

    public void setBetRate(double betRate) {
        this.betRate = betRate;
    }

    public double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(double betAmount) {
        this.betAmount = betAmount;
    }

    public double getBetReturnAmount() {
        return betReturnAmount;
    }

    public void setBetReturnAmount(double betReturnAmount) {
        this.betReturnAmount = betReturnAmount;
    }

    public int getBetModeId() {
        return betModeId;
    }

    public void setBetModeId(int betModeId) {
        this.betModeId = betModeId;
    }

    public String getBetDate() {
        return betDate;
    }

    public void setBetDate(String betDate) {
        this.betDate = betDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
