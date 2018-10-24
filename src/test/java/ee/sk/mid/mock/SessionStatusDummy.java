package ee.sk.mid.mock;

import ee.sk.mid.rest.dao.SessionStatus;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SessionStatusDummy {

    public static SessionStatus createRunningSessionStatus() {
        SessionStatus status = new SessionStatus();
        status.setState("RUNNING");
        return status;
    }

    public static SessionStatus createSuccessfulSessionStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("OK");
        return status;
    }

    public static SessionStatus createTimeoutSessionStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("TIMEOUT");
        return status;
    }

    public static SessionStatus createResponseRetrievingErrorStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("ERROR");
        return status;
    }

    public static SessionStatus createNotMIDClientStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("NOT_MID_CLIENT");
        return status;
    }

    public static SessionStatus createMSSPTransactionExpiredStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("EXPIRED_TRANSACTION");
        return status;
    }

    public static SessionStatus createUserCancellationStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("USER_CANCELLED");
        return status;
    }

    public static SessionStatus createMIDNotReadyStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("MID_NOT_READY");
        return status;
    }

    public static SessionStatus createSimNotAvailableStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("PHONE_ABSENT");
        return status;
    }

    public static SessionStatus createDeliveryErrorStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("DELIVERY_ERROR");
        return status;
    }

    public static SessionStatus createInvalidCardResponseStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("SIM_ERROR");
        return status;
    }

    public static SessionStatus createSignatureHashMismatchStatus() {
        SessionStatus status = createCompleteSessionStatus();
        status.setResult("SIGNATURE_HASH_MISMATCH");
        return status;
    }

    private static SessionStatus createCompleteSessionStatus() {
        SessionStatus status = new SessionStatus();
        status.setState("COMPLETE");
        return status;
    }

    public static void assertCompleteSessionStatus(SessionStatus status) {
        assertThat(status, is(notNullValue()));
        assertThat(status.getState(), is("COMPLETE"));
    }

    public static void assertSuccessfulSessionStatus(SessionStatus status) {
        assertThat(status.getState(), is("COMPLETE"));
        assertThat(status.getResult(), is("OK"));
    }

    public static void assertErrorSessionStatus(SessionStatus status, String result) {
        assertThat(status.getState(), is("COMPLETE"));
        assertThat(status.getResult(), is(result));
    }
}
