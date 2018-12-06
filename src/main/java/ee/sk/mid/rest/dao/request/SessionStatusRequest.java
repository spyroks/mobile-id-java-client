package ee.sk.mid.rest.dao.request;

import java.io.Serializable;

public class SessionStatusRequest implements Serializable {

    private String sessionID;

    public SessionStatusRequest(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }
}
