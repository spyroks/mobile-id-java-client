package ee.sk.mid;

import ee.sk.mid.exception.*;
import ee.sk.mid.rest.MobileIdConnectorSpy;
import ee.sk.mid.rest.SessionStatusPoller;
import ee.sk.mid.test.TestUtils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.CertificateEncodingException;

import static ee.sk.mid.test.DummyData.*;
import static ee.sk.mid.test.TestData.*;
import static ee.sk.mid.test.TestUtils.createDummySessionStatusResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuthenticationRequestBuilderTest {

    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";

    private MobileIdConnectorSpy connector;
    private AuthenticationRequestBuilder builder;

    @Before
    public void setUp() {
        connector = new MobileIdConnectorSpy();
        connector.authenticationSessionResponseToRespond = TestUtils.createDummyAuthenticationSessionResponse();
        connector.sessionStatusToRespond = createDummySessionStatusResponse();
        SessionStatusPoller sessionStatusPoller = new SessionStatusPoller(connector);
        builder = new AuthenticationRequestBuilder(connector, sessionStatusPoller);
    }

    @Test
    public void authenticateWithGeneratedHash() throws Exception {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        MobileIdAuthentication authenticationResponse = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);

        assertCorrectSessionRequestMade();
        assertAuthenticationResponseCorrect(authenticationResponse, authenticationHash.getHashInBase64());
    }

    @Test
    public void authenticateWithHash() throws Exception {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==");
        authenticationHash.setHashType(HashType.SHA512);

        MobileIdAuthentication authenticationResponse = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);

        assertCorrectSessionRequestMade();
        assertAuthenticationResponseCorrect(authenticationResponse, "7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==");
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithoutRelyingPartyUuid_shouldThrowException() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==");
        authenticationHash.setHashType(HashType.SHA512);

        builder
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);

    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithoutRelyingPartyName_shouldThrowException() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==");
        authenticationHash.setHashType(HashType.SHA512);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithoutPhoneNumber_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithoutNationalIdentityNumber_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithoutHash_andWithoutSignableData_shouldThrowException() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithHash_withoutHashType_shouldThrowException() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==");

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithHash_withoutHash_shouldThrowException() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashType(HashType.SHA512);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticateWithoutLanguage_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }

    @Test(expected = SessionTimeoutException.class)
    public void authenticate_withTimeout_shouldThrowException() {
        connector.sessionStatusToRespond = createTimeoutSessionStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_withResponseRetrievingError_shouldThrowException() {
        connector.sessionStatusToRespond = createResponseRetrievingErrorStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = NotMIDClientException.class)
    public void authenticate_withNotMIDClient_shouldThrowException() {
        connector.sessionStatusToRespond = createNotMIDClientStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = ExpiredTransactionException.class)
    public void authenticate_withMSSPTransactionExpired_shouldThrowException() {
        connector.sessionStatusToRespond = createMSSPTransactionExpiredStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = UserCancellationException.class)
    public void authenticate_withUserCancellation_shouldThrowException() {
        connector.sessionStatusToRespond = createUserCancellationStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = MIDNotReadyException.class)
    public void authenticate_withMIDNotReady_shouldThrowException() {
        connector.sessionStatusToRespond = createMIDNotReadyStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = SimNotAvailableException.class)
    public void authenticate_withSimNotAvailable_shouldThrowException() {
        connector.sessionStatusToRespond = createSimNotAvailableStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = DeliveryException.class)
    public void authenticate_withDeliveryError_shouldThrowException() {
        connector.sessionStatusToRespond = createDeliveryErrorStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = InvalidCardResponseException.class)
    public void authenticate_withInvalidCardResponse_shouldThrowException() {
        connector.sessionStatusToRespond = createInvalidCardResponseStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void authenticate_withSignatureHashMismatch_shouldThrowException() {
        connector.sessionStatusToRespond = createSignatureHashMismatchStatus();
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withResultMissingInResponse_shouldThrowException() {
        connector.sessionStatusToRespond.setResult(null);
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withSignatureMissingInResponse_shouldThrowException() {
        connector.sessionStatusToRespond.setSignature(null);
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withCertificateMissingInResponse_shouldThrowException() {
        connector.sessionStatusToRespond.setCertificate(null);
        makeAuthenticationRequest();
    }

    private void assertCorrectSessionRequestMade() {
        assertEquals("97f5058e-e308-4c83-ac14-7712b0eb9d86", connector.sessionIdUsed);
    }

    private void assertAuthenticationResponseCorrect(MobileIdAuthentication authentication, String expectedHashToSignInBase64) throws CertificateEncodingException {
        assertNotNull(authentication);
        assertEquals("OK", authentication.getResult());
        assertEquals(expectedHashToSignInBase64, authentication.getSignedHashInBase64());
        assertEquals("c2FtcGxlIHNpZ25hdHVyZQ0K", authentication.getSignatureValueInBase64());
        assertEquals("sha512WithRSAEncryption", authentication.getAlgorithmName());
        assertEquals(CERTIFICATE, Base64.encodeBase64String(authentication.getCertificate().getEncoded()));
    }

    private void makeAuthenticationRequest() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64("7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==");
        authenticationHash.setHashType(HashType.SHA512);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate(AUTHENTICATION_SESSION_PATH);
    }
}
