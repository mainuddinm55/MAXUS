package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatchBetRates {

    @SerializedName("bet")
    @Expose
    private Bet bet;
    @SerializedName("bet_rates")
    @Expose
    private List<BetRate> betRates = null;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("team1")
    @Expose
    private String team1;
    @SerializedName("team2")
    @Expose
    private String team2;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("match_type")
    @Expose
    private String matchType;
    @SerializedName("match_format")
    @Expose
    private String matchFormat;
    @SerializedName("status")
    @Expose
    private Integer status;

    public Bet getBet() {
        return bet;
    }

    public void setBet(Bet bet) {
        this.bet = bet;
    }

    public List<BetRate> getBetRates() {
        return betRates;
    }

    public void setBetRates(List<BetRate> betRates) {
        this.betRates = betRates;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchFormat() {
        return matchFormat;
    }

    public void setMatchFormat(String matchFormat) {
        this.matchFormat = matchFormat;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
