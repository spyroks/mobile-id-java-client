package ee.sk.mid.integration;

import ee.sk.mid.*;
import ee.sk.mid.categories.IntegrationTest;
import ee.sk.mid.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.TestData.*;
import static ee.sk.mid.mock.TestData.VALID_NAT_IDENTITY;
import static ee.sk.mid.mock.TestData.VALID_PHONE;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@Category({IntegrationTest.class})
public class MobileIdSignatureIT {

    private MobileIdClient client;

    @Before
    public void setUp() {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(VALID_RELYING_PARTY_UUID);
        client.setRelyingPartyName(VALID_RELYING_PARTY_NAME);
        client.setHostUrl(HOST_URL);
    }

    @Test
    public void sign() {
        MobileIdSignature signature = createValidSignature(client);

        assertSignatureCreated(signature);
    }

    @Test
    public void signHash_withDisplayText() {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        MobileIdSignature signature = client
                .createSignatureRequestBuilder()
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .withDisplayText("Authorize transfer of 10 euros")
                .sign();

        assertSignatureCreated(signature);
    }

    @Test
    public void sign_withDelay() {
        long duration = measureSigningDuration();
        assertThat("Duration is " + duration, duration > 10000L, is(true));
        assertThat("Duration is " + duration, duration < 12000L, is(true));
    }

    @Test(expected = ResponseRetrievingException.class)
    public void sign_whenResponseRetrievingError_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_ERROR, VALID_NAT_IDENTITY_ERROR);
    }

    @Test(expected = NotMIDClientException.class)
    public void sign_whenNotMIDClient_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_NOT_MID_CLIENT, VALID_NAT_IDENTITY_NOT_MID_CLIENT);
    }

    @Test(expected = ExpiredException.class)
    public void sign_whenMSSPTransactionExpired_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_EXPIRED_TRANSACTION, VALID_NAT_IDENTITY_EXPIRED_TRANSACTION);
    }

    @Test(expected = UserCancellationException.class)
    public void sign_whenUserCancelled_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_USER_CANCELLED, VALID_NAT_IDENTITY_USER_CANCELLED);
    }

    @Test(expected = SimNotAvailableException.class)
    public void sign_whenSimNotAvailable_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_ABSENT, VALID_NAT_IDENTITY_ABSENT);
    }

    @Test(expected = DeliveryException.class)
    public void sign_whenDeliveryError_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_DELIVERY_ERROR, VALID_NAT_IDENTITY_DELIVERY_ERROR);
    }

    @Test(expected = InvalidCardResponseException.class)
    public void sign_whenInvalidCardResponse_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_SIM_ERROR, VALID_NAT_IDENTITY_SIM_ERROR);
    }

    @Test(expected = SignatureHashMismatchException.class)
    public void authenticate_whenSignatureHashMismatch_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_SIGNATURE_HASH_MISMATCH, VALID_NAT_IDENTITY_SIGNATURE_HASH_MISMATCH);
    }

    @Test(expected = TechnicalErrorException.class)
    public void sign_whenInternalErrorResult_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE_INTERNAL_ERROR, VALID_NAT_IDENTITY_INTERNAL_ERROR);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongPhoneNumber_shouldThrowException() {
        makeSignatureRequest(client, WRONG_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongNationalIdentityNumber_shouldThrowException() {
        makeSignatureRequest(client, VALID_PHONE, WRONG_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongRelyingPartyUUID_shouldThrowException() {
        client.setRelyingPartyUUID(WRONG_RELYING_PARTY_UUID);
        makeSignatureRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void sign_withWrongRelyingPartyName_shouldThrowException() {
        client.setRelyingPartyName(WRONG_RELYING_PARTY_NAME);
        makeSignatureRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void sign_withUnknownRelyingPartyUUID_shouldThrowException() {
        client.setRelyingPartyUUID(UNKNOWN_RELYING_PARTY_UUID);
        makeSignatureRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void sign_withUnknownRelyingPartyName_shouldThrowException() {
        client.setRelyingPartyName(UNKNOWN_RELYING_PARTY_NAME);
        makeSignatureRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    private long measureSigningDuration() {
        long startTime = System.currentTimeMillis();
        MobileIdSignature signature = createSignature(client, VALID_PHONE_WITH_TIMEOUT, VALID_NAT_IDENTITY_WITH_TIMEOUT);
        long endTime = System.currentTimeMillis();
        assertThat(signature, is(notNullValue()));
        return endTime - startTime;
    }
}
