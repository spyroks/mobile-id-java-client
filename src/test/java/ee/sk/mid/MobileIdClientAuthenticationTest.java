package ee.sk.mid;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.exception.*;
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
import static ee.sk.mid.mock.MobileIdRestServiceStubs.*;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class MobileIdClientAuthenticationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdClient client;

    @Before
    public void setUp() throws IOException {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1);
        client.setRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1);
        client.setHostUrl("http://localhost:18089");
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequest.json", "responses/authenticationResponse.json");
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequestWithDisplayText.json", "responses/authenticationResponse.json");
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusForSuccessfulAuthenticationRequest.json");
    }

    @Test
    public void authenticate() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("kc42j4tGXa1Pc2LdMcJCKAgpOk9RCQgrBogF6fHA40VSPw1qITw8zQ8g5ZaLcW5jSlq67ehG3uSvQAWIFs3TOw==");
        authenticationHash.setHashType(HashType.SHA512);

        assertThat(authenticationHash.calculateVerificationCode(), is("4677"));

        MobileIdAuthentication authentication = client
                .createAuthentication()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();

        assertAuthenticationResponseValid(authentication);
    }

    @Test
    public void authenticate_withDisplayText() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("kc42j4tGXa1Pc2LdMcJCKAgpOk9RCQgrBogF6fHA40VSPw1qITw8zQ8g5ZaLcW5jSlq67ehG3uSvQAWIFs3TOw==");
        authenticationHash.setHashType(HashType.SHA512);

        assertThat(authenticationHash.calculateVerificationCode(), is("4677"));

        MobileIdAuthentication authentication = client
                .createAuthentication()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .withDisplayText("Log into internet banking system")
                .authenticate();

        assertAuthenticationResponseValid(authentication);
    }

    @Test(expected = SessionTimeoutException.class)
    public void authenticate_whenTimeout_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenTimeout.json");
        makeAuthenticationRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_whenResponseRetrievingError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenError.json");
        makeAuthenticationRequest();
    }

    @Test(expected = NotMIDClientException.class)
    public void authenticate_whenNotMIDClient_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenNotMIDClient.json");
        makeAuthenticationRequest();
    }

    @Test(expected = ExpiredTransactionException.class)
    public void authenticate_whenMSSPTransactionExpired_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenExpiredTransaction.json");
        makeAuthenticationRequest();
    }

    @Test(expected = UserCancellationException.class)
    public void authenticate_whenUserCancelled_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenUserCancelled.json");
        makeAuthenticationRequest();
    }

    @Test(expected = MIDNotReadyException.class)
    public void authenticate_whenMIDNotReady_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenMIDNotReady.json");
        makeAuthenticationRequest();
    }

    @Test(expected = SimNotAvailableException.class)
    public void authenticate_whenSimNotAvailable_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenPhoneAbsent.json");
        makeAuthenticationRequest();
    }

    @Test(expected = DeliveryException.class)
    public void authenticate_whenDeliveryError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenDeliveryError.json");
        makeAuthenticationRequest();
    }

    @Test(expected = InvalidCardResponseException.class)
    public void authenticate_whenInvalidCardResponse_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenSimError.json");
        makeAuthenticationRequest();
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void authenticate_whenSignatureHashMismatch_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/authentication/session/1dcc1600-29a6-4e95-a95c-d69b31febcfb", "responses/sessionStatusWhenSignatureHashMismatch.json");
        makeAuthenticationRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_whenGettingSessionIdFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeAuthenticationRequest();
    }

    @Test(expected = ResponseNotFound.class)
    public void authenticate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeAuthenticationRequest();
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeAuthenticationRequest();
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        makeAuthenticationRequest();
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
        String headerValue = "HACKERMAN";

        Map<String, String> headersToAdd = new HashMap<>();
        headersToAdd.put(headerName, headerValue);
        ClientConfig clientConfig = getClientConfigWithCustomRequestHeaders(headersToAdd);
        client.setNetworkConnectionConfig(clientConfig);
        makeAuthenticationRequest();

        verify(postRequestedFor(urlEqualTo("/mid-api/authentication"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private long measureAuthenticationDuration() {
        long startTime = System.currentTimeMillis();
        MobileIdAuthentication authentication = createAuthentication();
        long endTime = System.currentTimeMillis();
        assertThat(authentication, is(notNullValue()));
        return endTime - startTime;
    }

    private MobileIdAuthentication createAuthentication() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("kc42j4tGXa1Pc2LdMcJCKAgpOk9RCQgrBogF6fHA40VSPw1qITw8zQ8g5ZaLcW5jSlq67ehG3uSvQAWIFs3TOw==");
        authenticationHash.setHashType(HashType.SHA512);

        return client
                .createAuthentication()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    private void makeAuthenticationRequest() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("kc42j4tGXa1Pc2LdMcJCKAgpOk9RCQgrBogF6fHA40VSPw1qITw8zQ8g5ZaLcW5jSlq67ehG3uSvQAWIFs3TOw==");
        authenticationHash.setHashType(HashType.SHA512);

        client
                .createAuthentication()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    private ClientConfig getClientConfigWithCustomRequestHeaders(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }

    private void assertAuthenticationResponseValid(MobileIdAuthentication authentication) {
        assertThat(authentication, is(notNullValue()));
        assertThat(authentication.getResult(), is("OK"));
        assertThat(authentication.getSignatureValueInBase64(), startsWith("luvjsi1+1iLN9yfDFEh/BE8h"));
        assertThat(authentication.getAlgorithmName(), is("sha256WithRSAEncryption"));
        assertThat(authentication.getCertificate(), is(notNullValue()));
        assertThat(authentication.getSignedHashInBase64(), is("kc42j4tGXa1Pc2LdMcJCKAgpOk9RCQgrBogF6fHA40VSPw1qITw8zQ8g5ZaLcW5jSlq67ehG3uSvQAWIFs3TOw=="));
    }
}
