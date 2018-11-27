package ee.sk.mid.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.ClientRequestHeaderFilter;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.ResponseNotFoundException;
import ee.sk.mid.exception.ResponseRetrievingException;
import ee.sk.mid.exception.UnauthorizedException;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createValidCertificateRequest;
import static ee.sk.mid.mock.MobileIdRestServiceStub.*;
import static ee.sk.mid.mock.TestData.LOCALHOST_URL;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MobileIdRestConnectorCertificateTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector(LOCALHOST_URL);
    }

    @Test
    public void getCertificate() throws Exception {
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponse.json");
        CertificateRequest request = createValidCertificateRequest();
        CertificateChoiceResponse response = connector.getCertificate(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getResult(), is("OK"));
        assertThat(response.getCertificate(), not(isEmptyOrNullString()));
    }

    @Test(expected = ResponseRetrievingException.class)
    public void getCertificate_whenGettingResponseFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        CertificateRequest request = createValidCertificateRequest();
        connector.getCertificate(request);
    }

    @Test(expected = ResponseNotFoundException.class)
    public void getCertificate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        CertificateRequest request = createValidCertificateRequest();
        connector.getCertificate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        CertificateRequest request = createValidCertificateRequest();
        connector.getCertificate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json");
        CertificateRequest request = createValidCertificateRequest();
        connector.getCertificate(request);
    }

    @Test
    public void verifyCustomRequestHeaderPresent_whenChoosingCertificate() throws Exception {
        String headerName = "custom-header";
        String headerValue = "Fetch";

        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        connector = new MobileIdRestConnector("http://localhost:18089", getClientConfigWithCustomRequestHeader(headers));
        stubRequestWithResponse("/mid-api/certificate", "requests/certificateChoiceRequest.json", "responses/certificateChoiceResponse.json");
        CertificateRequest request = createValidCertificateRequest();
        connector.getCertificate(request);

        verify(postRequestedFor(urlEqualTo("/mid-api/certificate"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private ClientConfig getClientConfigWithCustomRequestHeader(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }
}
