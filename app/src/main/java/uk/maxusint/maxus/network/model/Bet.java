package uk.maxusint.maxus.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Bet implements Parcelable {
    @SerializedName("bet_id")
    private int betId;
    @SerializedName("question")
    private String question;
    @SerializedName("started_date")
    private String date;
    @SerializedName("match_id")
    private int matchId;
    @SerializedName("bet_mode")
    private int betMode;
    @SerializedName("status")
    private int status;
    @SerializedName("result")
    private String result;

    public Bet(int betId, String question, String date, int matchId, int betMode, int status, String result) {
        this.betId = betId;
        this.question = question;
        this.date = date;
        this.matchId = matchId;
        this.betMode = betMode;
        this.status = status;
        this.result = result;
    }

    protected Bet(Parcel in) {
        betId = in.readInt();
        question = in.readString();
        date = in.readString();
        matchId = in.readInt();
        betMode = in.readInt();
        status = in.readInt();
        result = in.readString();
    }

    public static final Creator<Bet> CREATOR = new Creator<Bet>() {
        @Override
        public Bet createFromParcel(Parcel in) {
            return new Bet(in);
        }

        @Override
        public Bet[] newArray(int size) {
            return new Bet[size];
        }
    };

    public int getBetId() {
        return betId;
    }

    public void setBetId(int betId) {
        this.betId = betId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getBetMode() {
        return betMode;
    }

    public void setBetMode(int betMode) {
        this.betMode = betMode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(betId);
        dest.writeString(question);
        dest.writeString(date);
        dest.writeInt(matchId);
        dest.writeInt(betMode);
        dest.writeInt(status);
        dest.writeString(result);
    }
}
