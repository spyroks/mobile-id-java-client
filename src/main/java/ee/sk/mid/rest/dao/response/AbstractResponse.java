package ee.sk.mid.rest.dao.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AbstractResponse {

    @JsonProperty("sessionID")
    private String sessionId;

    public AbstractResponse() {
    }

    public AbstractResponse(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }
}
