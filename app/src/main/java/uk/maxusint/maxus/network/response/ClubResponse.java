package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uk.maxusint.maxus.network.model.Club;

public class ClubResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("club")
    @Expose
    private Club club;

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

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

}