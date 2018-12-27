package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.maxusint.maxus.network.model.Bet;

public class AllBetResponse {
    @SerializedName("error")
    private boolean err;
    @SerializedName("bets")
    private List<Bet> bets;

    public AllBetResponse(boolean err, List<Bet> bets) {
        this.err = err;
        this.bets = bets;
    }

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public List<Bet> getBets() {
        return bets;
    }

    public void setBets(List<Bet> bets) {
        this.bets = bets;
    }
}
