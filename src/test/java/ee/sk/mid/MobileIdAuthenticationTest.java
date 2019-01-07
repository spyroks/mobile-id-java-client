package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;

import static ee.sk.mid.mock.TestData.AUTH_CERTIFICATE_EE;
import static ee.sk.mid.mock.TestData.SIGNED_HASH_IN_BASE64;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MobileIdAuthenticationTest {

    @Test(expected = InvalidBase64CharacterException.class)
    public void setInvalidValueInBase64_shouldThrowException() {

        MobileIdAuthentication authentication = MobileIdAuthentication.newBuilder()
                .withSignatureValueInBase64("!IsNotValidBase64Character")
                .build();

        authentication.getSignatureValue();
    }

    @Test
    public void getSignatureValueInBase64() {
        MobileIdAuthentication authentication = MobileIdAuthentication.newBuilder()
                .withResult("OK")
                .withSignatureValueInBase64("SEFDS0VSTUFO")
                .withCertificate(CertificateParser.parseX509Certificate(AUTH_CERTIFICATE_EE))
                .withSignedHashInBase64(SIGNED_HASH_IN_BASE64)
                .withHashType(HashType.SHA512)
                .build();

        assertThat(authentication.getSignatureValueInBase64(), is("SEFDS0VSTUFO"));
    }

    @Test
    public void getSignatureValueInBytes() {
        MobileIdAuthentication authentication = MobileIdAuthentication.newBuilder()
                .withResult("OK")
                .withSignatureValueInBase64("SEFDS0VSTUFO")
                .withCertificate(CertificateParser.parseX509Certificate(AUTH_CERTIFICATE_EE))
                .withSignedHashInBase64("K74MSLkafRuKZ1Ooucvh2xa4Q3nz+R/hFWIShN96SPHNcem+uQ6mFMe9kkJQqp5EaoZnJeaFpl310TmlzRgNyQ==")
                .withHashType(HashType.SHA512)
                .build();

        assertThat(authentication.getSignatureValue(), is("HACKERMAN".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void createMobileIdAuthentication() throws CertificateEncodingException {
        MobileIdAuthentication authentication = MobileIdAuthentication.newBuilder()
                .withResult("OK")
                .withSignatureValueInBase64("SEFDS0VSTUFO")
                .withCertificate(CertificateParser.parseX509Certificate(AUTH_CERTIFICATE_EE))
                .withSignedHashInBase64("K74MSLkafRuKZ1Ooucvh2xa4Q3nz+R/hFWIShN96SPHNcem+uQ6mFMe9kkJQqp5EaoZnJeaFpl310TmlzRgNyQ==")
                .withHashType(HashType.SHA512)
                .withAlgorithmName(HashType.SHA512.getAlgorithmName())
                .build();

        assertThat(authentication.getResult(), is("OK"));
        assertThat(authentication.getSignatureValueInBase64(), is("SEFDS0VSTUFO"));
        assertThat(authentication.getAlgorithmName(), is("SHA-512"));
        assertThat(Base64.encodeBase64String(authentication.getCertificate().getEncoded()), is(AUTH_CERTIFICATE_EE));
        assertThat(authentication.getSignedHashInBase64(), is("K74MSLkafRuKZ1Ooucvh2xa4Q3nz+R/hFWIShN96SPHNcem+uQ6mFMe9kkJQqp5EaoZnJeaFpl310TmlzRgNyQ=="));
        assertThat(authentication.getHashType(), is(HashType.SHA512));
    }
}
