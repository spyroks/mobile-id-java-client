package ee.sk.mid.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import ee.sk.mid.ClientRequestHeaderFilter;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.ResponseNotFoundException;
import ee.sk.mid.exception.ResponseRetrievingException;
import ee.sk.mid.exception.UnauthorizedException;
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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createValidAuthenticationRequest;
import static ee.sk.mid.mock.MobileIdRestServiceStub.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class MobileIdRestConnectorAuthenticationTest {

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
        AuthenticationRequest request = createValidAuthenticationRequest();
        AuthenticationResponse response = connector.authenticate(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionId(), is("1dcc1600-29a6-4e95-a95c-d69b31febcfb"));
    }

    @Test
    public void authenticate_withDisplayText() throws IOException {
        stubRequestWithResponse("/mid-api/authentication", "requests/authenticationRequestWithDisplayText.json", "responses/authenticationResponse.json");
        AuthenticationRequest request = createValidAuthenticationRequest();
        request.setDisplayText("Log into internet banking system");
        AuthenticationResponse response = connector.authenticate(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionId(), is("1dcc1600-29a6-4e95-a95c-d69b31febcfb"));
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_whenGettingResponseFailed_shouldThrowException() throws IOException {
        stubInternalServerErrorResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        AuthenticationRequest request = createValidAuthenticationRequest();
        connector.authenticate(request);
    }

    @Test(expected = ResponseNotFoundException.class)
    public void authenticate_whenResponseNotFound_shouldThrowException() throws IOException {
        stubNotFoundResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        AuthenticationRequest request = createValidAuthenticationRequest();
        connector.authenticate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRequestParams_shouldThrowException() throws IOException {
        stubBadRequestResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        AuthenticationRequest request = createValidAuthenticationRequest();
        connector.authenticate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withWrongAuthenticationParams_shouldThrowException() throws IOException {
        stubUnauthorizedResponse("/mid-api/authentication", "requests/authenticationRequest.json");
        AuthenticationRequest request = createValidAuthenticationRequest();
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
        AuthenticationRequest request = createValidAuthenticationRequest();
        connector.authenticate(request);

        verify(postRequestedFor(urlEqualTo("/mid-api/authentication"))
                .withHeader(headerName, equalTo(headerValue)));
    }

    private ClientConfig getClientConfigWithCustomRequestHeader(Map<String, String> headers) {
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new ApacheConnectorProvider());
        clientConfig.register(new ClientRequestHeaderFilter(headers));
        return clientConfig;
    }
}
