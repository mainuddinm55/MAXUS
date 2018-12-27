package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.maxusint.maxus.network.model.BetRate;

public class BetRateResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("betrate")
    @Expose
    private List<BetRate> betrate = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BetRate> getBetrate() {
        return betrate;
    }

    public void setBetrate(List<BetRate> betrate) {
        this.betrate = betrate;
    }

}
