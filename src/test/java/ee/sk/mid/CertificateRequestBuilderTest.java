package ee.sk.mid;

import ee.sk.mid.exception.ExpiredException;
import ee.sk.mid.exception.CertificateNotPresentException;
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
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .fetch();

        assertValidCertificateChoiceRequestMade();
        assertCertificateCorrect(certificate);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutRelyingPartyUUID_shouldThrowException() {
        builder
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .fetch();

    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutRelyingPartyName_shouldThrowException() {
        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .fetch();
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutPhoneNumber_shouldThrowException() {
        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .fetch();
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutNationalIdentityNumber_shouldThrowException() {
        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .fetch();
    }

    @Test(expected = CertificateNotPresentException.class)
    public void getCertificate_withCertificateNotPresent_shouldThrowException() {
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

    private void makeCertificateRequest() {
        builder
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .fetch();
    }

    private void assertValidCertificateChoiceRequestMade() {
        assertThat(connector.getCertificateRequestUsed().getRelyingPartyUUID(), is(VALID_RELYING_PARTY_UUID));
        assertThat(connector.getCertificateRequestUsed().getRelyingPartyName(), is(VALID_RELYING_PARTY_NAME));
        assertThat(connector.getCertificateRequestUsed().getPhoneNumber(), is(VALID_PHONE));
        assertThat(connector.getCertificateRequestUsed().getNationalIdentityNumber(), is(VALID_NAT_IDENTITY));
    }

    private void assertCertificateCorrect(X509Certificate certificate) {
        assertThat(certificate, is(notNullValue()));
    }
}
