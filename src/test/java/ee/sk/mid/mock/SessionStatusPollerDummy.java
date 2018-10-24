package ee.sk.mid.mock;

import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SessionStatusPollerDummy {

    public static SessionStatus pollSessionStatus(MobileIdConnector connector, String sessionId, String path) throws InterruptedException {
        SessionStatus status = null;
        while (status == null || StringUtils.equalsIgnoreCase("RUNNING", status.getState())) {
            SessionStatusRequest request = new SessionStatusRequest(sessionId);
            status = connector.getSessionStatus(request, path);
            TimeUnit.SECONDS.sleep(1);
        }
        assertThat(status.getState(), is("COMPLETE"));
        return status;
    }
}
