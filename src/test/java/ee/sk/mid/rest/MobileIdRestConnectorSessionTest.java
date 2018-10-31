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
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusRunning.json");

        assertThat(sessionStatus, is(notNullValue()));
        assertThat(sessionStatus.getState(), is("RUNNING"));
    }

    @Test
    public void getSessionStatus_forSuccessfulAuthenticationRequest() throws Exception {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusForSuccessfulAuthenticationRequest.json");

        assertSuccessfulSessionStatus(sessionStatus);

        assertThat(sessionStatus.getSignature(), is(notNullValue()));
        assertThat(sessionStatus.getSignature().getValueInBase64(), startsWith("luvjsi1+1iLN9yfDFEh/BE8hXtAKhAIxilv"));
        assertThat(sessionStatus.getSignature().getAlgorithm(), is("sha256WithRSAEncryption"));
        assertThat(sessionStatus.getCertificate(), startsWith("MIIHhjCCBW6gAwIBAgIQDNYLtVwrKURYStr"));
    }

    @Test
    public void getSessionStatus_whenTimeout() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenTimeout.json");
        assertErrorSessionStatus(sessionStatus, "TIMEOUT");
    }

    @Test
    public void getSessionStatus_whenError() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenError.json");
        assertErrorSessionStatus(sessionStatus, "ERROR");
    }

    @Test
    public void getSessionStatus_whenNotMIDClient() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenNotMIDClient.json");
        assertErrorSessionStatus(sessionStatus, "NOT_MID_CLIENT");
    }

    @Test
    public void getSessionStatus_whenExpiredTransaction() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenExpiredTransaction.json");
        assertErrorSessionStatus(sessionStatus, "EXPIRED_TRANSACTION");
    }

    @Test
    public void getSessionStatus_whenUserCancelled() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenUserCancelled.json");
        assertErrorSessionStatus(sessionStatus, "USER_CANCELLED");
    }

    @Test
    public void getSessionStatus_whenMIDNotReady() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenMIDNotReady.json");
        assertErrorSessionStatus(sessionStatus, "MID_NOT_READY");
    }

    @Test
    public void getSessionStatus_whenPhoneAbsent() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenPhoneAbsent.json");
        assertErrorSessionStatus(sessionStatus, "PHONE_ABSENT");
    }

    @Test
    public void getSessionStatus_whenDeliveryError() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenDeliveryError.json");
        assertErrorSessionStatus(sessionStatus, "DELIVERY_ERROR");
    }

    @Test
    public void getSessionStatus_whenSimError() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenSimError.json");
        assertErrorSessionStatus(sessionStatus, "SIM_ERROR");
    }

    @Test
    public void getSessionStatus_whenSignatureHashMismatch() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenSignatureHashMismatch.json");
        assertErrorSessionStatus(sessionStatus, "SIGNATURE_HASH_MISMATCH");
    }

    private SessionStatus getStubbedSessionStatusWithResponse(String responseFile) throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/de305d54-75b4-431b-adb2-eb6b9e546016", responseFile);
        SessionStatusRequest request = new SessionStatusRequest("de305d54-75b4-431b-adb2-eb6b9e546016");
        return connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
    }
}
