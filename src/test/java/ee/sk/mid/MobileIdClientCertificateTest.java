package ee.sk.mid;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.exception.*;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ee.sk.mid.mock.MobileIdRestServiceStubs.*;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MobileIdClientCertificateTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdClient client;

    @Before
    public void setUp() throws Exception {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1);
        client.setRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1);
        client.setHostUrl("http://localhost:18089");
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponse.json");
    }

    @Test
    public void getCertificate() {
        X509Certificate certificate = client
                .getCertificate()
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .fetch();

        assertCertificateResponseValid(certificate);
    }

    @Test(expected = CertificateNotPresentException.class)
    public void getCertificate_whenCertificateNotPresent_shouldThrowException() throws Exception {
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponseWhenCertificateNotFound.json");
        makeGetCertificateRequest();
    }

    @Test(expected = ExpiredException.class)
    public void getCertificate_whenInactiveCertificateFound_shouldThrowException() throws Exception {
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponseWhenInactiveCertificateFound.json");
        makeGetCertificateRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void getCertificate_whenGettingSessionIdFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeGetCertificateRequest();
    }

    @Test(expected = ResponseNotFoundException.class)
    public void getCertificate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeGetCertificateRequest();
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeGetCertificateRequest();
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeGetCertificateRequest();
    }

    @Test
    public void verifyCertificateChoice_withNetworkConnectionConfigurationHavingCustomHeader() {
        String headerName = "custom-header";
        String headerValue = "Fetch";

        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        ClientConfig clientConfig = getClientConfigWithCustomRequestHeaders(headers);
        client.setNetworkConnectionConfig(clientConfig);
        makeGetCertificateRequest();

        verify(postRequestedFor(urlEqualTo("/mid-api/certificate"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private void makeGetCertificateRequest() {
        client
                .getCertificate()
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .fetch();
    }

    private ClientConfig getClientConfigWithCustomRequestHeaders(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }

    private void assertCertificateResponseValid(X509Certificate certificate) {
        assertThat(certificate, is(notNullValue()));
        assertThat(certificate.getSubjectDN().getName(), containsString("SERIALNUMBER=PNOEE-31111111111"));
    }
}
