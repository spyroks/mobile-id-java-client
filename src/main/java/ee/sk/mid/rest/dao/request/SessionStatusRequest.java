package ee.sk.mid.rest.dao.request;

import java.io.Serializable;

public class SessionStatusRequest implements Serializable {

    private String sessionId;

    public SessionStatusRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
