package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.maxusint.maxus.network.model.Match;

public class MatchResponse {
    @SerializedName("error")
    private boolean err;
    @SerializedName("matches")
    private List<Match> matches;

    public MatchResponse(boolean err, List<Match> matches) {
        this.err = err;
        this.matches = matches;
    }

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
