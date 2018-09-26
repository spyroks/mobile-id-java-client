package ee.sk.mid;

import ee.sk.mid.exception.*;
import ee.sk.mid.mock.MobileIdConnectorSpy;
import ee.sk.mid.rest.SessionStatusPoller;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.SessionStatusResultDummy.*;
import static ee.sk.mid.mock.TestData.*;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummySignatureResponse;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummySignatureSessionStatusResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SignatureRequestBuilderTest {

    private static final String SIGNATURE_SESSION_PATH = "/mid-api/signature/session/{sessionId}";

    private MobileIdConnectorSpy connector;
    private SignatureRequestBuilder builder;

    @Before
    public void setUp() {
        connector = new MobileIdConnectorSpy();
        SessionStatusPoller sessionStatusPoller = new SessionStatusPoller(connector);
        connector.setSignatureResponseToRespond(createDummySignatureResponse());
        connector.setSessionStatusToRespond(createDummySignatureSessionStatusResponse());
        builder = new SignatureRequestBuilder(connector, sessionStatusPoller);
    }

    @Test
    public void signWithSignableHash() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("AE7S1QxYjqtVv+Tgukv2bMMi9gDCbc9ca2vy/iIG6ug=");
        hashToSign.setHashType(HashType.SHA256);

        MobileIdSignature signature = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);

        assertCorrectSignatureRequestMade();
        assertCorrectSessionRequestMade();
        assertSignatureCorrect(signature);
    }

    @Test
    public void signWithSignableData() {
        SignableData dataToSign = new SignableData("HACKERMAN".getBytes());
        dataToSign.setHashType(HashType.SHA256);

        MobileIdSignature signature = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableData(dataToSign)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);

        assertCorrectSignatureRequestMade();
        assertCorrectSessionRequestMade();
        assertSignatureCorrect(signature);
    }

    @Test(expected = ParameterMissingException.class)
    public void signWithoutSignableHash_andWithoutSignableData_shouldThrowException() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void signWithSignableHash_withoutHashType_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("0nbgC2fVdLVQFZJdBbmG7oPoElpCYsQMtrY0c0wKYRg=");

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void signWithSignableHash_withoutHash_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void signWithoutRelyingPartyUuid_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("0nbgC2fVdLVQFZJdBbmG7oPoElpCYsQMtrY0c0wKYRg=");
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);
    }

    @Test(expected = ParameterMissingException.class)
    public void signWithoutRelyingPartyName_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("0nbgC2fVdLVQFZJdBbmG7oPoElpCYsQMtrY0c0wKYRg=");
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);
    }

    @Test(expected = SessionTimeoutException.class)
    public void sign_withTimeout_shouldThrowException() {
        connector.setSessionStatusToRespond(createTimeoutSessionStatus());
        makeSigningRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_withResponseRetrievingError_shouldThrowException() {
        connector.setSessionStatusToRespond(createResponseRetrievingErrorStatus());
        makeSigningRequest();
    }

    @Test(expected = NotMIDClientException.class)
    public void sign_withNotMIDClient_shouldThrowException() {
        connector.setSessionStatusToRespond(createNotMIDClientStatus());
        makeSigningRequest();
    }

    @Test(expected = ExpiredTransactionException.class)
    public void sign_withMSSPTransactionExpired_shouldThrowException() {
        connector.setSessionStatusToRespond(createMSSPTransactionExpiredStatus());
        makeSigningRequest();
    }

    @Test(expected = UserCancellationException.class)
    public void sign_withUserCancellation_shouldThrowException() {
        connector.setSessionStatusToRespond(createUserCancellationStatus());
        makeSigningRequest();
    }

    @Test(expected = MIDNotReadyException.class)
    public void sign_withMIDNotReady_shouldThrowException() {
        connector.setSessionStatusToRespond(createMIDNotReadyStatus());
        makeSigningRequest();
    }

    @Test(expected = SimNotAvailableException.class)
    public void sign_withSimNotAvailable_shouldThrowException() {
        connector.setSessionStatusToRespond(createSimNotAvailableStatus());
        makeSigningRequest();
    }

    @Test(expected = DeliveryException.class)
    public void sign_withDeliveryError_shouldThrowException() {
        connector.setSessionStatusToRespond(createDeliveryErrorStatus());
        makeSigningRequest();
    }

    @Test(expected = InvalidCardResponseException.class)
    public void sign_withInvalidCardResponse_shouldThrowException() {
        connector.setSessionStatusToRespond(createInvalidCardResponseStatus());
        makeSigningRequest();
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void sign_withSignatureHashMismatch_shouldThrowException() {
        connector.setSessionStatusToRespond(createSignatureHashMismatchStatus());
        makeSigningRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void sign_withResultMissingInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setResult(null);
        makeSigningRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void sign_withSignatureMissingInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setSignature(null);
        makeSigningRequest();
    }

    private void assertCorrectSignatureRequestMade() {
        assertEquals(RELYING_PARTY_UUID_OF_USER_1, connector.getSignatureRequestUsed().getRelyingPartyUUID());
        assertEquals(RELYING_PARTY_NAME_OF_USER_1, connector.getSignatureRequestUsed().getRelyingPartyName());
        assertEquals(VALID_PHONE_1, connector.getSignatureRequestUsed().getPhoneNumber());
        assertEquals(VALID_NAT_IDENTITY_1, connector.getSignatureRequestUsed().getNationalIdentityNumber());
        assertEquals("AE7S1QxYjqtVv+Tgukv2bMMi9gDCbc9ca2vy/iIG6ug=", connector.getSignatureRequestUsed().getHash());
        assertEquals(HashType.SHA256, connector.getSignatureRequestUsed().getHashType());
        assertEquals(Language.EST, connector.getSignatureRequestUsed().getLanguage());
    }

    private void assertCorrectSessionRequestMade() {
        assertEquals("97f5058e-e308-4c83-ac14-7712b0eb9d86", connector.getSessionIdUsed());
    }

    private void assertSignatureCorrect(MobileIdSignature signature) {
        assertNotNull(signature);
        assertEquals("luvjsi1+1iLN9yfDFEh/BE8h", signature.getValueInBase64());
        assertEquals("sha256WithRSAEncryption", signature.getAlgorithmName());
    }

    private void makeSigningRequest() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64("jsflWgpkVcWOyICotnVn5lazcXdaIWvcvNOWTYPceYQ=");
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign(SIGNATURE_SESSION_PATH);
    }
}
