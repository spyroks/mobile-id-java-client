package ee.sk.mid;

import ee.sk.mid.exception.*;
import ee.sk.mid.mock.MobileIdConnectorSpy;
import ee.sk.mid.rest.SessionStatusPoller;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummySignatureResponse;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummySignatureSessionStatusResponse;
import static ee.sk.mid.mock.SessionStatusResultDummy.*;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class SignatureRequestBuilderTest {

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
    public void sign_withSignableHash() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        MobileIdSignature signature = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();

        assertCorrectSignatureRequestMade();
        assertCorrectSessionRequestMade();
        assertSignatureCorrect(signature);
    }

    @Test
    public void sign_withSignableData() {
        SignableData dataToSign = new SignableData(DATA_TO_SIGN);
        dataToSign.setHashType(HashType.SHA256);

        MobileIdSignature signature = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableData(dataToSign)
                .withLanguage(Language.EST)
                .sign();

        assertCorrectSignatureRequestMade();
        assertCorrectSessionRequestMade();
        assertSignatureCorrect(signature);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutRelyingPartyUuid_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutRelyingPartyName_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutPhoneNumber_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutNationalIdentityNumber_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutSignableHash_andWithoutSignableData_shouldThrowException() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withSignableHash_withoutHashType_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withSignableHash_withoutHash_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutLanguage_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .sign();
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
        assertThat(connector.getSignatureRequestUsed().getRelyingPartyUUID(), is(RELYING_PARTY_UUID_OF_USER_1));
        assertThat(connector.getSignatureRequestUsed().getRelyingPartyName(), is(RELYING_PARTY_NAME_OF_USER_1));
        assertThat(connector.getSignatureRequestUsed().getPhoneNumber(), is(VALID_PHONE_1));
        assertThat(connector.getSignatureRequestUsed().getNationalIdentityNumber(), is(VALID_NAT_IDENTITY_1));
        assertThat(connector.getSignatureRequestUsed().getHash(), is(SHA256_HASH_IN_BASE64));
        assertThat(connector.getSignatureRequestUsed().getHashType(), is(HashType.SHA256));
        assertThat(connector.getSignatureRequestUsed().getLanguage(), is(Language.EST));
    }

    private void assertCorrectSessionRequestMade() {
        assertThat(connector.getSessionIdUsed(), is(SESSION_ID));
    }

    private void assertSignatureCorrect(MobileIdSignature signature) {
        assertThat(signature, is(notNullValue()));
        assertThat(signature.getValueInBase64(), is("luvjsi1+1iLN9yfDFEh/BE8h"));
        assertThat(signature.getAlgorithmName(), is("sha256WithRSAEncryption"));
    }

    private void makeSigningRequest() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }
}
