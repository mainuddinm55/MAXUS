package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.SerializedName;

public class Pin {
    @SerializedName("id")
    private int id;
    @SerializedName("pin")
    private String pin;
    @SerializedName("user_type_id")
    private int userTypeId;
    @SerializedName("used")
    private int used;
    @SerializedName("validity")
    private int validity;
    @SerializedName("owner_id")
    private int ownerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getUserTypeId() {
        return userTypeId;
    }

    public void setUserTypeId(int userTypeId) {
        this.userTypeId = userTypeId;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getValidity() {
        return validity;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
