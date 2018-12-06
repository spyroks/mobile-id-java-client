package ee.sk.mid;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VerificationCodeCalculatorTest {

    private static final String HACKERMAN_SHA256 = "HACKERMAN_SHA256";
    private static final String HACKERMAN_SHA384 = "HACKERMAN_SHA384";
    private static final String HACKERMAN_SHA512 = "HACKERMAN_SHA512";

    @Test
    public void calculateVerificationCode_withSHA256() {
        String verificationCode = calculateVerificationCode(getStringDigest(HACKERMAN_SHA256, HashType.SHA256));
        assertThat(verificationCode, is("5924"));
    }

    @Test
    public void calculateVerificationCode_withSHA384() {
        String verificationCode = calculateVerificationCode(getStringDigest(HACKERMAN_SHA384, HashType.SHA384));
        assertThat(verificationCode,is("7228"));
    }

    @Test
    public void calculateVerificationCode_withSHA512() {
        String verificationCode = calculateVerificationCode(getStringDigest(HACKERMAN_SHA512, HashType.SHA512));
        assertThat(verificationCode,is("3922"));
    }

    @Test
    public void calculateVerificationCode_withTooShortHash() {
        String verificationCode = calculateVerificationCode(new byte[] {1, 2, 3, 4});
        assertThat(verificationCode,is("0000"));
    }

    @Test
    public void calculateVerificationCode_withNullHash() {
        String verificationCode = calculateVerificationCode(null);
        assertThat(verificationCode,is("0000"));
    }

    private byte[] getStringDigest(String hash, HashType hashType) {
        return DigestCalculator.calculateDigest(hash.getBytes(StandardCharsets.UTF_8), hashType);
    }

    private String calculateVerificationCode(byte[] dummyDocumentHash) {
        return VerificationCodeCalculator.calculateMobileIdVerificationCode(dummyDocumentHash);
    }
}
