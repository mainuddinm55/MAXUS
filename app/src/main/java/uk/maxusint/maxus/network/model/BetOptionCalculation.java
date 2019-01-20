package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.SerializedName;

public class BetOptionCalculation {
    @SerializedName("bet_id")
    private int betId;
    @SerializedName("bet_option_id")
    private int betOptionId;
    @SerializedName("bet_amount")
    private double betAmount;
    @SerializedName("bet_return_amount")
    private double betReturnAmount;
    private String betOption;

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

    public String getBetOption() {
        return betOption;
    }

    public void setBetOption(String betOption) {
        this.betOption = betOption;
    }

}
