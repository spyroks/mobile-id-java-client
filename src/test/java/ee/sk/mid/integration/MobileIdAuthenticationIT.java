package ee.sk.mid.integration;

import ee.sk.mid.*;
import ee.sk.mid.categories.IntegrationTest;
import ee.sk.mid.exception.*;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.assertAuthenticationPolled;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Category({IntegrationTest.class})
public class MobileIdAuthenticationIT {

    private MobileIdClient client;

    @Before
    public void setUp() {
        client = MobileIdClient.createMobileIdClientBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();
    }

    @Test
    public void authenticate() {
        MobileIdAuthenticationHash authenticationHash = MobileIdAuthenticationHash.generateRandomHashOfDefaultType();

        assertThat(authenticationHash.calculateVerificationCode().length(), is(4));
        assertThat(Integer.valueOf(authenticationHash.calculateVerificationCode()), allOf(greaterThanOrEqualTo(0), lessThanOrEqualTo(8192)));

        MobileIdAuthentication authentication = createAndSendAuthentication(client, VALID_PHONE, VALID_NAT_IDENTITY, authenticationHash);

        assertAuthenticationCreated(authentication, authenticationHash.getHashInBase64());

        AuthenticationResponseValidator validator = new AuthenticationResponseValidator();
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertAuthenticationResultValid(authenticationResult);
    }

    @Test
    public void authenticate_withDisplayText() {
        MobileIdAuthenticationHash authenticationHash = MobileIdAuthenticationHash.generateRandomHashOfDefaultType();

        AuthenticationRequest request = client
                .createAuthenticationRequestBuilder()
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .withDisplayText("Log into internet banking system")
                .build();

        AuthenticationResponse response = client.getMobileIdConnector().authenticate(request);
        assertThat(response.getSessionID(), not(isEmptyOrNullString()));

        SessionStatus sessionStatus = client.getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionID(), AUTHENTICATION_SESSION_PATH);
        assertAuthenticationPolled(sessionStatus);

        MobileIdAuthentication authentication = client.createMobileIdAuthentication(sessionStatus);
        assertAuthenticationCreated(authentication, authenticationHash.getHashInBase64());
    }

    @Test(expected = NotMIDClientException.class)
    public void authenticate_whenNotMIDClient_shouldThrowException() {
        makeAuthenticationRequest(client, VALID_PHONE_NOT_MID_CLIENT, VALID_NAT_IDENTITY_NOT_MID_CLIENT);
    }

    @Test(expected = ExpiredException.class)
    public void authenticate_whenMSSPTransactionExpired_shouldThrowException() {
        MobileIdAuthenticationHash authenticationHash = createAuthenticationSHA512Hash();

        AuthenticationRequest request = client
                .createAuthenticationRequestBuilder()
                .withPhoneNumber(VALID_PHONE_EXPIRED_TRANSACTION)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_EXPIRED_TRANSACTION)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .build();

        AuthenticationResponse response = client.getMobileIdConnector().authenticate(request);
        SessionStatus sessionStatus = client.getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionID(), AUTHENTICATION_SESSION_PATH);
        client.createMobileIdAuthentication(sessionStatus);
    }

    @Test(expected = UserCancellationException.class)
    public void authenticate_whenUserCancelled_shouldThrowException() {
        makeAuthenticationRequest(client, VALID_PHONE_USER_CANCELLED, VALID_NAT_IDENTITY_USER_CANCELLED);
    }

    @Test(expected = SimNotAvailableException.class)
    public void authenticate_whenSimNotAvailable_shouldThrowException() {
        makeAuthenticationRequest(client, VALID_PHONE_ABSENT, VALID_NAT_IDENTITY_ABSENT);
    }

    @Test(expected = DeliveryException.class)
    public void authenticate_whenDeliveryError_shouldThrowException() {
        makeAuthenticationRequest(client, VALID_PHONE_DELIVERY_ERROR, VALID_NAT_IDENTITY_DELIVERY_ERROR);
    }

    @Test(expected = InvalidCardResponseException.class)
    public void authenticate_whenInvalidCardResponse_shouldThrowException() {
        makeAuthenticationRequest(client, VALID_PHONE_SIM_ERROR, VALID_NAT_IDENTITY_SIM_ERROR);
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void authenticate_whenSignatureHashMismatch_shouldThrowException() {
        makeAuthenticationRequest(client, VALID_PHONE_SIGNATURE_HASH_MISMATCH, VALID_NAT_IDENTITY_SIGNATURE_HASH_MISMATCH);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongPhoneNumber_shouldThrowException() {
        makeAuthenticationRequest(client, WRONG_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongNationalIdentityNumber_shouldThrowException() {
        makeAuthenticationRequest(client, VALID_PHONE, WRONG_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRelyingPartyUUID_shouldThrowException() {
        client.setRelyingPartyUUID(WRONG_RELYING_PARTY_UUID);
        makeAuthenticationRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withWrongRelyingPartyName_shouldThrowException() {
        client.setRelyingPartyName(WRONG_RELYING_PARTY_NAME);
        makeAuthenticationRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withUnknownRelyingPartyUUID_shouldThrowException() {
        client.setRelyingPartyUUID(UNKNOWN_RELYING_PARTY_UUID);
        makeAuthenticationRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void authenticate_withUnknownRelyingPartyName_shouldThrowException() {
        client.setRelyingPartyName(UNKNOWN_RELYING_PARTY_NAME);
        makeAuthenticationRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    private void assertAuthenticationResultValid(MobileIdAuthenticationResult authenticationResult) {
        assertThat(authenticationResult.isValid(), is(true));
        assertThat(authenticationResult.getErrors().isEmpty(), is(true));
        assertAuthenticationIdentityValid(authenticationResult.getAuthenticationIdentity());
    }

    private void assertAuthenticationIdentityValid(AuthenticationIdentity authenticationIdentity) {
        assertThat(authenticationIdentity.getGivenName(), not(isEmptyOrNullString()));
        assertThat(authenticationIdentity.getSurName(), not(isEmptyOrNullString()));
        assertThat(authenticationIdentity.getIdentityCode(), not(isEmptyOrNullString()));
        assertThat(authenticationIdentity.getCountry(), not(isEmptyOrNullString()));
    }
}
