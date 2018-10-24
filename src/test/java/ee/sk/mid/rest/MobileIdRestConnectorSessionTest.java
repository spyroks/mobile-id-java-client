package ee.sk.mid.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static ee.sk.mid.mock.MobileIdRestServiceStub.stubNotFoundResponse;
import static ee.sk.mid.mock.MobileIdRestServiceStub.stubRequestWithResponse;
import static ee.sk.mid.mock.SessionStatusDummy.assertErrorSessionStatus;
import static ee.sk.mid.mock.SessionStatusDummy.assertSuccessfulSessionStatus;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

public class MobileIdRestConnectorSessionTest {

    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector("http://localhost:18089");
    }

    @Test(expected = SessionNotFoundException.class)
    public void getNotExistingSessionStatus() {
        stubNotFoundResponse("/mid-api/authentication/session/de305d54-75b4-431b-adb2-eb6b9e546016");
        SessionStatusRequest request = new SessionStatusRequest("de305d54-75b4-431b-adb2-eb6b9e546016");
        connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
    }

    @Test
    public void getRunningSessionStatus() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusRunning.json");

        assertThat(status, is(notNullValue()));
        assertThat(status.getState(), is("RUNNING"));
    }

    @Test
    public void getSessionStatus_forSuccessfulAuthenticationRequest() throws Exception {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusForSuccessfulAuthenticationRequest.json");

        assertSuccessfulSessionStatus(status);

        assertThat(status.getSignature(), is(notNullValue()));
        assertThat(status.getSignature().getValueInBase64(), startsWith("luvjsi1+1iLN9yfDFEh/BE8hXtAKhAIxilv"));
        assertThat(status.getSignature().getAlgorithm(), is("sha256WithRSAEncryption"));
        assertThat(status.getCertificate(), startsWith("MIIHhjCCBW6gAwIBAgIQDNYLtVwrKURYStr"));
    }

    @Test
    public void getSessionStatus_whenTimeout() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenTimeout.json");
        assertErrorSessionStatus(status, "TIMEOUT");
    }

    @Test
    public void getSessionStatus_whenError() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenError.json");
        assertErrorSessionStatus(status, "ERROR");
    }

    @Test
    public void getSessionStatus_whenNotMIDClient() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenNotMIDClient.json");
        assertErrorSessionStatus(status, "NOT_MID_CLIENT");
    }

    @Test
    public void getSessionStatus_whenExpiredTransaction() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenExpiredTransaction.json");
        assertErrorSessionStatus(status, "EXPIRED_TRANSACTION");
    }

    @Test
    public void getSessionStatus_whenUserCancelled() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenUserCancelled.json");
        assertErrorSessionStatus(status, "USER_CANCELLED");
    }

    @Test
    public void getSessionStatus_whenMIDNotReady() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenMIDNotReady.json");
        assertErrorSessionStatus(status, "MID_NOT_READY");
    }

    @Test
    public void getSessionStatus_whenPhoneAbsent() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenPhoneAbsent.json");
        assertErrorSessionStatus(status, "PHONE_ABSENT");
    }

    @Test
    public void getSessionStatus_whenDeliveryError() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenDeliveryError.json");
        assertErrorSessionStatus(status, "DELIVERY_ERROR");
    }

    @Test
    public void getSessionStatus_whenSimError() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenSimError.json");
        assertErrorSessionStatus(status, "SIM_ERROR");
    }

    @Test
    public void getSessionStatus_whenSignatureHashMismatch() throws IOException {
        SessionStatus status = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenSignatureHashMismatch.json");
        assertErrorSessionStatus(status, "SIGNATURE_HASH_MISMATCH");
    }

    private SessionStatus getStubbedSessionStatusWithResponse(String responseFile) throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/de305d54-75b4-431b-adb2-eb6b9e546016", responseFile);
        SessionStatusRequest request = new SessionStatusRequest("de305d54-75b4-431b-adb2-eb6b9e546016");
        return connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
    }
}
