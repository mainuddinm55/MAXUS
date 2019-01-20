package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BetResult {

    @SerializedName("match")
    @Expose
    private String match;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("right_ans")
    @Expose
    private Integer rightAns;
    @SerializedName("bet_option_id")
    @Expose
    private Integer betOptionId;
    @SerializedName("bet_amount")
    @Expose
    private Double betAmount;
    @SerializedName("bet_return_amount")
    @Expose
    private Double betReturnAmount;
    @SerializedName("result")
    @Expose
    private String result;

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getRightAns() {
        return rightAns;
    }

    public void setRightAns(Integer rightAns) {
        this.rightAns = rightAns;
    }

    public Integer getBetOptionId() {
        return betOptionId;
    }

    public void setBetOptionId(Integer betOptionId) {
        this.betOptionId = betOptionId;
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