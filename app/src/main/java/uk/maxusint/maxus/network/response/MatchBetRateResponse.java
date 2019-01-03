package uk.maxusint.maxus.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import uk.maxusint.maxus.network.model.Bet;
import uk.maxusint.maxus.network.model.BetRate;
import uk.maxusint.maxus.network.model.Match;

public class MatchBetRateResponse {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("matches")
    @Expose
    private List<Match_> matches = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public List<Match_> getMatches() {
        return matches;
    }

    public void setMatches(List<Match_> matches) {
        this.matches = matches;
    }

    public static class Bet_ implements Parcelable {

        @SerializedName("bet")
        @Expose
        private uk.maxusint.maxus.network.model.Bet bet;
        @SerializedName("bet_rates")
        @Expose
        private List<BetRate> betRates = null;

        protected Bet_(Parcel in) {
            bet = in.readParcelable(Bet.class.getClassLoader());
            betRates = in.createTypedArrayList(BetRate.CREATOR);
        }

        public static final Creator<Bet_> CREATOR = new Creator<Bet_>() {
            @Override
            public Bet_ createFromParcel(Parcel in) {
                return new Bet_(in);
            }

            @Override
            public Bet_[] newArray(int size) {
                return new Bet_[size];
            }
        };

        public uk.maxusint.maxus.network.model.Bet getBet() {
            return bet;
        }

        public void setBet(uk.maxusint.maxus.network.model.Bet bet) {
            this.bet = bet;
        }


        public void setBetRates(List<BetRate> betRates) {
            this.betRates = betRates;
        }

        public List<BetRate> getBetRates() {
            return betRates;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(bet, flags);
            dest.writeTypedList(betRates);
        }
    }

    public static class Match_ implements Parcelable {

        @SerializedName("match")
        @Expose
        private uk.maxusint.maxus.network.model.Match match;
        @SerializedName("bets")
        @Expose
        private List<Bet_> bets = null;


        protected Match_(Parcel in) {
            match = in.readParcelable(Match.class.getClassLoader());
            bets = in.createTypedArrayList(Bet_.CREATOR);
        }

        public static final Creator<Match_> CREATOR = new Creator<Match_>() {
            @Override
            public Match_ createFromParcel(Parcel in) {
                return new Match_(in);
            }

            @Override
            public Match_[] newArray(int size) {
                return new Match_[size];
            }
        };

        public uk.maxusint.maxus.network.model.Match getMatch() {
            return match;
        }

        public void setMatch(uk.maxusint.maxus.network.model.Match match) {
            this.match = match;
        }

        public List<Bet_> getBets() {
            return bets;
        }

        public void setBets(List<Bet_> bets) {
            this.bets = bets;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(match, flags);
            dest.writeTypedList(bets);
        }
    }

}
