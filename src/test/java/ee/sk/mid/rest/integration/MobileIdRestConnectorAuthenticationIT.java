package ee.sk.mid.rest.integration;

import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.UnauthorizedException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createAuthenticationRequest;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createValidAuthenticationRequest;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.assertAuthenticationPolled;
import static ee.sk.mid.mock.SessionStatusPollerDummy.pollSessionStatus;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MobileIdRestConnectorAuthenticationIT {

    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector(HOST_URL);
    }

    @Test
    public void authenticate() throws Exception {
        AuthenticationRequest request = createValidAuthenticationRequest();
        AuthenticationResponse response = connector.authenticate(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionId(), not(isEmptyOrNullString()));

        SessionStatus sessionStatus = pollSessionStatus(connector, response.getSessionId(), AUTHENTICATION_SESSION_PATH);

        assertAuthenticationPolled(sessionStatus);
    }

    @Test
    public void authenticate_withDisplayText() throws InterruptedException {
        AuthenticationRequest request = createValidAuthenticationRequest();
        request.setDisplayText("Log into internet banking system");
        AuthenticationResponse response = connector.authenticate(request);

        assertThat(response, is(notNullValue()));
        assertThat(response.getSessionId(), not(isEmptyOrNullString()));

        SessionStatus sessionStatus = pollSessionStatus(connector, response.getSessionId(), AUTHENTICATION_SESSION_PATH);

        assertAuthenticationPolled(sessionStatus);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongPhoneNumber_shouldThrowException() {
        AuthenticationRequest request = createAuthenticationRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, WRONG_PHONE, VALID_NAT_IDENTITY);
        connector.authenticate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongNationalIdentityNumber_shouldThrowException() {
        AuthenticationRequest request = createAuthenticationRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, WRONG_NAT_IDENTITY);
        connector.authenticate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRelyingPartyUUID_shouldThrowException() {
        AuthenticationRequest request = createAuthenticationRequest(WRONG_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.authenticate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRelyingPartyName_shouldThrowException() {
        AuthenticationRequest request = createAuthenticationRequest(VALID_RELYING_PARTY_UUID, WRONG_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.authenticate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withUnknownRelyingPartyUUID_shouldThrowException() {
        AuthenticationRequest request = createAuthenticationRequest(VALID_RELYING_PARTY_UUID, UNKNOWN_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.authenticate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withUnknownRelyingPartyName_shouldThrowException() {
        AuthenticationRequest request = createAuthenticationRequest(UNKNOWN_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.authenticate(request);
    }
}
