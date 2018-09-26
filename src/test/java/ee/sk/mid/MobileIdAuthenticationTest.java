package ee.sk.mid;

import ee.sk.mid.exception.TechnicalErrorException;
import ee.sk.mid.mock.SessionStatusResultDummy;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.security.cert.CertificateEncodingException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MobileIdAuthenticationTest {

    @Test
    public void getSignatureValueInBase64() {
        MobileIdAuthentication authenticationResponse = new MobileIdAuthentication();
        authenticationResponse.setSignatureValueInBase64("SGVsbG8gU21hcnQtSUQgc2lnbmF0dXJlIQ==");
        assertEquals("SGVsbG8gU21hcnQtSUQgc2lnbmF0dXJlIQ==", authenticationResponse.getSignatureValueInBase64());
    }

    @Test
    public void getSignatureValueInBytes() {
        MobileIdAuthentication authenticationResponse = new MobileIdAuthentication();
        authenticationResponse.setSignatureValueInBase64("VGVyZSBhbGxraXJpIQ==");
        assertArrayEquals("Tere allkiri!".getBytes(), authenticationResponse.getSignatureValue());
    }

    @Test(expected = TechnicalErrorException.class)
    public void incorrectBase64StringShouldThrowException() {
        MobileIdAuthentication authenticationResponse = new MobileIdAuthentication();
        authenticationResponse.setSignatureValueInBase64("!IsNotValidBase64Character");
        authenticationResponse.getSignatureValue();
    }

    @Test
    public void getCertificate() throws CertificateEncodingException {
        MobileIdAuthentication authenticationResponse = new MobileIdAuthentication();
        authenticationResponse.setCertificate(CertificateParser.parseX509Certificate(SessionStatusResultDummy.CERTIFICATE));
        assertEquals(SessionStatusResultDummy.CERTIFICATE, Base64.encodeBase64String(authenticationResponse.getCertificate().getEncoded()));
    }
}
