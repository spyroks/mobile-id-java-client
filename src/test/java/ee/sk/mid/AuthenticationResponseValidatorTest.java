package ee.sk.mid;

import ee.sk.mid.exception.TechnicalErrorException;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.X509Certificate;
import java.util.Date;

import static ee.sk.mid.mock.TestData.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class AuthenticationResponseValidatorTest {

    private AuthenticationResponseValidator validator;

    @Before
    public void setUp() {
        validator = new AuthenticationResponseValidator();
    }

    @Test
    public void validate_whenRSA_shouldReturnValidAuthenticationResult() {
        MobileIdAuthentication authentication = createValidMobileIdAuthentication();
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertThat(authenticationResult.isValid(), is(true));
        assertThat(authenticationResult.getErrors().isEmpty(), is(true));
    }

    @Test
    public void validate_whenECC_shouldReturnValidAuthenticationResult() {
        MobileIdAuthentication authentication = createMobileIdAuthenticationWithECC();
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertThat(authenticationResult.isValid(), is(true));
        assertThat(authenticationResult.getErrors().isEmpty(), is(true));
    }

    @Test
    public void validate_whenResultLowerCase_shouldReturnValidAuthenticationResult() {
        MobileIdAuthentication authentication = createValidMobileIdAuthentication();
        authentication.setResult("ok");
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertThat(authenticationResult.isValid(), is(true));
        assertThat(authenticationResult.getErrors().isEmpty(), is(true));
    }

    @Test
    public void validate_whenResultNotOk_shouldReturnInvalidAuthenticationResult() {
        MobileIdAuthentication authentication = createMobileIdAuthenticationWithInvalidResult();
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertThat(authenticationResult.isValid(), is(false));
        assertThat(authenticationResult.getErrors(), contains("Response result verification failed"));
    }

    @Test
    public void validate_whenSignatureVerificationFails_shouldReturnInvalidAuthenticationResult() {
        MobileIdAuthentication authentication = createMobileIdAuthenticationWithInvalidSignature();
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertThat(authenticationResult.isValid(), is(false));
        assertThat(authenticationResult.getErrors(), contains("Signature verification failed"));
    }

    @Test
    public void validate_whenSignersCertExpired_shouldReturnInvalidAuthenticationResult() {
        MobileIdAuthentication authentication = createMobileIdAuthenticationWithExpiredCertificate();
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertThat(authenticationResult.isValid(), is(false));
        assertThat(authenticationResult.getErrors(), contains("Signer's certificate expired"));
    }

    @Test
    public void validate_shouldReturnValidIdentity() {
        MobileIdAuthentication authentication = createValidMobileIdAuthentication();
        MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

        assertThat(authenticationResult.getAuthenticationIdentity().getGivenName(), is("MARY ÄNN"));
        assertThat(authenticationResult.getAuthenticationIdentity().getSurName(), is("O’CONNEŽ-ŠUSLIK TESTNUMBER"));
        assertThat(authenticationResult.getAuthenticationIdentity().getIdentityCode(), is("60001019906"));
        assertThat(authenticationResult.getAuthenticationIdentity().getCountry(), is("EE"));
    }

    @Test(expected = TechnicalErrorException.class)
    public void validate_whenCertificateIsNull_shouldThrowException() {
        MobileIdAuthentication authentication = createValidMobileIdAuthentication();
        authentication.setCertificate(null);
        validator.validate(authentication);
    }

    @Test(expected = TechnicalErrorException.class)
    public void validate_whenSignatureIsEmpty_shouldThrowException() {
        MobileIdAuthentication authentication = createValidMobileIdAuthentication();
        authentication.setSignatureValueInBase64("");
        validator.validate(authentication);
    }

    @Test(expected = TechnicalErrorException.class)
    public void validate_whenHashTypeIsNull_shouldThrowException() {
        MobileIdAuthentication authentication = createValidMobileIdAuthentication();
        authentication.setHashType(null);
        validator.validate(authentication);
    }

    private MobileIdAuthentication createValidMobileIdAuthentication() {
        return createMobileIdAuthentication("OK", VALID_SIGNATURE_IN_BASE64);
    }

    private MobileIdAuthentication createMobileIdAuthenticationWithInvalidResult() {
        return createMobileIdAuthentication("NOT OK", VALID_SIGNATURE_IN_BASE64);
    }

    private MobileIdAuthentication createMobileIdAuthenticationWithInvalidSignature() {
        return createMobileIdAuthentication("OK", INVALID_SIGNATURE_IN_BASE64);
    }

    private MobileIdAuthentication createMobileIdAuthenticationWithExpiredCertificate() {
        MobileIdAuthentication authentication = createMobileIdAuthentication("OK", VALID_SIGNATURE_IN_BASE64);
        X509Certificate certificateSpy = spy(authentication.getCertificate());
        when(certificateSpy.getNotAfter()).thenReturn(DateUtils.addHours(new Date(), -1));
        authentication.setCertificate(certificateSpy);
        return authentication;
    }

    private MobileIdAuthentication createMobileIdAuthentication(String result, String signatureInBase64) {
        MobileIdAuthentication authentication = new MobileIdAuthentication();
        authentication.setResult(result);
        authentication.setSignatureValueInBase64(signatureInBase64);
        authentication.setCertificate(CertificateParser.parseX509Certificate(AUTH_CERTIFICATE_EE));
        authentication.setSignedHashInBase64(SIGNED_HASH_IN_BASE64);
        authentication.setHashType(HashType.SHA512);
        return authentication;
    }

    private MobileIdAuthentication createMobileIdAuthenticationWithECC() {
        MobileIdAuthentication authentication = new MobileIdAuthentication();
        authentication.setResult("OK");
        authentication.setSignatureValueInBase64(VALID_ECC_SIGNATURE_IN_BASE64);
        authentication.setCertificate(CertificateParser.parseX509Certificate(ECC_CERTIFICATE));
        authentication.setSignedHashInBase64(SIGNED_ECC_HASH_IN_BASE64);
        authentication.setHashType(HashType.SHA512);
        return authentication;
    }

    @Test
    public void constructAuthenticationIdentity_withEECertificate() {
        X509Certificate certificateEe = CertificateParser.parseX509Certificate(AUTH_CERTIFICATE_EE);
        AuthenticationIdentity authenticationIdentity = validator.constructAuthenticationIdentity(certificateEe);

        assertThat(authenticationIdentity.getGivenName(), is("MARY ÄNN"));
        assertThat(authenticationIdentity.getSurName(), is("O’CONNEŽ-ŠUSLIK TESTNUMBER"));
        assertThat(authenticationIdentity.getIdentityCode(), is("60001019906"));
        assertThat(authenticationIdentity.getCountry(), is("EE"));
    }

    @Test
    public void constructAuthenticationIdentity_withLVCertificate() {
        X509Certificate certificateLv = CertificateParser.parseX509Certificate(AUTH_CERTIFICATE_LV);
        AuthenticationIdentity authenticationIdentity = validator.constructAuthenticationIdentity(certificateLv);

        assertThat(authenticationIdentity.getGivenName(), is("FORENAME-010117-21234"));
        assertThat(authenticationIdentity.getSurName(), is("SURNAME-010117-21234"));
        assertThat(authenticationIdentity.getIdentityCode(), is("010117-21234"));
        assertThat(authenticationIdentity.getCountry(), is("LV"));
    }

    @Test
    public void constructAuthenticationIdentity_withLTCertificate() {
        X509Certificate certificateLt = CertificateParser.parseX509Certificate(AUTH_CERTIFICATE_LT);
        AuthenticationIdentity authenticationIdentity = validator.constructAuthenticationIdentity(certificateLt);

        assertThat(authenticationIdentity.getGivenName(), is("FORENAMEPNOLT-36009067968"));
        assertThat(authenticationIdentity.getSurName(), is("SURNAMEPNOLT-36009067968"));
        assertThat(authenticationIdentity.getIdentityCode(), is("36009067968"));
        assertThat(authenticationIdentity.getCountry(), is("LT"));
    }
}
