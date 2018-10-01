package ee.sk.mid;

import ee.sk.mid.exception.ExpiredException;
import ee.sk.mid.exception.NotFoundException;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.TechnicalErrorException;
import ee.sk.mid.mock.MobileIdConnectorSpy;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.X509Certificate;

import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.createDummyCertificateChoiceResponse;
import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CertificateRequestBuilderTest {

    private MobileIdConnectorSpy connector;
    private CertificateRequestBuilder builder;

    @Before
    public void setUp() {
        connector = new MobileIdConnectorSpy();
        connector.setCertificateChoiceResponseToRespond(createDummyCertificateChoiceResponse());
        builder = new CertificateRequestBuilder(connector);
    }

    @Test
    public void getCertificate() {
        X509Certificate certificate = builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .fetch();

        assertValidCertificateChoiceRequestMade();
        assertCertificateCorrect(certificate);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutRelyingPartyUUID_shouldThrowException() {
        builder
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .fetch();

    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutRelyingPartyName_shouldThrowException() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .fetch();
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutPhoneNumber_shouldThrowException() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .fetch();
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutNationalIdentityNumber_shouldThrowException() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .fetch();
    }

    @Test(expected = NotFoundException.class)
    public void getCertificate_withCertificateNotFound_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult("NOT_FOUND");
        makeCertificateRequest();
    }

    @Test(expected = ExpiredException.class)
    public void getCertificate_withInactiveCertificateFound_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult("NOT_ACTIVE");
        makeCertificateRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withResultMissingInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult(null);
        makeCertificateRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withResultBlankInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult("");
        makeCertificateRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withCertificateMissingInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setCertificate(null);
        makeCertificateRequest();
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withCertificateBlankInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setCertificate("");
        makeCertificateRequest();
    }

    private void assertValidCertificateChoiceRequestMade() {
        assertThat(connector.getCertificateRequestUsed().getRelyingPartyUUID(), is(RELYING_PARTY_UUID_OF_USER_1));
        assertThat(connector.getCertificateRequestUsed().getRelyingPartyName(), is(RELYING_PARTY_NAME_OF_USER_1));
        assertThat(connector.getCertificateRequestUsed().getPhoneNumber(), is(VALID_PHONE_1));
        assertThat(connector.getCertificateRequestUsed().getNationalIdentityNumber(), is(VALID_NAT_IDENTITY_1));
    }

    private void assertCertificateCorrect(X509Certificate certificate) {
        assertThat(certificate, is(notNullValue()));
    }

    private void makeCertificateRequest() {
        builder
                .withRelyingPartyUUID(RELYING_PARTY_UUID_OF_USER_1)
                .withRelyingPartyName(RELYING_PARTY_NAME_OF_USER_1)
                .withPhoneNumber(VALID_PHONE_1)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY_1)
                .fetch();
    }
}
