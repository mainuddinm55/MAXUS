package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.maxusint.maxus.network.model.BetRate;

public class AllBetRateResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("bet_rate")
    @Expose
    private List<BetRate> betRate = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<BetRate> getBetRate() {
        return betRate;
    }

    public void setBetRate(List<BetRate> betRate) {
        this.betRate = betRate;
    }


}
