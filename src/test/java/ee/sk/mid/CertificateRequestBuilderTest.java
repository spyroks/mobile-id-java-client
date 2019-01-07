package ee.sk.mid;

import ee.sk.mid.exception.CertificateNotPresentException;
import ee.sk.mid.exception.ExpiredException;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.TechnicalErrorException;
import ee.sk.mid.mock.MobileIdConnectorSpy;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.TestData.*;

public class CertificateRequestBuilderTest {

    private MobileIdConnectorSpy connector;

    @Before
    public void setUp() {
        connector = new MobileIdConnectorSpy();
        connector.setCertificateChoiceResponseToRespond(createDummyCertificateChoiceResponse());
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutRelyingPartyUUID_shouldThrowException() {
        CertificateRequest request = CertificateRequest.newBuilder()
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .build();

        MobileIdConnector connector = new MobileIdRestConnector(LOCALHOST_URL);
        connector.getCertificate(request);

    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutRelyingPartyName_shouldThrowException() {
        CertificateRequest request = CertificateRequest.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .build();

        MobileIdConnector connector = new MobileIdRestConnector(LOCALHOST_URL);
        connector.getCertificate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutPhoneNumber_shouldThrowException() {
        CertificateRequest request = CertificateRequest.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .build();

        MobileIdConnector connector = new MobileIdRestConnector(LOCALHOST_URL);
        connector.getCertificate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withoutNationalIdentityNumber_shouldThrowException() {
        CertificateRequest request = CertificateRequest.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .build();

        MobileIdConnector connector = new MobileIdRestConnector(LOCALHOST_URL);
        connector.getCertificate(request);
    }

    @Test(expected = CertificateNotPresentException.class)
    public void getCertificate_withCertificateNotPresent_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult("NOT_FOUND");
        makeCertificateRequest(connector);
    }

    @Test(expected = ExpiredException.class)
    public void getCertificate_withInactiveCertificateFound_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult("NOT_ACTIVE");
        makeCertificateRequest(connector);
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withResultMissingInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult(null);
        makeCertificateRequest(connector);
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withResultBlankInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setResult("");
        makeCertificateRequest(connector);
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withCertificateMissingInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setCert(null);
        makeCertificateRequest(connector);
    }

    @Test(expected = TechnicalErrorException.class)
    public void getCertificate_withCertificateBlankInResponse_shouldThrowException() {
        connector.getCertificateChoiceResponseToRespond().setCert("");
        makeCertificateRequest(connector);
    }

    private void makeCertificateRequest(MobileIdConnector connector) {
        CertificateRequest request = CertificateRequest.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .build();

        CertificateChoiceResponse response = connector.getCertificate(request);

        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(LOCALHOST_URL)
                .build();

        client.createMobileIdCertificate(response);
    }

    private static CertificateChoiceResponse createDummyCertificateChoiceResponse() {
        CertificateChoiceResponse certificateChoiceResponse = new CertificateChoiceResponse();
        certificateChoiceResponse.setResult("OK");
        certificateChoiceResponse.setCert(AUTH_CERTIFICATE_EE);
        return certificateChoiceResponse;
    }
}
