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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ee.sk.mid.mock.MobileIdRestServiceStubs.*;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

public class MobileIdRestConnectorAuthenticationTest {

    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector("http://localhost:18089");
    }

    @Test
    public void authenticate() throws IOException {
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequest.json", "responses/authenticationResponse.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        AuthenticationResponse response = connector.authenticate(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionId(), is("1dcc1600-29a6-4e95-a95c-d69b31febcfb"));
    }

    @Test
    public void authenticate_withDisplayText() throws IOException {
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequestWithDisplayText.json", "responses/authenticationResponse.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        request.setDisplayText("Log into internet banking system");
        AuthenticationResponse response = connector.authenticate(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionId(), is("1dcc1600-29a6-4e95-a95c-d69b31febcfb"));
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_whenGettingSessionIdFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);
    }

    @Test(expected = NotFoundException.class)
    public void authenticate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/authentication", "requests/authenticationRequest.json");
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
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequest.json", "responses/authenticationResponse.json");
        AuthenticationRequest request = createDummyAuthenticationSessionRequest();
        connector.authenticate(request);

        verify(postRequestedFor(urlEqualTo("/mid-api/authentication"))
                .withHeader(headerName, equalTo(headerValue)));
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
    public void getSessionStatus_forSuccessfulSigningRequest() throws Exception {
        SessionStatus sessionStatus = getStubbedSessionStatusWithResponse("responses/sessionStatusForSuccessfulSigningRequest.json");
        assertSuccessfulResponse(sessionStatus);
        assertThat(sessionStatus.getSignature(), is(notNullValue()));
        assertThat(sessionStatus.getSignature().getValueInBase64(), startsWith("luvjsi1+1iLN9yfDFEh/BE8hXtAKhAIxilv"));
        assertThat(sessionStatus.getSignature().getAlgorithm(), is("sha256WithRSAEncryption"));
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

    private SessionStatus getStubbedSessionStatusWithResponse(String responseFile) throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/de305d54-75b4-431b-adb2-eb6b9e546016", responseFile);
        SessionStatusRequest request = new SessionStatusRequest("de305d54-75b4-431b-adb2-eb6b9e546016");
        return connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
    }

    private void assertSessionStatusErrorWithResult(SessionStatus sessionStatus, String result) {
        assertThat(sessionStatus.getState(), is("COMPLETE"));
        assertThat(sessionStatus.getResult(), is(result));
    }

    private void assertSuccessfulResponse(SessionStatus sessionStatus) {
        assertThat(sessionStatus.getState(), is("COMPLETE"));
        assertThat(sessionStatus.getResult(), is(notNullValue()));
        assertThat(sessionStatus.getResult(), is("OK"));
    }

    private AuthenticationRequest createDummyAuthenticationSessionRequest() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1);
        request.setRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1);
        request.setPhoneNumber(VALID_PHONE_1);
        request.setNationalIdentityNumber(VALID_NAT_IDENTITY_1);
        request.setHash("kc42j4tGXa1Pc2LdMcJCKAgpOk9RCQgrBogF6fHA40VSPw1qITw8zQ8g5ZaLcW5jSlq67ehG3uSvQAWIFs3TOw==");
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
