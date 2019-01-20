package uk.maxusint.maxus.network.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Notification implements Serializable {
    @SerializedName("type_id")
    private int id;
    @SerializedName("body")
    private String body;
    @SerializedName("type")
    private String type;
    @SerializedName("date")
    private String date;
    @SerializedName("isseen")
    private int isseen;

    public Notification(int id, String body, String type, String date, int isseen) {
        this.id = id;
        this.body = body;
        this.type = type;
        this.date = date;
        this.isseen = isseen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIsseen() {
        return isseen;
    }

    public void setIsseen(int isseen) {
        this.isseen = isseen;
    }

    public class Type {
        public static final String TRANSACTION = "Transaction";
    }
}
