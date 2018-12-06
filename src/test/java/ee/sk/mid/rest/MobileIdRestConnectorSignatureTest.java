package ee.sk.mid.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.ClientRequestHeaderFilter;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.ResponseNotFoundException;
import ee.sk.mid.exception.ResponseRetrievingException;
import ee.sk.mid.exception.UnauthorizedException;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.SignatureResponse;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createValidSignatureRequest;
import static ee.sk.mid.mock.MobileIdRestServiceStub.*;
import static ee.sk.mid.mock.TestData.LOCALHOST_URL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MobileIdRestConnectorSignatureTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(18089);
    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector(LOCALHOST_URL);
    }

    @Test
    public void sign() throws Exception {
        stubRequestWithResponse("/mid-api/signature", "requests/signatureRequest.json", "responses/signatureResponse.json");
        SignatureRequest request = createValidSignatureRequest();
        SignatureResponse response = connector.sign(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionID(), is("2c52caf4-13b0-41c4-bdc6-aa268403cc00"));
    }

    @Test
    public void sign_withDisplayText() throws Exception {
        stubRequestWithResponse("/mid-api/signature", "requests/signatureRequestWithDisplayText.json", "responses/signatureResponse.json");
        SignatureRequest request = createValidSignatureRequest();
        request.setDisplayText("Authorize transfer of 10 euros");
        SignatureResponse response = connector.sign(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionID(), is("2c52caf4-13b0-41c4-bdc6-aa268403cc00"));
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_whenGettingResponseFailed_shouldThrowException() throws Exception {
        stubInternalServerErrorResponse("/mid-api/signature", "requests/signatureRequest.json");
        SignatureRequest request = createValidSignatureRequest();
        connector.sign(request);
    }

    @Test(expected = ResponseNotFoundException.class)
    public void sign_whenResponseNotFound_shouldThrowException() throws Exception {
        stubNotFoundResponse("/mid-api/signature", "requests/signatureRequest.json");
        SignatureRequest request = createValidSignatureRequest();
        connector.sign(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongRequestParams_shouldThrowException() throws Exception {
        stubBadRequestResponse("/mid-api/signature", "requests/signatureRequest.json");
        SignatureRequest request = createValidSignatureRequest();
        connector.sign(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void sign_withWrongAuthenticationParams_shouldThrowException() throws Exception {
        stubUnauthorizedResponse("/mid-api/signature", "requests/signatureRequest.json");
        SignatureRequest request = createValidSignatureRequest();
        connector.sign(request);
    }

    @Test
    public void verifyCustomRequestHeaderPresent_whenSigning() throws Exception {
        String headerName = "custom-header";
        String headerValue = "Sign";

        Map<String, String> headers = new HashMap<>();
        headers.put(headerName, headerValue);
        connector = new MobileIdRestConnector("http://localhost:18089", getClientConfigWithCustomRequestHeader(headers));
        stubRequestWithResponse("/mid-api/signature", "requests/signatureRequest.json", "responses/signatureResponse.json");
        SignatureRequest request = createValidSignatureRequest();
        connector.sign(request);

        verify(postRequestedFor(urlEqualTo("/mid-api/signature"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private ClientConfig getClientConfigWithCustomRequestHeader(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }
}
