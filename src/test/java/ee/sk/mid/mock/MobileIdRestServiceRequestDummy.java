package ee.sk.mid.mock;

import ee.sk.mid.DigestCalculator;
import ee.sk.mid.HashType;
import ee.sk.mid.Language;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import org.apache.commons.codec.binary.Base64;

import static ee.sk.mid.mock.TestData.*;

public class MobileIdRestServiceRequestDummy {

    public static CertificateRequest createValidCertificateRequest() {
        return createCertificateRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    public static CertificateRequest createCertificateRequest(String UUID, String name, String phoneNumber, String nationalIdentityNumber) {
        CertificateRequest request = new CertificateRequest();
        request.setRelyingPartyUUID(UUID);
        request.setRelyingPartyName(name);
        request.setPhoneNumber(phoneNumber);
        request.setNationalIdentityNumber(nationalIdentityNumber);
        return request;
    }

    public static SignatureRequest createValidSignatureRequest() {
        return createSignatureRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    public static SignatureRequest createSignatureRequest(String UUID, String name, String phoneNumber, String nationalIdentityNumber) {
        SignatureRequest request = new SignatureRequest();
        request.setRelyingPartyUUID(UUID);
        request.setRelyingPartyName(name);
        request.setPhoneNumber(phoneNumber);
        request.setNationalIdentityNumber(nationalIdentityNumber);
        request.setHash(calculateHashInBase64(HashType.SHA256));
        request.setHashType(HashType.SHA256);
        request.setLanguage(Language.EST);
        return request;
    }

    public static AuthenticationRequest createValidAuthenticationRequest() {
        return createAuthenticationRequest(VALID_RELYING_PARTY_UUID, VALID_RELYING_PARTY_NAME, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    public static AuthenticationRequest createAuthenticationRequest(String UUID, String name, String phoneNumber, String nationalIdentityNumber) {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setRelyingPartyUUID(UUID);
        request.setRelyingPartyName(name);
        request.setPhoneNumber(phoneNumber);
        request.setNationalIdentityNumber(nationalIdentityNumber);
        request.setHash(calculateHashInBase64(HashType.SHA512));
        request.setHashType(HashType.SHA512);
        request.setLanguage(Language.EST);
        return request;
    }

    private static String calculateHashInBase64(HashType hashType) {
        byte[] digestValue = DigestCalculator.calculateDigest(DATA_TO_SIGN, hashType);
        return Base64.encodeBase64String(digestValue);
    }
}
