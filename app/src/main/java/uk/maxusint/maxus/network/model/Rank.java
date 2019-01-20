package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rank {

    @SerializedName("rank_id")
    @Expose
    private Integer rankId;
    @SerializedName("rank_name")
    @Expose
    private String rankName;
    @SerializedName("assert_need")
    @Expose
    private Integer assertNeed;
    @SerializedName("bonus")
    @Expose
    private Integer bonus;

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public Integer getAssertNeed() {
        return assertNeed;
    }

    public void setAssertNeed(Integer assertNeed) {
        this.assertNeed = assertNeed;
    }

    public Integer getBonus() {
        return bonus;
    }

    public void setBonus(Integer bonus) {
        this.bonus = bonus;
    }

}
