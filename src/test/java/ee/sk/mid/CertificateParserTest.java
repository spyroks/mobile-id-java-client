package ee.sk.mid;

import ee.sk.mid.exception.TechnicalErrorException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import static ee.sk.mid.mock.TestData.CERTIFICATE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CertificateParserTest {

    @Test
    public void parseCertificate() throws CertificateEncodingException {
        X509Certificate x509Certificate = CertificateParser.parseX509Certificate(CERTIFICATE);
        assertThat(Base64.encodeBase64String(x509Certificate.getEncoded()), is(CERTIFICATE));
    }

    @Test(expected = TechnicalErrorException.class)
    public void parseInvalidCertificate_shouldThrowException() {
        CertificateParser.parseX509Certificate("HACKERMAN");
    }
}
