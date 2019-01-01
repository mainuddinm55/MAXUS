package uk.maxusint.maxus.network.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uk.maxusint.maxus.network.model.Admin;
import uk.maxusint.maxus.network.model.User;

public class AdminResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("admin")
    @Expose
    private Admin admin;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

}
