package ee.sk.mid.mock;

import ee.sk.mid.rest.dao.SessionStatus;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SessionStatusDummy {

    public static SessionStatus createRunningSessionStatus() {
        SessionStatus sessionStatus = new SessionStatus();
        sessionStatus.setState("RUNNING");
        return sessionStatus;
    }

    public static SessionStatus createSuccessfulSessionStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("OK");
        return sessionStatus;
    }

    public static SessionStatus createTimeoutSessionStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("TIMEOUT");
        return sessionStatus;
    }

    public static SessionStatus createResponseRetrievingErrorStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("ERROR");
        return sessionStatus;
    }

    public static SessionStatus createNotMIDClientStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("NOT_MID_CLIENT");
        return sessionStatus;
    }

    public static SessionStatus createMSSPTransactionExpiredStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("EXPIRED_TRANSACTION");
        return sessionStatus;
    }

    public static SessionStatus createUserCancellationStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("USER_CANCELLED");
        return sessionStatus;
    }

    public static SessionStatus createMIDNotReadyStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("MID_NOT_READY");
        return sessionStatus;
    }

    public static SessionStatus createSimNotAvailableStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("PHONE_ABSENT");
        return sessionStatus;
    }

    public static SessionStatus createDeliveryErrorStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("DELIVERY_ERROR");
        return sessionStatus;
    }

    public static SessionStatus createInvalidCardResponseStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("SIM_ERROR");
        return sessionStatus;
    }

    public static SessionStatus createSignatureHashMismatchStatus() {
        SessionStatus sessionStatus = createCompleteSessionStatus();
        sessionStatus.setResult("SIGNATURE_HASH_MISMATCH");
        return sessionStatus;
    }

    private static SessionStatus createCompleteSessionStatus() {
        SessionStatus sessionStatus = new SessionStatus();
        sessionStatus.setState("COMPLETE");
        return sessionStatus;
    }

    public static void assertCompleteSessionStatus(SessionStatus sessionStatus) {
        assertThat(sessionStatus, is(notNullValue()));
        assertThat(sessionStatus.getState(), is("COMPLETE"));
    }

    public static void assertSuccessfulSessionStatus(SessionStatus sessionStatus) {
        assertThat(sessionStatus.getState(), is("COMPLETE"));
        assertThat(sessionStatus.getResult(), is("OK"));
    }

    public static void assertErrorSessionStatus(SessionStatus sessionStatus, String result) {
        assertThat(sessionStatus.getState(), is("COMPLETE"));
        assertThat(sessionStatus.getResult(), is(result));
    }
}
