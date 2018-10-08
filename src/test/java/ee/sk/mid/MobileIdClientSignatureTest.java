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
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static ee.sk.mid.mock.MobileIdRestServiceStubs.*;
import static ee.sk.mid.mock.MobileIdRestServiceStubs.stubSessionStatusWithState;
import static ee.sk.mid.mock.MobileIdRestServiceStubs.stubUnauthorizedResponse;
import static ee.sk.mid.mock.TestData.*;
import static ee.sk.mid.mock.TestData.VALID_NAT_IDENTITY_1;
import static ee.sk.mid.mock.TestData.VALID_PHONE_1;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class MobileIdClientSignatureTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdClient client;

    @Before
    public void setUp() throws IOException {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1);
        client.setRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1);
        client.setHostUrl("http://localhost:18089");
        stubRequestWithResponse("/mid-api/signature", "requests/signatureRequest.json", "responses/signatureResponse.json");
        stubRequestWithResponse("/mid-api/signature", "requests/signatureRequestWithDisplayText.json", "responses/signatureResponse.json");
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusForSuccessfulSigningRequest.json");
    }

    @Test
    public void sign() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("AE7S1QxYjqtVv+Tgukv2bMMi9gDCbc9ca2vy/iIG6ug=");
        hashToSign.setHashType(HashType.SHA256);

        assertThat(hashToSign.calculateVerificationCode(), is("0108"));

        MobileIdSignature signature = client
                .createSignature()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();

        assertValidSignatureCreated(signature);
    }

    @Test
    public void sign_withDisplayText() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("AE7S1QxYjqtVv+Tgukv2bMMi9gDCbc9ca2vy/iIG6ug=");
        hashToSign.setHashType(HashType.SHA256);

        assertThat(hashToSign.calculateVerificationCode(), is("0108"));

        MobileIdSignature signature = client
                .createSignature()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .withDisplayText("Authorize transfer of €10")
                .sign();

        assertValidSignatureCreated(signature);
    }

    @Test(expected = SessionTimeoutException.class)
    public void sign_whenTimeout_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenTimeout.json");
        makeSignatureRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_whenResponseRetrievingError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenError.json");
        makeSignatureRequest();
    }

    @Test(expected = NotMIDClientException.class)
    public void sign_whenNotMIDClient_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenNotMIDClient.json");
        makeSignatureRequest();
    }

    @Test(expected = ExpiredException.class)
    public void sign_whenMSSPTransactionExpired_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenExpiredTransaction.json");
        makeSignatureRequest();
    }

    @Test(expected = UserCancellationException.class)
    public void sign_whenUserCancelled_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenUserCancelled.json");
        makeSignatureRequest();
    }

    @Test(expected = MIDNotReadyException.class)
    public void sign_whenMIDNotReady_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenMIDNotReady.json");
        makeSignatureRequest();
    }

    @Test(expected = SimNotAvailableException.class)
    public void sign_whenSimNotAvailable_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenPhoneAbsent.json");
        makeSignatureRequest();
    }

    @Test(expected = DeliveryException.class)
    public void sign_whenDeliveryError_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenDeliveryError.json");
        makeSignatureRequest();
    }

    @Test(expected = InvalidCardResponseException.class)
    public void sign_whenInvalidCardResponse_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenSimError.json");
        makeSignatureRequest();
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void sign_whenSignatureHashMismatch_shouldThrowException() throws IOException {
        stubRequestWithResponse("/mid-api/signature/session/2c52caf4-13b0-41c4-bdc6-aa268403cc00", "responses/sessionStatusWhenSignatureHashMismatch.json");
        makeSignatureRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_whenGettingSessionIdFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeSignatureRequest();
    }

    @Test(expected = ResponseNotFoundException.class)
    public void sign_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeSignatureRequest();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeSignatureRequest();
    }

    @Test(expected = UnauthorizedException.class)
    public void sign_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/signature", "requests/signatureRequest.json");
        makeSignatureRequest();
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
        makeSignatureRequest();

        verify(postRequestedFor(urlEqualTo("/mid-api/signature"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private long measureSigningDuration() {
        long startTime = System.currentTimeMillis();
        MobileIdSignature signature = createSignature();
        long endTime = System.currentTimeMillis();
        assertThat(signature, is(notNullValue()));
        return endTime - startTime;
    }

    private MobileIdSignature createSignature() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("AE7S1QxYjqtVv+Tgukv2bMMi9gDCbc9ca2vy/iIG6ug=");
        hashToSign.setHashType(HashType.SHA256);

        return client
                .createSignature()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    private void makeSignatureRequest() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("AE7S1QxYjqtVv+Tgukv2bMMi9gDCbc9ca2vy/iIG6ug=");
        hashToSign.setHashType(HashType.SHA256);

        client
                .createSignature()
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    private ClientConfig getClientConfigWithCustomRequestHeaders(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }

    private void assertValidSignatureCreated(MobileIdSignature signature) {
        assertThat(signature, is(notNullValue()));
        assertThat(signature.getValueInBase64(), startsWith("luvjsi1+1iLN9yfDFEh/BE8h"));
        assertThat(signature.getAlgorithmName(), is("sha256WithRSAEncryption"));
    }
}
