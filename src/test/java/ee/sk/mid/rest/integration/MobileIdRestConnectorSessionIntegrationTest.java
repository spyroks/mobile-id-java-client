package ee.sk.mid.rest.integration;

import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createValidAuthenticationRequest;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createValidSignatureRequest;
import static ee.sk.mid.mock.TestData.HOST_URL;
import static ee.sk.mid.mock.TestData.SESSION_ID;

public class MobileIdRestConnectorSessionIntegrationTest {

    private static final String SIGNATURE_SESSION_PATH = "/mid-api/signature/session/{sessionId}";
    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector(HOST_URL);
    }

    @Test
    public void getSessionStatus_forSuccessfulSigningRequest() {
        SignatureRequest signatureRequest = createValidSignatureRequest();
        SignatureResponse signatureResponse = connector.sign(signatureRequest);

        SessionStatusRequest sessionStatusRequest = new SessionStatusRequest(signatureResponse.getSessionId());
        connector.getSessionStatus(sessionStatusRequest, SIGNATURE_SESSION_PATH);
    }

    @Test
    public void getSessionStatus_forSuccessfulAuthenticationRequest() {
        AuthenticationRequest authenticationRequest = createValidAuthenticationRequest();
        AuthenticationResponse authenticationResponse = connector.authenticate(authenticationRequest);

        SessionStatusRequest sessionStatusRequest = new SessionStatusRequest(authenticationResponse.getSessionId());
        connector.getSessionStatus(sessionStatusRequest, AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = SessionNotFoundException.class)
    public void getSessionStatus_whenSessionStatusNotExists_shouldThrowException() {
        SessionStatusRequest request = new SessionStatusRequest(SESSION_ID);
        connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = SessionNotFoundException.class)
    public void getSessionStatus_whenSessionStatusNotFound_shouldThrowException() {
        SignatureRequest signatureRequest = createValidSignatureRequest();
        SignatureResponse signatureResponse = connector.sign(signatureRequest);

        SessionStatusRequest sessionStatusRequest = new SessionStatusRequest(signatureResponse.getSessionId());
        connector.getSessionStatus(sessionStatusRequest, AUTHENTICATION_SESSION_PATH);
    }
}
