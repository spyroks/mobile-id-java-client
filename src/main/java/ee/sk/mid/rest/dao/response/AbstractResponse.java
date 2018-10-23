package ee.sk.mid.rest.dao.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractResponse implements Serializable {

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
