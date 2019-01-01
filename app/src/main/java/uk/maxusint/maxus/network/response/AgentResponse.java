package uk.maxusint.maxus.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uk.maxusint.maxus.network.model.Agent;

public class AgentResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("agent")
    @Expose
    private Agent agent;

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

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

}
