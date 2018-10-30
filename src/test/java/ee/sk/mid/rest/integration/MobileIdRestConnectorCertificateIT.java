package ee.sk.mid.rest.integration;

import ee.sk.mid.categories.IntegrationTest;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.UnauthorizedException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createCertificateRequest;
import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.createValidCertificateRequest;
import static ee.sk.mid.mock.MobileIdRestServiceResponseDummy.assertCertificateChosen;
import static ee.sk.mid.mock.TestData.*;

@Category({IntegrationTest.class})
public class MobileIdRestConnectorCertificateIT {

    private MobileIdConnector connector;

    @Before
    public void setUp() {
        connector = new MobileIdRestConnector(HOST_URL);
    }

    @Test
    public void getCertificate() {
        CertificateRequest request = createValidCertificateRequest();
        CertificateChoiceResponse response = connector.getCertificate(request);

        assertCertificateChosen(response);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongPhoneNumber_shouldThrowException() {
        CertificateRequest request = createCertificateRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, WRONG_PHONE, VALID_NAT_IDENTITY);
        connector.getCertificate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongNationalIdentityNumber_shouldThrowException() {
        CertificateRequest request = createCertificateRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, WRONG_NAT_IDENTITY);
        connector.getCertificate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRelyingPartyUUID_shouldThrowException() {
        CertificateRequest request = createCertificateRequest(WRONG_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.getCertificate(request);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRelyingPartyName_shouldThrowException() {
        CertificateRequest request = createCertificateRequest(VALID_RELYING_PARTY_UUID, WRONG_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.getCertificate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withUnknownRelyingPartyUUID_shouldThrowException() {
        CertificateRequest request = createCertificateRequest(VALID_RELYING_PARTY_UUID, UNKNOWN_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.getCertificate(request);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withUnknownRelyingPartyName_shouldThrowException() {
        CertificateRequest request = createCertificateRequest(UNKNOWN_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
        connector.getCertificate(request);
    }
}
