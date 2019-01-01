package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
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
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("agent_id")
    @Expose
    private Integer agentId;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("upazilla")
    @Expose
    private String upazilla;
    @SerializedName("up")
    @Expose
    private String up;
    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("advanced_balance")
    @Expose
    private Integer advancedBalance;
    @SerializedName("trade_balance")
    @Expose
    private Object tradeBalance;
    @SerializedName("rank_id")
    @Expose
    private Integer rankId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
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

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getAdvancedBalance() {
        return advancedBalance;
    }

    public void setAdvancedBalance(Integer advancedBalance) {
        this.advancedBalance = advancedBalance;
    }

    public Object getTradeBalance() {
        return tradeBalance;
    }

    public void setTradeBalance(Object tradeBalance) {
        this.tradeBalance = tradeBalance;
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }

}
