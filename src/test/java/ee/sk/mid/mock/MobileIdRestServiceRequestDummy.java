package ee.sk.mid.mock;

import ee.sk.mid.*;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;

import java.security.cert.X509Certificate;

import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

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

    public static X509Certificate createCertificate(MobileIdClient client) {
        return client
                .getCertificate()
                .withPhoneNumber(VALID_PHONE)
                .withNationalIdentityNumber(VALID_NAT_IDENTITY)
                .fetch();
    }

    public static MobileIdSignature createValidSignature(MobileIdClient client) {
        return createSignature(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    public static MobileIdSignature createSignature(MobileIdClient client, String phoneNumber, String nationalIdentityNumber) {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        assertThat(hashToSign.calculateVerificationCode(), is("0108"));

        return client
                .createSignature()
                .withPhoneNumber(phoneNumber)
                .withNationalIdentityNumber(nationalIdentityNumber)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    public static MobileIdAuthentication createAuthentication(MobileIdClient client, String phoneNumber, String nationalIdentityNumber, MobileIdAuthenticationHash authenticationHash) {
        return client
                .createAuthentication()
                .withPhoneNumber(phoneNumber)
                .withNationalIdentityNumber(nationalIdentityNumber)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    public static MobileIdAuthenticationHash createAuthenticationSHA512Hash() {
        MobileIdAuthenticationHash authenticationHash = new MobileIdAuthenticationHash();
        authenticationHash.setHashInBase64(SHA512_HASH_IN_BASE64);
        authenticationHash.setHashType(HashType.SHA512);
        return authenticationHash;
    }

    public static void makeValidCertificateRequest(MobileIdClient client) {
        makeCertificateRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    public static void makeCertificateRequest(MobileIdClient client, String phoneNumber, String nationalIdentityNumber) {
        client
                .getCertificate()
                .withPhoneNumber(phoneNumber)
                .withNationalIdentityNumber(nationalIdentityNumber)
                .fetch();
    }

    public static void makeValidSignatureRequest(MobileIdClient client) {
        makeSignatureRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    public static void makeSignatureRequest(MobileIdClient client, String phoneNumber, String nationalIdentityNumber) {
        SignableHash hashToSign = new SignableHash();
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);

        client
                .createSignature()
                .withPhoneNumber(phoneNumber)
                .withNationalIdentityNumber(nationalIdentityNumber)
                .withSignableHash(hashToSign)
                .withLanguage(Language.EST)
                .sign();
    }

    public static void makeValidAuthenticationRequest(MobileIdClient client) {
        makeAuthenticationRequest(client, VALID_PHONE, VALID_NAT_IDENTITY);
    }

    public static void makeAuthenticationRequest(MobileIdClient client, String phoneNumber, String nationalIdentityNumber) {
        MobileIdAuthenticationHash authenticationHash = createAuthenticationSHA512Hash();

        client
                .createAuthentication()
                .withPhoneNumber(phoneNumber)
                .withNationalIdentityNumber(nationalIdentityNumber)
                .withAuthenticationHash(authenticationHash)
                .withLanguage(Language.EST)
                .authenticate();
    }

    private static String calculateHashInBase64(HashType hashType) {
        byte[] digestValue = DigestCalculator.calculateDigest(DATA_TO_SIGN, hashType);
        return Base64.encodeBase64String(digestValue);
    }

    public static void assertCertificateCreated(X509Certificate certificate) {
        assertThat(certificate, is(notNullValue()));
    }

    public static void assertSignatureCreated(MobileIdSignature signature) {
        assertThat(signature, is(notNullValue()));
        assertThat(signature.getValueInBase64(), not(isEmptyOrNullString()));
        assertThat(signature.getAlgorithmName(), not(isEmptyOrNullString()));
    }

    public static void assertAuthenticationCreated(MobileIdAuthentication authentication, String expectedHashToSignInBase64) {
        assertThat(authentication, is(notNullValue()));
        assertThat(authentication.getResult(), not(isEmptyOrNullString()));
        assertThat(authentication.getSignatureValueInBase64(), not(isEmptyOrNullString()));
        assertThat(authentication.getCertificate(), is(notNullValue()));
        assertThat(authentication.getSignedHashInBase64(), is(expectedHashToSignInBase64));
        assertThat(authentication.getHashType(), Matchers.is(HashType.SHA512));
    }
}
