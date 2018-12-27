package uk.maxusint.maxus.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BetRate implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("bet_id")
    @Expose
    private Integer betId;
    @SerializedName("options")
    @Expose
    private String options;
    @SerializedName("rate")
    @Expose
    private Double rate;
    @SerializedName("user_type_id")
    @Expose
    private Integer userTypeId;
    @SerializedName("bet_mode_id")
    @Expose
    private Integer betModeId;

    protected BetRate(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            betId = null;
        } else {
            betId = in.readInt();
        }
        options = in.readString();
        if (in.readByte() == 0) {
            rate = null;
        } else {
            rate = in.readDouble();
        }
        if (in.readByte() == 0) {
            userTypeId = null;
        } else {
            userTypeId = in.readInt();
        }
        if (in.readByte() == 0) {
            betModeId = null;
        } else {
            betModeId = in.readInt();
        }
    }

    public static final Creator<BetRate> CREATOR = new Creator<BetRate>() {
        @Override
        public BetRate createFromParcel(Parcel in) {
            return new BetRate(in);
        }

        @Override
        public BetRate[] newArray(int size) {
            return new BetRate[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBetId() {
        return betId;
    }

    public void setBetId(Integer betId) {
        this.betId = betId;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(Integer userTypeId) {
        this.userTypeId = userTypeId;
    }

    public Integer getBetModeId() {
        return betModeId;
    }

    public void setBetModeId(Integer betModeId) {
        this.betModeId = betModeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (betId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(betId);
        }
        dest.writeString(options);
        if (rate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rate);
        }
        if (userTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userTypeId);
        }
        if (betModeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(betModeId);
        }
    }
}
