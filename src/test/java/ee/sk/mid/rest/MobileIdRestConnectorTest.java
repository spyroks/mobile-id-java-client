package ee.sk.mid.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.ClientRequestHeaderFilter;
import ee.sk.mid.HashType;
import ee.sk.mid.Language;
import ee.sk.mid.exception.*;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ee.sk.mid.test.MobileIdRestServiceStubs.*;
import static ee.sk.mid.test.TestData.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MobileIdRestConnectorTest {

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
        assertNotNull(sessionStatus);
        assertEquals("RUNNING", sessionStatus.getState());
    }

    @Test
    public void getSessionStatus_whenTimeout() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenTimeout.json");
        assertSessionStatusErrorWithResult(sessionStatus, "TIMEOUT");
    }

    @Test
    public void getSessionStatus_whenError() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenError.json");
        assertSessionStatusErrorWithResult(sessionStatus, "ERROR");
    }

    @Test
    public void getSessionStatus_whenNotMIDClient() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenNotMIDClient.json");
        assertSessionStatusErrorWithResult(sessionStatus, "NOT_MID_CLIENT");
    }

    @Test
    public void getSessionStatus_whenExpiredTransaction() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenExpiredTransaction.json");
        assertSessionStatusErrorWithResult(sessionStatus, "EXPIRED_TRANSACTION");
    }

    @Test
    public void getSessionStatus_whenUserCancelled() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenUserCancelled.json");
        assertSessionStatusErrorWithResult(sessionStatus, "USER_CANCELLED");
    }

    @Test
    public void getSessionStatus_whenMIDNotReady() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenMIDNotReady.json");
        assertSessionStatusErrorWithResult(sessionStatus, "MID_NOT_READY");
    }

    @Test
    public void getSessionStatus_whenPhoneAbsent() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenPhoneAbsent.json");
        assertSessionStatusErrorWithResult(sessionStatus, "PHONE_ABSENT");
    }

    @Test
    public void getSessionStatus_whenDeliveryError() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenDeliveryError.json");
        assertSessionStatusErrorWithResult(sessionStatus, "DELIVERY_ERROR");
    }

    @Test
    public void getSessionStatus_whenSimError() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenSimError.json");
        assertSessionStatusErrorWithResult(sessionStatus, "SIM_ERROR");
    }

    @Test
    public void getSessionStatus_whenSignatureHashMismatch() throws IOException {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusWhenSignatureHashMismatch.json");
        assertSessionStatusErrorWithResult(sessionStatus, "SIGNATURE_HASH_MISMATCH");
    }

    @Test
    public void getSessionStatus_withTimeoutParameter() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/de305d54-75b4-431b-adb2-eb6b9e546016", "responses/sessionStatusForSuccessfulAuthenticationRequest.json");
        SessionStatusRequest request = new SessionStatusRequest("de305d54-75b4-431b-adb2-eb6b9e546016");
        request.setResponseSocketOpenTime(TimeUnit.SECONDS, 10L);
        SessionStatus sessionStatus = connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
        assertSuccessfulResponse(sessionStatus);
        verify(getRequestedFor(urlEqualTo("/mid-api/authentication/session/de305d54-75b4-431b-adb2-eb6b9e546016?timeoutMs=10000")));
    }

    @Test
    public void authenticate() throws IOException {
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationSessionRequest.json", "responses/authenticationSessionResponse.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        AuthenticationResponse response = connector.authenticate(request);
        assertNotNull(response);
        assertEquals("1dcc1600-29a6-4e95-a95c-d69b31febcfb", response.getSessionId());
    }

    @Test
    public void authenticate_withDisplayText() throws IOException {
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationSessionRequestWithDisplayText.json", "responses/authenticationSessionResponse.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        request.setDisplayText("Log into internet banking system");
        AuthenticationResponse response = connector.authenticate(request);
        assertNotNull(response);
        assertEquals("1dcc1600-29a6-4e95-a95c-d69b31febcfb", response.getSessionId());
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_whenGettingSessionIdFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/authentication", "requests/authenticationSessionRequest.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);
    }

    @Test(expected = ResponseNotFound.class)
    public void authenticate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/authentication", "requests/authenticationSessionRequest.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/authentication", "requests/authenticationSessionRequest.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/authentication", "requests/authenticationSessionRequest.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);
    }

    @Test
    public void verifyCustomRequestHeaderPresent_whenAuthenticating() throws IOException {
        String headerName = "custom-header";
        String headerValue = "Auth";

        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        connector = new MobileIdRestConnector("http://localhost:18089", getClientConfigWithCustomRequestHeader(headers));
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationSessionRequest.json", "responses/authenticationSessionResponse.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);

        verify(postRequestedFor(urlEqualTo("/mid-api/authentication"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private SessionStatus getStubbedSessionStatusWithResponse(String responseFile) throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/de305d54-75b4-431b-adb2-eb6b9e546016", responseFile);
        SessionStatusRequest request = new SessionStatusRequest("de305d54-75b4-431b-adb2-eb6b9e546016");
        return connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
    }

    private void assertSessionStatusErrorWithResult(SessionStatus sessionStatus, String result) {
        assertEquals("COMPLETE", sessionStatus.getState());
        assertEquals(result, sessionStatus.getResult());
    }

    private void assertSuccessfulResponse(SessionStatus sessionStatus) {
        assertEquals("COMPLETE", sessionStatus.getState());
        assertNotNull(sessionStatus.getResult());
        assertEquals("OK", sessionStatus.getResult());
    }

    private AuthenticationRequest createDummyAuthenticationSessionRequest() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1);
        request.setRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1);
        request.setPhoneNumber(VALID_PHONE_1);
        request.setNationalIdentityNumber(VALID_NAT_IDENTITY_1);
        request.setHash("K74MSLkafRuKZ1Ooucvh2xa4Q3nz+R/hFWIShN96SPHNcem+uQ6mFMe9kkJQqp5EaoZnJeaFpl310TmlzRgNyQ==");
        request.setHashType(HashType.SHA512);
        request.setLanguage(Language.EST);
        return request;
    }

    private ClientConfig getClientConfigWithCustomRequestHeader(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }
}
