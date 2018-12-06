package ee.sk.mid.rest.dao.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractResponse implements Serializable {

    private String sessionID;

    public AbstractResponse() {
    }

    public AbstractResponse(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "sessionID='" + sessionID + '\'' +
                '}';
    }
}
