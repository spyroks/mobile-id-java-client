package ee.sk.mid;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.exception.*;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
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
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.assertAuthenticationPolled;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.assertAuthenticationResponse;
import static ee.sk.mid.mock.MobileIdRestServiceStub.*;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MobileIdClientAuthenticationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdClient client;

    @Before
    public void setUp() throws IOException {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(VALID_RELYING_PARTY_UUID);
        client.setRelyingPartyName(VALID_RELYING_PARTY_NAME);
        client.setHostUrl(LOCALHOST_URL);
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequest.json", "responses/authenticationResponse.json");
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequestWithDisplayText.json", "responses/authenticationResponse.json");
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusForSuccessfulAuthenticationRequest.json");
    }

    @Test
    public void authenticate_withHash() {
        MobileIdAuthenticationHash authenticationHash = createAuthenticationSHA512Hash();
        assertThat(authenticationHash.calculateVerificationCode(), is("4677"));

        MobileIdAuthentication authentication = createAuthentication(client, VALID_PHONE, VALID_NAT_IDENTITY, authenticationHash);
        assertAuthenticationCreated(authentication, authenticationHash.getHashInBase64());
    }

    @Test
    public void authenticate_withSignableData() {
        SignableData dataToSign = new SignableData(DATA_TO_SIGN);
        dataToSign.setHashType(HashType.SHA512);

        AuthenticationRequest request = client
                .createAuthenticationRequestBuilder()
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withSignableData(dataToSign)
                .withLanguage(Language.EST)
                .build();

        assertCorrectAuthenticationRequestMade(request);

        AuthenticationResponse response = client.getMobileIdConnector().authenticate(request);
        assertAuthenticationResponse(response);

        SessionStatus sessionStatus = client.getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionId(), AUTHENTICATION_SESSION_PATH);
        assertAuthenticationPolled(sessionStatus);

        MobileIdAuthentication authentication = client.createMobileIdAuthentication(sessionStatus);
        assertAuthenticationCreated(authentication, dataToSign.calculateHashInBase64());
    }

    @Test
    public void authenticate_withDisplayText() {
        MobileIdAuthenticationHash authenticationHash = createAuthenticationSHA512Hash();
        assertThat(authenticationHash.calculateVerificationCode(), is("4677"));

        AuthenticationRequest request = client
                .createAuthenticationRequestBuilder()
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .withDisplayText("Log into internet banking system")
                .build();

        assertCorrectAuthenticationRequestMade(request);

        AuthenticationResponse response = client.getMobileIdConnector().authenticate(request);
        assertAuthenticationResponse(response);

        SessionStatus sessionStatus = client.getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionId(), AUTHENTICATION_SESSION_PATH);
        assertAuthenticationPolled(sessionStatus);

        MobileIdAuthentication authentication = client.createMobileIdAuthentication(sessionStatus);
        assertAuthenticationCreated(authentication, authenticationHash.getHashInBase64());
    }

    @Test(expected = SessionTimeoutException.class)
    public void authenticate_whenTimeout_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenTimeout.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_whenResponseRetrievingError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenError.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = NotMIDClientException.class)
    public void authenticate_whenNotMIDClient_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenNotMIDClient.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = ExpiredException.class)
    public void authenticate_whenMSSPTransactionExpired_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenExpiredTransaction.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = UserCancellationException.class)
    public void authenticate_whenUserCancelled_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenUserCancelled.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = MIDNotReadyException.class)
    public void authenticate_whenMIDNotReady_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenMIDNotReady.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = SimNotAvailableException.class)
    public void authenticate_whenSimNotAvailable_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenPhoneAbsent.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = DeliveryException.class)
    public void authenticate_whenDeliveryError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenDeliveryError.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = InvalidCardResponseException.class)
    public void authenticate_whenInvalidCardResponse_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenSimError.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void authenticate_whenSignatureHashMismatch_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenSignatureHashMismatch.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_whenGettingResponseFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = ResponseNotFoundException.class)
    public void authenticate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeValidAuthenticationRequest(client);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeValidAuthenticationRequest(client);
    }

    @Test
    public void setPollingSleepTimeoutForAuthentication() throws IOException {
        stubSessionStatusWithState("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusRunning.json", STARTED, "COMPLETE");
        stubSessionStatusWithState("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusForSuccessfulAuthenticationRequest.json", "COMPLETE", STARTED);
        client.setPollingSleepTimeout(TimeUnit.SECONDS, 2L);
        long duration = measureAuthenticationDuration();
        assertThat("Duration is " + duration, duration > 2000L, is(true));
        assertThat("Duration is " + duration, duration < 3000L, is(true));
    }

    @Test
    public void verifyAuthentication_withNetworkConnectionConfigurationHavingCustomHeader() {
        String headerName = "custom-header";
        String headerValue = "Auth";

        Map<String, String> headersToAdd = new HashMap<>();
        headersToAdd.put(headerName, headerValue);
        ClientConfig clientConfig = getClientConfigWithCustomRequestHeaders(headersToAdd);
        client.setNetworkConnectionConfig(clientConfig);
        makeValidAuthenticationRequest(client);

        verify(postRequestedFor(urlEqualTo("/mid-api/authentication"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    @Test
    public void verifyMobileIdConnector_whenConnectorIsNotProvided() {
        MobileIdConnector connector = client.getMobileIdConnector();
        assertThat(connector instanceof MobileIdRestConnector, is(true));
    }

    @Test
    public void verifyMobileIdConnector_whenConnectorIsProvided() {
        final String mock = "Mock";
        SessionStatus status = mock(SessionStatus.class);
        when(status.getState()).thenReturn(mock);
        MobileIdConnector connector = mock(MobileIdConnector.class);
        when(connector.getSessionStatus(null, null)).thenReturn(status);
        client.setMobileIdConnector(connector);
        assertThat(client.getMobileIdConnector().getSessionStatus(null, null).getState(), is(mock));
    }

    private long measureAuthenticationDuration() {
        long startTime = System.currentTimeMillis();
        MobileIdAuthenticationHash authenticationHash = createAuthenticationSHA512Hash();
        MobileIdAuthentication authentication = createAuthentication(client, VALID_PHONE, VALID_NAT_IDENTITY, authenticationHash);
        assertAuthenticationCreated(authentication, authenticationHash.getHashInBase64());
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    private ClientConfig getClientConfigWithCustomRequestHeaders(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }
}
