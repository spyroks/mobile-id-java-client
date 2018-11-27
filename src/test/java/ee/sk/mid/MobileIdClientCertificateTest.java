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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.MobileIdRestServiceStub.*;
import static ee.sk.mid.mock.TestData.*;

public class MobileIdClientCertificateTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdClient client;

    @Before
    public void setUp() throws Exception {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(VALID_RELYING_PARTY_UUID);
        client.setRelyingPartyName(VALID_RELYING_PARTY_NAME);
        client.setHostUrl(LOCALHOST_URL);
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponse.json");
    }

    @Test
    public void getCertificate() {
        X509Certificate certificate = createCertificate(client);

        assertCertificateCreated(certificate);
    }

    @Test(expected = CertificateNotPresentException.class)
    public void getCertificate_whenCertificateNotPresent_shouldThrowException() throws Exception {
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponseWhenCertificateNotFound.json");
        makeValidCertificateRequest(client);
    }

    @Test(expected = ExpiredException.class)
    public void getCertificate_whenInactiveCertificateFound_shouldThrowException() throws Exception {
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponseWhenInactiveCertificateFound.json");
        makeValidCertificateRequest(client);
    }

    @Test(expected = ResponseRetrievingException.class)
    public void getCertificate_whenGettingResponseFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeValidCertificateRequest(client);
    }

    @Test(expected = ResponseNotFoundException.class)
    public void getCertificate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeValidCertificateRequest(client);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeValidCertificateRequest(client);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        makeValidCertificateRequest(client);
    }

    @Test
    public void verifyCertificateChoice_withNetworkConnectionConfigurationHavingCustomHeader() {
        String headerName = "custom-header";
        String headerValue = "Fetch";

        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        ClientConfig clientConfig = getClientConfigWithCustomRequestHeaders(headers);
        client.setNetworkConnectionConfig(clientConfig);
        makeValidCertificateRequest(client);

        verify(postRequestedFor(urlEqualTo("/mid-api/certificate"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private ClientConfig getClientConfigWithCustomRequestHeaders(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }
}
