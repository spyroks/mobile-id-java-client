package ee.sk.mid.rest.integration;

import ee.sk.mid.categories.IntegrationTest;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.UnauthorizedException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.SignatureResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.assertCorrectSignatureRequestMade;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createSignatureRequest;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.assertSignaturePolled;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.assertSignatureResponse;
import static ee.sk.mid.mock.SessionStatusPollerDummy.pollSessionStatus;
import static ee.sk.mid.mock.TestData.*;

@Category({IntegrationTest.class})
public class MobileIdRestConnectorSignatureIT {

    private static final String SIGNATURE_SESSION_PATH = "/mid-api/signature/session/{sessionId}";

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector(TEST_HOST_URL);
    }

    @Test
    public void sign() throws Exception {
        SignatureRequest request = createSignatureRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        assertCorrectSignatureRequestMade(request);

        SignatureResponse response = connector.sign(request);
        assertSignatureResponse(response);

        SessionStatus sessionStatus = pollSessionStatus(connector, response.getSessionId(), SIGNATURE_SESSION_PATH);
        assertSignaturePolled(sessionStatus);
    }

    @Test
    public void sign_withDisplayText() throws InterruptedException {
        SignatureRequest request = createSignatureRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        request.setDisplayText("Authorize transfer of 10 euros");
        assertCorrectSignatureRequestMade(request);

        SignatureResponse response = connector.sign(request);
        assertSignatureResponse(response);

        SessionStatus sessionStatus = pollSessionStatus(connector, response.getSessionId(), SIGNATURE_SESSION_PATH);
        assertSignaturePolled(sessionStatus);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongPhoneNumber_shouldThrowException() {
        SignatureRequest request = createSignatureRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, WRONG_PHONE, VALID_NAT_IDENTITY);
        connector.sign(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongNationalIdentityNumber_shouldThrowException() {
        SignatureRequest request = createSignatureRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, WRONG_NAT_IDENTITY);
        connector.sign(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongRelyingPartyUUID_shouldThrowException() {
        SignatureRequest request = createSignatureRequest(WRONG_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.sign(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongRelyingPartyName_shouldThrowException() {
        SignatureRequest request = createSignatureRequest(VALID_RELYING_PARTY_UUID, WRONG_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.sign(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void sign_withUnknownRelyingPartyUUID_shouldThrowException() {
        SignatureRequest request = createSignatureRequest(VALID_RELYING_PARTY_UUID, UNKNOWN_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.sign(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void sign_withUnknownRelyingPartyName_shouldThrowException() {
        SignatureRequest request = createSignatureRequest(UNKNOWN_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.sign(request);
    }
}
