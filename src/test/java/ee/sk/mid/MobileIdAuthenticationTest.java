package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.CertificateEncodingException;

import static ee.sk.mid.mock.SessionStatusResultDummy.CERTIFICATE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MobileIdAuthenticationTest {

    private MobileIdAuthentication authentication;

    @Before
    public void setUp() {
        authentication = new MobileIdAuthentication();
    }

    @Test(expected = InvalidBase64CharacterException.class)
    public void setInvalidValueInBase64_shouldThrowException() {
        authentication.setSignatureValueInBase64("!IsNotValidBase64Character");
        authentication.getSignatureValue();
    }

    @Test
    public void getSignatureValueInBase64() {
        authentication.setSignatureValueInBase64("SEFDS0VSTUFO");
        assertThat(authentication.getSignatureValueInBase64(), is("SEFDS0VSTUFO"));
    }

    @Test
    public void getSignatureValueInBytes() {
        authentication.setSignatureValueInBase64("SEFDS0VSTUFO");
        assertThat(authentication.getSignatureValue(), is("HACKERMAN".getBytes()));
    }

    @Test
    public void createMobileIdAuthentication() throws CertificateEncodingException {
        authentication.setResult("OK");
        authentication.setSignatureValueInBase64("SEFDS0VSTUFO");
        authentication.setAlgorithmName(HashType.SHA512.getAlgorithmName());
        authentication.setCertificate(CertificateParser.parseX509Certificate(CERTIFICATE));
        authentication.setSignedHashInBase64("K74MSLkafRuKZ1Ooucvh2xa4Q3nz+R/hFWIShN96SPHNcem+uQ6mFMe9kkJQqp5EaoZnJeaFpl310TmlzRgNyQ==");
        authentication.setHashType(HashType.SHA512);

        assertThat(authentication.getResult(), is("OK"));
        assertThat(authentication.getSignatureValueInBase64(), is("SEFDS0VSTUFO"));
        assertThat(authentication.getAlgorithmName(), is("SHA-512"));
        assertThat(Base64.encodeBase64String(authentication.getCertificate().getEncoded()), is(CERTIFICATE));
        assertThat(authentication.getSignedHashInBase64(), is("K74MSLkafRuKZ1Ooucvh2xa4Q3nz+R/hFWIShN96SPHNcem+uQ6mFMe9kkJQqp5EaoZnJeaFpl310TmlzRgNyQ=="));
        assertThat(authentication.getHashType(), is(HashType.SHA512));
    }
}
