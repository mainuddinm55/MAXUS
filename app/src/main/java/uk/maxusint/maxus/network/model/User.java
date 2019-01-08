package uk.maxusint.maxus.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @SerializedName("user_id")
    @Expose
    private int userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("club_id")
    @Expose
    private int clubId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("agent_id")
    @Expose
    private int agentId;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("upazilla")
    @Expose
    private String upazilla;
    @SerializedName("up")
    @Expose
    private String up;
    @SerializedName("total_balance")
    @Expose
    private double totalBalance;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("rank_id")
    @Expose
    private int rankId;
    @SerializedName("type_id")
    @Expose
    private int typeId;

    public User(int userId, String name, String username, String email, String mobile, int clubId, String reference, int agentId, String district, String upazilla, String up, double totalBalance, String status, int rankId, int typeId) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.clubId = clubId;
        this.reference = reference;
        this.agentId = agentId;
        this.district = district;
        this.upazilla = upazilla;
        this.up = up;
        this.totalBalance = totalBalance;
        this.status = status;
        this.rankId = rankId;
        this.typeId = typeId;
    }

    protected User(Parcel in) {
        userId = in.readInt();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        mobile = in.readString();
        clubId = in.readInt();
        reference = in.readString();
        agentId = in.readInt();
        district = in.readString();
        upazilla = in.readString();
        up = in.readString();
        totalBalance = in.readDouble();
        status = in.readString();
        rankId = in.readInt();
        typeId = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUpazilla() {
        return upazilla;
    }

    public void setUpazilla(String upazilla) {
        this.upazilla = upazilla;
    }

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRankId() {
        return rankId;
    }

    public void setRankId(int rankId) {
        this.rankId = rankId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeInt(clubId);
        dest.writeString(reference);
        dest.writeInt(agentId);
        dest.writeString(district);
        dest.writeString(upazilla);
        dest.writeString(up);
        dest.writeDouble(totalBalance);
        dest.writeString(status);
        dest.writeInt(rankId);
        dest.writeInt(typeId);
    }


    public class UserType {
        public static final int ADMIN = 1;
        public static final int CLUB = 2;
        public static final int AGENT = 3;
        public static final int ROYAL = 4;
        public static final int CLASSIC = 5;
        public static final int PREMIUM = 6;

    }

    public class UserRank {
        public static final int GENERAL_MEMBER = 1;
        public static final int ASSOCIATE = 2;
        public static final int SR_ASSOCIATE = 3;
        public static final int BRONZE = 4;
        public static final int SILVER = 5;
        public static final int GOLD = 6;
        public static final int P_D = 7;
        public static final int A_M = 8;

        public class UserRankName {
            public static final String GENERAL_MEMBER = "General Member";
            public static final String ASSOCIATE = "Associate";
            public static final String SR_ASSOCIATE = "Sr. Associate";
            public static final String BRONZE = "Bronze";
            public static final String SILVER = "Silver";
            public static final String GOLD = "Gold";
            public static final String P_D = "P.D";
            public static final String A_M = "A.M";
        }
    }
}
