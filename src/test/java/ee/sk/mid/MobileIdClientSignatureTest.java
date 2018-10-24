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
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.MobileIdRestServiceStub.*;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MobileIdClientSignatureTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdClient client;

    @Before
    public void setUp() throws IOException {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(VALID_RELYING_PARTY_UUID);
        client.setRelyingPartyName(VALID_RELYING_PARTY_NAME);
        client.setHostUrl("http://localhost:18089");
        stubRequestWithResponse("/mid-api/signature", "requests/signatureRequest.json", "responses/signatureResponse.json");
        stubRequestWithResponse("/mid-api/signature", "requests/signatureRequestWithDisplayText.json", "responses/signatureResponse.json");
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusForSuccessfulSigningRequest.json");
    }

    @Test
    public void sign() {
        MobileIdSignature signature = createValidSignature(client);

        assertSignatureCreated(signature);
    }

    @Test
    public void sign_withDisplayText() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        assertThat(hashToSign.calculateVerificationCode(), is("0108"));

        MobileIdSignature signature = client
                .createSignature()
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .withDisplayText("Authorize transfer of €10")
                .sign();

        assertSignatureCreated(signature);
    }

    @Test(expected = SessionTimeoutException.class)
    public void sign_whenTimeout_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenTimeout.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_whenResponseRetrievingError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenError.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = NotMIDClientException.class)
    public void sign_whenNotMIDClient_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenNotMIDClient.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = ExpiredException.class)
    public void sign_whenMSSPTransactionExpired_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenExpiredTransaction.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = UserCancellationException.class)
    public void sign_whenUserCancelled_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenUserCancelled.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = MIDNotReadyException.class)
    public void sign_whenMIDNotReady_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenMIDNotReady.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = SimNotAvailableException.class)
    public void sign_whenSimNotAvailable_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenPhoneAbsent.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = DeliveryException.class)
    public void sign_whenDeliveryError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenDeliveryError.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = InvalidCardResponseException.class)
    public void sign_whenInvalidCardResponse_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenSimError.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void sign_whenSignatureHashMismatch_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenSignatureHashMismatch.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_whenGettingResponseFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = ResponseNotFoundException.class)
    public void sign_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeValidSignatureRequest(client);
    }

    @Test(expected = UnauthorizedException.class)
    public void sign_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeValidSignatureRequest(client);
    }

    @Test
    public void setPollingSleepTimeoutForSignatureCreation() throws Exception {
        stubSessionStatusWithState("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusRunning.json", STARTED, "COMPLETE");
        stubSessionStatusWithState("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusForSuccessfulSigningRequest.json", "COMPLETE", STARTED);
        client.setPollingSleepTimeout(TimeUnit.SECONDS, 2L);
        long duration = measureSigningDuration();
        assertThat("Duration is " + duration, duration > 2000L, is(true));
        assertThat("Duration is " + duration, duration < 3000L, is(true));
    }

    @Test
    public void verifySigning_withNetworkConnectionConfigurationHavingCustomHeader() {
        String headerName = "custom-header";
        String headerValue = "Sign";

        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        ClientConfig clientConfig = getClientConfigWithCustomRequestHeaders(headers);
        client.setNetworkConnectionConfig(clientConfig);
        makeValidSignatureRequest(client);

        verify(postRequestedFor(urlEqualTo("/mid-api/signature"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private long measureSigningDuration() {
        long startTime = System.currentTimeMillis();
        MobileIdSignature signature = createValidSignature(client);
        long endTime = System.currentTimeMillis();
        assertThat(signature, is(notNullValue()));
        return endTime - startTime;
    }

    private ClientConfig getClientConfigWithCustomRequestHeaders(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }
}
