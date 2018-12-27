package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uk.maxusint.maxus.network.model.Match;

public class InsertedMatchResponse {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("match")
    @Expose
    private Match match;

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

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }
}
