package ee.sk.mid;

import ee.sk.mid.exception.*;
import ee.sk.mid.mock.MobileIdConnectorSpy;
import ee.sk.mid.rest.SessionStatusPoller;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummySignatureResponse;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummySignatureSessionStatus;
import static ee.sk.mid.mock.SessionStatusDummy.*;
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
        connector.setSessionStatusToRespond(createDummySignatureSessionStatus());
        builder = new SignatureRequestBuilder(connector, sessionStatusPoller);
    }

    @Test
    public void sign_withSignableHash() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        MobileIdSignature signature = builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
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
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withSignableData(dataToSign)
                .withLanguage(Language.EST)
                .sign();

        assertCorrectSignatureRequestMade();
        assertCorrectSessionRequestMade();
        assertSignatureCorrect(signature);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutRelyingPartyUUID_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
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
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
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
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
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
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withoutSignableHash_andWithoutSignableData_shouldThrowException() {
        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withSignableHash_withoutHashType_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);

        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withSignableHash_withoutHash_shouldThrowException() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
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
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withSignableHash(hashToSign)
                .sign();
    }

    @Test(expected = SessionTimeoutException.class)
    public void sign_withTimeout_shouldThrowException() {
        connector.setSessionStatusToRespond(createTimeoutSessionStatus());
        makeSignatureRequest();
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_withResponseRetrievingError_shouldThrowException() {
        connector.setSessionStatusToRespond(createResponseRetrievingErrorStatus());
        makeSignatureRequest();
    }

    @Test(expected = NotMIDClientException.class)
    public void sign_withNotMIDClient_shouldThrowException() {
        connector.setSessionStatusToRespond(createNotMIDClientStatus());
        makeSignatureRequest();
    }

    @Test(expected = ExpiredException.class)
    public void sign_withMSSPTransactionExpired_shouldThrowException() {
        connector.setSessionStatusToRespond(createMSSPTransactionExpiredStatus());
        makeSignatureRequest();
    }

    @Test(expected = UserCancellationException.class)
    public void sign_withUserCancellation_shouldThrowException() {
        connector.setSessionStatusToRespond(createUserCancellationStatus());
        makeSignatureRequest();
    }

    @Test(expected = MIDNotReadyException.class)
    public void sign_withMIDNotReady_shouldThrowException() {
        connector.setSessionStatusToRespond(createMIDNotReadyStatus());
        makeSignatureRequest();
    }

    @Test(expected = SimNotAvailableException.class)
    public void sign_withSimNotAvailable_shouldThrowException() {
        connector.setSessionStatusToRespond(createSimNotAvailableStatus());
        makeSignatureRequest();
    }

    @Test(expected = DeliveryException.class)
    public void sign_withDeliveryError_shouldThrowException() {
        connector.setSessionStatusToRespond(createDeliveryErrorStatus());
        makeSignatureRequest();
    }

    @Test(expected = InvalidCardResponseException.class)
    public void sign_withInvalidCardResponse_shouldThrowException() {
        connector.setSessionStatusToRespond(createInvalidCardResponseStatus());
        makeSignatureRequest();
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void sign_withSignatureHashMismatch_shouldThrowException() {
        connector.setSessionStatusToRespond(createSignatureHashMismatchStatus());
        makeSignatureRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void sign_withResultMissingInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setResult(null);
        makeSignatureRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void sign_withResultBlankInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setResult("");
        makeSignatureRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void sign_withSignatureMissingInResponse_shouldThrowException() {
        connector.getSessionStatusToRespond().setSignature(null);
        makeSignatureRequest();
    }

    private void makeSignatureRequest() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    private void assertCorrectSignatureRequestMade() {
        assertThat(connector.getSignatureRequestUsed().getRelyingPartyUUID(), is(VALID_RELYING_PARTY_UUID));
        assertThat(connector.getSignatureRequestUsed().getRelyingPartyName(), is(VALID_RELYING_PARTY_NAME));
        assertThat(connector.getSignatureRequestUsed().getPhoneNumber(), is(VALID_PHONE));
        assertThat(connector.getSignatureRequestUsed().getNationalIdentityNumber(), is(VALID_NAT_IDENTITY));
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
}
