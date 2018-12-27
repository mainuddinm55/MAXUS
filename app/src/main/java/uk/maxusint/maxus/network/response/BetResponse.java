package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.maxusint.maxus.network.model.Bet;

public class BetResponse {
    @SerializedName("error")
    private boolean err;
    @SerializedName("message")
    private String msg;
    @SerializedName("bet")
    private Bet bet;

    public BetResponse(boolean err, String msg, Bet bet) {
        this.err = err;
        this.msg = msg;
        this.bet = bet;
    }

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
