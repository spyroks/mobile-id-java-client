package ee.sk.mid.integration;

import ee.sk.mid.MobileIdClient;
import ee.sk.mid.categories.IntegrationTest;
import ee.sk.mid.exception.CertificateNotPresentException;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.UnauthorizedException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.security.cert.X509Certificate;

import static ee.sk.mid.mock.MobileIdRestServiceRequestDummy.*;
import static ee.sk.mid.mock.TestData.*;

@Category({IntegrationTest.class})
public class MobileIdCertificateIT {

    @Test
    public void getCertificate() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        X509Certificate certificate = createCertificate(client);

        assertCertificateCreated(certificate);
    }

    @Test(expected = CertificateNotPresentException.class)
    public void getCertificate_whenCertificateNotPresent_shouldThrowException() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        makeCertificateRequest(client, VALID_PHONE_NOT_MID_CLIENT, VALID_NAT_IDENTITY_NOT_MID_CLIENT);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongPhoneNumber_shouldThrowException() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        makeCertificateRequest(client, WRONG_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongNationalIdentityNumber_shouldThrowException() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        makeCertificateRequest(client, VALID_PHONE, WRONG_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRelyingPartyUUID_shouldThrowException() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(WRONG_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = ParameterMissingException.class)
    public void getCertificate_withWrongRelyingPartyName_shouldThrowException() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(WRONG_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withUnknownRelyingPartyUUID_shouldThrowException() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(UNKNOWN_RELYING_PARTY_UUID)
                .withRelyingPartyName(VALID_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    @Test(expected = UnauthorizedException.class)
    public void getCertificate_withUnknownRelyingPartyName_shouldThrowException() {
        MobileIdClient client = MobileIdClient.newBuilder()
                .withRelyingPartyUUID(VALID_RELYING_PARTY_UUID)
                .withRelyingPartyName(UNKNOWN_RELYING_PARTY_NAME)
                .withHostUrl(DEMO_HOST_URL)
                .build();

        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }
}
