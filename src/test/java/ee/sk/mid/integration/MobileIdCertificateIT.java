package ee.sk.mid.integration;

import ee.sk.mid.MobileIdClient;
import ee.sk.mid.categories.IntegrationTest;
import ee.sk.mid.exception.CertificateNotPresentException;
import ee.sk.mid.exception.ExpiredException;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.UnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.security.cert.X509Certificate;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.TestData.*;

@Category({IntegrationTest.class})
public class MobileIdCertificateIT {

    private MobileIdClient client;

    @Before
    public void setUp() {
        client = new MobileIdClient();
        client.setRelyingPartyUUID(VALID_RELYING_PARTY_UUID);
        client.setRelyingPartyName(VALID_RELYING_PARTY_NAME);
        client.setHostUrl(TEST_HOST_URL);
    }

    @Test
    public void getCertificate() {
        X509Certificate certificate = createCertificate(client);

        assertCertificateCreated(certificate);
    }

    @Test(expected = CertificateNotPresentException.class)
    public void getCertificate_whenCertificateNotPresent_shouldThrowException() {
        makeCertificateRequest(client, VALID_PHONE_NOT_MID_CLIENT, VALID_NAT_IDENTITY_NOT_MID_CLIENT);
    }

    @Test(expected = ExpiredException.class)
    public void getCertificate_whenInactiveCertificateFound_shouldThrowException() {
        makeCertificateRequest(client, VALID_PHONE_NOT_ACTIVE_CERTIFICATE, VALID_NAT_IDENTITY_NOT_ACTIVE_CERTIFICATE);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongPhoneNumber_shouldThrowException() {
        makeCertificateRequest(client, WRONG_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongNationalIdentityNumber_shouldThrowException() {
        makeCertificateRequest(client, VALID_PHONE, WRONG_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRelyingPartyUUID_shouldThrowException() {
        client.setRelyingPartyUUID(WRONG_RELYING_PARTY_UUID);
        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRelyingPartyName_shouldThrowException() {
        client.setRelyingPartyName(WRONG_RELYING_PARTY_NAME);
        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withUnknownRelyingPartyUUID_shouldThrowException() {
        client.setRelyingPartyUUID(UNKNOWN_RELYING_PARTY_UUID);
        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withUnknownRelyingPartyName_shouldThrowException() {
        client.setRelyingPartyName(UNKNOWN_RELYING_PARTY_NAME);
        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }
}
