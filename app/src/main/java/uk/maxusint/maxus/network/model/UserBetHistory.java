package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserBetHistory {

    @SerializedName("bet_date")
    @Expose
    private String betDate;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("bet_rate")
    @Expose
    private Double betRate;
    @SerializedName("bet_amount")
    @Expose
    private Double betAmount;
    @SerializedName("bet_return_amount")
    @Expose
    private Double betReturnAmount;
    @SerializedName("result")
    @Expose
    private String result;

    public String getBetDate() {
        return betDate;
    }

    public void setBetDate(String betDate) {
        this.betDate = betDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Double getBetRate() {
        return betRate;
    }

    public void setBetRate(Double betRate) {
        this.betRate = betRate;
    }

    public Double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Double betAmount) {
        this.betAmount = betAmount;
    }

    public Double getBetReturnAmount() {
        return betReturnAmount;
    }

    public void setBetReturnAmount(Double betReturnAmount) {
        this.betReturnAmount = betReturnAmount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}