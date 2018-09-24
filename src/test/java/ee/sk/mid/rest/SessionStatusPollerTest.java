package ee.sk.mid.rest;

import ee.sk.mid.exception.*;
import ee.sk.mid.rest.dao.SessionStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static ee.sk.mid.test.DummyData.*;
import static org.junit.Assert.*;

public class SessionStatusPollerTest {

    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";

    private MobileIdConnectorStub connector;
    private SessionStatusPoller poller;

    @Before
    public void setUp() {
        connector = new MobileIdConnectorStub();
        poller = new SessionStatusPoller(connector);
        poller.setPollingSleepTime(TimeUnit.MILLISECONDS, 1L);
    }

    @Test
    public void getFirstCompleteResponse() {
        connector.responses.add(createCompleteSessionStatus());
        SessionStatus status = poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
        assertEquals("97f5058e-e308-4c83-ac14-7712b0eb9d86", connector.sessionIdUsed);
        assertEquals(1, connector.responseNumber);
        assertCompleteStateReceived(status);
    }

    @Test
    public void pollAndGetThirdCompleteResponse() {
        connector.responses.add(createRunningSessionStatus());
        connector.responses.add(createRunningSessionStatus());
        connector.responses.add(createCompleteSessionStatus());
        SessionStatus status = poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
        assertEquals(3, connector.responseNumber);
        assertCompleteStateReceived(status);
    }

    @Test
    public void setPollingSleepTime() {
        poller.setPollingSleepTime(TimeUnit.MILLISECONDS, 200L);
        addMultipleRunningSessionResponses(5);
        connector.responses.add(createCompleteSessionStatus());
        long duration = measurePollingDuration();
        assertTrue(duration > 1000L);
        assertTrue(duration < 1100L);
    }

    @Test
    public void setResponseSocketOpenTime() {
        poller.setResponseSocketOpenTime(TimeUnit.MINUTES, 2L);
        connector.responses.add(createCompleteSessionStatus());
        SessionStatus status = poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
        assertCompleteStateReceived(status);
        assertTrue(connector.requestUsed.isResponseSocketOpenTimeSet());
        assertEquals(TimeUnit.MINUTES, connector.requestUsed.getResponseSocketOpenTimeUnit());
        assertEquals(2L, connector.requestUsed.getResponseSocketOpenTimeValue());
    }

    @Test
    public void responseSocketOpenTimeShouldNotBeSetByDefault() {
        connector.responses.add(createCompleteSessionStatus());
        SessionStatus status = poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
        assertCompleteStateReceived(status);
        assertFalse(connector.requestUsed.isResponseSocketOpenTimeSet());
    }

    @Test(expected = SessionTimeoutException.class)
    public void getUserTimeoutResponse_shouldThrowException() {
        connector.responses.add(createTimeoutSessionStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ResponseRetrievingException.class)
    public void getResponseRetrievingErrorResponse_shouldThrowException() {
        connector.responses.add(createResponseRetrievingErrorStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = NotMIDClientException.class)
    public void getNotMIDClientResponse_shouldThrowException() {
        connector.responses.add(createNotMIDClientStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ExpiredTransactionException.class)
    public void getMSSSPTransactionExpiredResponse_shouldThrowException() {
        connector.responses.add(createMSSPTransactionExpiredStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = UserCancellationException.class)
    public void getUserCancellationResponse_shouldThrowException() {
        connector.responses.add(createUserCancellationStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = MIDNotReadyException.class)
    public void getMIDNotReadyResponse_shouldThrowException() {
        connector.responses.add(createMIDNotReadyStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = SimNotAvailableException.class)
    public void getSimNotAvailableResponse_shouldThrowException() {
        connector.responses.add(createSimNotAvailableStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = DeliveryException.class)
    public void getDeliveryErrorResponse_shouldThrowException() {
        connector.responses.add(createDeliveryErrorStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = InvalidCardResponseException.class)
    public void getInvalidCardResponse_shouldThrowException() {
        connector.responses.add(createInvalidCardResponseStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void getSignatureHashMismatchResponse_shouldThrowException() {
        connector.responses.add(createSignatureHashMismatchStatus());
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = TechnicalErrorException.class)
    public void getUnknownResult_shouldThrowException() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("BLAH");
        connector.responses.add(status);
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = TechnicalErrorException.class)
    public void getMissingResult_shouldThrowException() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult(null);
        connector.responses.add(status);
        poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
    }

    private long measurePollingDuration() {
        long startTime = System.currentTimeMillis();
        SessionStatus status = poller.fetchFinalSessionStatus("97f5058e-e308-4c83-ac14-7712b0eb9d86", AUTHENTICATION_SESSION_PATH);
        long endTime = System.currentTimeMillis();
        assertCompleteStateReceived(status);
        return endTime - startTime;
    }

    private void addMultipleRunningSessionResponses(int numberOfResponses) {
        for (int i = 0; i < numberOfResponses; i++)
            connector.responses.add(createRunningSessionStatus());
    }

    private void assertCompleteStateReceived(SessionStatus status) {
        assertNotNull(status);
        assertEquals("COMPLETE", status.getState());
    }

    private SessionStatus createCompleteSessionStatus() {
        SessionStatus sessionStatus = new SessionStatus();
        sessionStatus.setState("COMPLETE");
        sessionStatus.setResult(createSessionResult());
        return sessionStatus;
    }

    private SessionStatus createRunningSessionStatus() {
        SessionStatus status = new SessionStatus();
        status.setState("RUNNING");
        return status;
    }
}
