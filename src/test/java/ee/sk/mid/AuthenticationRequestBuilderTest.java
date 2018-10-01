package ee.sk.mid;

import ee.sk.mid.exception.*;
import ee.sk.mid.mock.MobileIdConnectorSpy;
import ee.sk.mid.rest.SessionStatusPoller;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.CertificateEncodingException;

import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummyAuthenticationResponse;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummyAuthenticationSessionStatusResponse;
import static ee.sk.mid.mock.SessionStatusResultDummy.*;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AuthenticationRequestBuilderTest {

    private MobileIdConnectorSpy connector;
    private AuthenticationRequestBuilder builder;

    @Before
    public void setUp() {
        connector = new MobileIdConnectorSpy();
        connector.setAuthenticationResponseToRespond(createDummyAuthenticationResponse());
        connector.setSessionStatusToRespond(createDummyAuthenticationSessionStatusResponse());
        SessionStatusPoller sessionStatusPoller = new SessionStatusPoller(connector);
        builder = new AuthenticationRequestBuilder(connector, sessionStatusPoller);
    }

    @Test
    public void authenticate_withGeneratedHash() throws Exception {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        MobileIdAuthentication authentication = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();

        assertCorrectSessionRequestMade();
        assertAuthenticationCorrect(authentication, authenticationHash.getHashInBase64());
    }

    @Test
    public void authenticate_withHash() throws Exception {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64(SHA512_HASH_IN_BASE64);
        authenticationHash.setHashType(HashType.SHA512);

        MobileIdAuthentication authentication = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();

        assertCorrectAuthenticationRequestMade();
        assertCorrectSessionRequestMade();
        assertAuthenticationCorrect(authentication, SHA512_HASH_IN_BASE64);
    }

    @Test
    public void authenticate_withSignableData() throws Exception {
        SignableData dataToSign = new SignableData(DATA_TO_SIGN);
        dataToSign.setHashType(HashType.SHA512);

        MobileIdAuthentication authentication = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableData(dataToSign)
                .withLanguage(Language.EST)
                .authenticate();

        assertCorrectAuthenticationRequestMade();
        assertCorrectSessionRequestMade();
        assertAuthenticationCorrect(authentication, SHA512_HASH_IN_BASE64);
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withoutRelyingPartyUUID_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();

    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withoutRelyingPartyName_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withoutPhoneNumber_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withoutNationalIdentityNumber_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withoutHash_andWithoutSignableData_shouldThrowException() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withLanguage(Language.EST)
                .authenticate();
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withHash_withoutHashType_shouldThrowException() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashInBase64(SHA512_HASH_IN_BASE64);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withHashType_withoutHash_shouldThrowException() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        authenticationHash.setHashType(HashType.SHA512);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    @Test(expected = ParameterMissingException.class)
    public void authenticate_withoutLanguage_shouldThrowException() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .authenticate();
    }

    @Test(expected = SessionTimeoutException.class)
    public void authenticate_withTimeout_shouldThrowException() {
        connector.setSessionStatusToRespond(createTimeoutSessionStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void authenticate_withResponseRetrievingError_shouldThrowException() {
        connector.setSessionStatusToRespond(createResponseRetrievingErrorStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = NotMIDClientException.class)
    public void authenticate_withNotMIDClient_shouldThrowException() {
        connector.setSessionStatusToRespond(createNotMIDClientStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = ExpiredException.class)
    public void authenticate_withMSSPTransactionExpired_shouldThrowException() {
        connector.setSessionStatusToRespond(createMSSPTransactionExpiredStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = UserCancellationException.class)
    public void authenticate_withUserCancellation_shouldThrowException() {
        connector.setSessionStatusToRespond(createUserCancellationStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = MIDNotReadyException.class)
    public void authenticate_withMIDNotReady_shouldThrowException() {
        connector.setSessionStatusToRespond(createMIDNotReadyStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = SimNotAvailableException.class)
    public void authenticate_withSimNotAvailable_shouldThrowException() {
        connector.setSessionStatusToRespond(createSimNotAvailableStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = DeliveryException.class)
    public void authenticate_withDeliveryError_shouldThrowException() {
        connector.setSessionStatusToRespond(createDeliveryErrorStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = InvalidCardResponseException.class)
    public void authenticate_withInvalidCardResponse_shouldThrowException() {
        connector.setSessionStatusToRespond(createInvalidCardResponseStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void authenticate_withSignatureHashMismatch_shouldThrowException() {
        connector.setSessionStatusToRespond(createSignatureHashMismatchStatus());
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withResultMissingInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setResult(null);
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withResultBlankInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setResult("");
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withSignatureMissingInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setSignature(null);
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withCertificateBlankInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setCertificate("");
        makeAuthenticationRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void authenticate_withCertificateMissingInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setCertificate(null);
        makeAuthenticationRequest();
    }

    private void assertCorrectAuthenticationRequestMade() {
        assertThat(connector.getAuthenticationRequestUsed().getRelyingPartyUUID(), is(RELYING_PARTY_UUID_OF_USER_1));
        assertThat(connector.getAuthenticationRequestUsed().getRelyingPartyName(), is(RELYING_PARTY_NAME_OF_USER_1));
        assertThat(connector.getAuthenticationRequestUsed().getPhoneNumber(), is(VALID_PHONE_1));
        assertThat(connector.getAuthenticationRequestUsed().getNationalIdentityNumber(), is(VALID_NAT_IDENTITY_1));
        assertThat(connector.getAuthenticationRequestUsed().getHash(), is(SHA512_HASH_IN_BASE64));
        assertThat(connector.getAuthenticationRequestUsed().getHashType(), is(HashType.SHA512));
        assertThat(connector.getAuthenticationRequestUsed().getLanguage(), is(Language.EST));
    }

    private void assertCorrectSessionRequestMade() {
        assertThat(connector.getSessionIdUsed(), is(SESSION_ID));
    }

    private void assertAuthenticationCorrect(MobileIdAuthentication authentication, String expectedHashToSignInBase64) throws CertificateEncodingException {
        assertThat(authentication, is(notNullValue()));
        assertThat(authentication.getResult(), is("OK"));
        assertThat(authentication.getSignatureValueInBase64(), is("c2FtcGxlIHNpZ25hdHVyZQ0K"));
        assertThat(authentication.getAlgorithmName(), is("sha512WithRSAEncryption"));
        assertThat(Base64.encodeBase64String(authentication.getCertificate().getEncoded()), is(CERTIFICATE));
        assertThat(authentication.getSignedHashInBase64(), is(expectedHashToSignInBase64));
    }

    private void makeAuthenticationRequest() {
        AuthenticationHash authenticationHash = AuthenticationHash.generateRandomHash();

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }
}
