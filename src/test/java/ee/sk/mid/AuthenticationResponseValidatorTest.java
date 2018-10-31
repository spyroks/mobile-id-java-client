package ee.sk.mid;

import ee.sk.mid.exception.TechnicalErrorException;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.cert.X509Certificate;
import java.util.Date;

import static ee.sk.mid.MobileIdAuthenticationError.*;
import static ee.sk.mid.mock.TestData.*;
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
    public void validationReturnsValidAuthenticationResult() throws Exception {
        MobileIdAuthentication response = createValidValidationResponse();
        MobileIdAuthenticationResult authenticationResult = validator.validate(response);

        assertThat(authenticationResult.isValid(), is(true));
        assertThat(authenticationResult.getErrors().isEmpty(), is(true));
        assertAuthenticationIdentityValid(authenticationResult.getAuthenticationIdentity(), response.getCertificate());
    }

    @Test
    public void validationReturnsValidAuthenticationResult_whenResultLowerCase() throws Exception {
        MobileIdAuthentication response = createValidValidationResponse();
        response.setResult("ok");
        MobileIdAuthenticationResult authenticationResult = validator.validate(response);

        assertThat(authenticationResult.isValid(), is(true));
        assertThat(authenticationResult.getErrors().isEmpty(), is(true));
        assertAuthenticationIdentityValid(authenticationResult.getAuthenticationIdentity(), response.getCertificate());
    }

    @Test
    public void validationReturnsInvalidAuthenticationResult_whenResultNotOk() throws Exception {
        MobileIdAuthentication response = createValidationResponseWithInvalidResult();
        MobileIdAuthenticationResult authenticationResult = validator.validate(response);

        assertThat(authenticationResult.isValid(), is(false));
        assertThat(authenticationResult.getErrors().contains("Response result verification failed"), is(true));
        assertAuthenticationIdentityValid(authenticationResult.getAuthenticationIdentity(), response.getCertificate());
    }

    @Test
    public void validationReturnsInvalidAuthenticationResult_whenSignatureVerificationFails() throws Exception {
        MobileIdAuthentication response = createValidationResponseWithInvalidSignature();
        MobileIdAuthenticationResult authenticationResult = validator.validate(response);

        assertThat(authenticationResult.isValid(), is(false));
        assertThat(authenticationResult.getErrors().contains("Signature verification failed"), is(true));
        assertAuthenticationIdentityValid(authenticationResult.getAuthenticationIdentity(), response.getCertificate());
    }

    @Test
    public void validationReturnsInvalidAuthenticationResult_whenSignersCertExpired() throws Exception {
        MobileIdAuthentication response = createValidationResponseWithExpiredCertificate();
        MobileIdAuthenticationResult authenticationResult = validator.validate(response);

        assertThat(authenticationResult.isValid(), is(false));
        assertThat(authenticationResult.getErrors().contains("Signer's certificate expired"), is(true));
        assertAuthenticationIdentityValid(authenticationResult.getAuthenticationIdentity(), response.getCertificate());
    }

    @Test(expected = TechnicalErrorException.class)
    public void whenCertificateIsNull_shouldThrowException() {
        MobileIdAuthentication response = createValidValidationResponse();
        response.setCertificate(null);
        validator.validate(response);
    }

    @Test(expected = TechnicalErrorException.class)
    public void whenSignatureIsEmpty_shouldThrowException() {
        MobileIdAuthentication response = createValidValidationResponse();
        response.setSignatureValueInBase64("");
        validator.validate(response);
    }

    @Test(expected = TechnicalErrorException.class)
    public void whenHashTypeIsNull_shouldThrowException() {
        MobileIdAuthentication response = createValidValidationResponse();
        response.setHashType(null);
        validator.validate(response);
    }

    private MobileIdAuthentication createValidValidationResponse() {
        return createValidationResponse("OK", VALID_SIGNATURE_IN_BASE64);
    }

    private MobileIdAuthentication createValidationResponseWithInvalidResult() {
        return createValidationResponse("NOT OK", VALID_SIGNATURE_IN_BASE64);
    }

    private MobileIdAuthentication createValidationResponseWithInvalidSignature() {
        return createValidationResponse("OK", INVALID_SIGNATURE_IN_BASE64);
    }

    private MobileIdAuthentication createValidationResponseWithExpiredCertificate() {
        MobileIdAuthentication response = createValidationResponse("OK", VALID_SIGNATURE_IN_BASE64);
        X509Certificate certificateSpy = spy(response.getCertificate());
        when(certificateSpy.getNotAfter()).thenReturn(DateUtils.addHours(new Date(), -1));
        response.setCertificate(certificateSpy);
        return response;
    }

    private MobileIdAuthentication createValidationResponse(String result, String signatureInBase64) {
        MobileIdAuthentication authentication = new MobileIdAuthentication();
        authentication.setResult(result);
        authentication.setSignatureValueInBase64(signatureInBase64);
        authentication.setCertificate(CertificateParser.parseX509Certificate(CERTIFICATE));
        authentication.setSignedHashInBase64(HASH_TO_SIGN_IN_BASE64);
        authentication.setHashType(HashType.SHA512);
        return authentication;
    }

    private void assertAuthenticationIdentityValid(AuthenticationIdentity authenticationIdentity, X509Certificate certificate) throws InvalidNameException {
        LdapName ln = new LdapName(certificate.getSubjectDN().getName());
        for (Rdn rdn : ln.getRdns()) {
            String type = rdn.getType().toUpperCase();
            String valueFromCertificate = rdn.getValue().toString();
            switch (type) {
                case "GIVENNAME":
                    assertThat(authenticationIdentity.getGivenName(), is(valueFromCertificate));
                    break;
                case "SURNAME":
                    assertThat(authenticationIdentity.getSurName(), is(valueFromCertificate));
                    break;
                case "SERIALNUMBER":
                    assertThat(authenticationIdentity.getIdentityCode(), is(valueFromCertificate));
                    break;
                case "C":
                    assertThat(authenticationIdentity.getCountry(), is(valueFromCertificate));
                    break;
            }
        }
    }
}
