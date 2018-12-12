package ee.sk.mid.rest.integration;

import ee.sk.mid.categories.IntegrationTest;
import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.*;
import static ee.sk.mid.mock.TestData.TEST_HOST_URL;
import static ee.sk.mid.mock.TestData.SESSION_ID;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@Category({IntegrationTest.class})
public class MobileIdRestConnectorSessionIT {

    private static final String SIGNATURE_SESSION_PATH = "/mid-api/signature/session/{sessionId}";
    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector(TEST_HOST_URL);
    }

    @Test
    public void getSessionStatus_forSuccessfulSigningRequest() {
        SignatureRequest signatureRequest = createValidSignatureRequest();
        assertCorrectSignatureRequestMade(signatureRequest);

        SignatureResponse signatureResponse = connector.sign(signatureRequest);
        assertThat(signatureResponse.getSessionID(), not(isEmptyOrNullString()));

        SessionStatusRequest sessionStatusRequest = new SessionStatusRequest(signatureResponse.getSessionID());
        SessionStatus sessionStatus = connector.getSessionStatus(sessionStatusRequest, SIGNATURE_SESSION_PATH);
        assertSignaturePolled(sessionStatus);
    }

    @Test
    public void getSessionStatus_forSuccessfulAuthenticationRequest() {
        AuthenticationRequest authenticationRequest = createValidAuthenticationRequest();
        assertCorrectAuthenticationRequestMade(authenticationRequest);

        AuthenticationResponse authenticationResponse = connector.authenticate(authenticationRequest);
        assertThat(authenticationResponse.getSessionID(), not(isEmptyOrNullString()));

        SessionStatusRequest sessionStatusRequest = new SessionStatusRequest(authenticationResponse.getSessionID());
        SessionStatus sessionStatus = connector.getSessionStatus(sessionStatusRequest, AUTHENTICATION_SESSION_PATH);
        assertAuthenticationPolled(sessionStatus);
    }

    @Test(expected = SessionNotFoundException.class)
    public void getSessionStatus_whenSessionStatusNotExists_shouldThrowException() {
        SessionStatusRequest request = new SessionStatusRequest(SESSION_ID);
        connector.getSessionStatus(request, AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = SessionNotFoundException.class)
    public void getSessionStatus_whenSessionStatusNotFound_shouldThrowException() {
        SignatureRequest signatureRequest = createValidSignatureRequest();
        assertCorrectSignatureRequestMade(signatureRequest);

        SignatureResponse signatureResponse = connector.sign(signatureRequest);
        assertThat(signatureResponse.getSessionID(), not(isEmptyOrNullString()));

        SessionStatusRequest sessionStatusRequest = new SessionStatusRequest(signatureResponse.getSessionID());
        connector.getSessionStatus(sessionStatusRequest, AUTHENTICATION_SESSION_PATH);
    }
}
