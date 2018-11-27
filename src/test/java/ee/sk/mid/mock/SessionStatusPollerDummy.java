package ee.sk.mid.mock;

import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SessionStatusPollerDummy {

    public static SessionStatus pollSessionStatus(MobileIdConnector connector, String sessionId, String path) throws InterruptedException {
        SessionStatus sessionStatus = null;
        while (sessionStatus == null || "RUNNING".equalsIgnoreCase(sessionStatus.getState())) {
            SessionStatusRequest request = new SessionStatusRequest(sessionId);
            sessionStatus = connector.getSessionStatus(request, path);
            TimeUnit.SECONDS.sleep(1);
        }
        assertThat(sessionStatus.getState(), is("COMPLETE"));
        return sessionStatus;
    }
}
