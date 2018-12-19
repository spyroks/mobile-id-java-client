package ee.sk.mid.rest.dao.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SessionStatusRequest implements Serializable {

    @JsonProperty(value = "sessionId")
    private String sessionID;

    public SessionStatusRequest(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }
}
