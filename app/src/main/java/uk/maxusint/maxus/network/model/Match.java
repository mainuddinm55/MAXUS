package uk.maxusint.maxus.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Match implements Parcelable {
    @SerializedName("id")
    private int id;
    @SerializedName("team1")
    private String team1;
    @SerializedName("team2")
    private String team2;
    @SerializedName("date_time")
    private String dateTime;
    @SerializedName("tournament")
    private String tournament;
    @SerializedName("match_type")
    private String matchType;
    @SerializedName("match_format")
    private String matchFormat;
    @SerializedName("status")
    private String status;

    public Match(int id, String team1, String team2, String dateTime, String tournament, String matchType, String matchFormat, String status) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.dateTime = dateTime;
        this.tournament = tournament;
        this.matchType = matchType;
        this.matchFormat = matchFormat;
        this.status = status;
    }


    protected Match(Parcel in) {
        id = in.readInt();
        team1 = in.readString();
        team2 = in.readString();
        dateTime = in.readString();
        tournament = in.readString();
        matchType = in.readString();
        matchFormat = in.readString();
        status = in.readString();
    }

    public static final Creator<Match> CREATOR = new Creator<Match>() {
        @Override
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        @Override
        public Match[] newArray(int size) {
            return new Match[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(team1);
        dest.writeString(team2);
        dest.writeString(dateTime);
        dest.writeString(tournament);
        dest.writeString(matchType);
        dest.writeString(matchFormat);
        dest.writeString(status);
    }
}



