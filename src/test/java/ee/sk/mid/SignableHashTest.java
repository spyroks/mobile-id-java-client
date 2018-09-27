package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.TestData.HASH_TO_SIGN;
import static ee.sk.mid.mock.TestData.SHA256_HASH_IN_BASE64;
import static ee.sk.mid.mock.TestData.SHA384_HASH_IN_BASE64;
import static ee.sk.mid.mock.TestData.SHA512_HASH_IN_BASE64;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SignableHashTest {

    private SignableHash hashToSign;

    @Before
    public void setUp() {
        hashToSign = new SignableHash();
    }

    @Test(expected = InvalidBase64CharacterException.class)
    public void setInvalidHashInBase64_shouldThrowException() {
        hashToSign.setHashInBase64("!IsNotValidBase64Character");
    }

    @Test
    public void calculateVerificationCode_withSha256() {
        hashToSign.setHash(DigestCalculator.calculateDigest(HASH_TO_SIGN, HashType.SHA256));
        hashToSign.setHashType(HashType.SHA256);
        assertThat(hashToSign.calculateVerificationCode(), is("0108"));
        assertThat(hashToSign.areFieldsFilled(), is(true));
    }

    @Test
    public void setHashInBase64_calculateVerificationCode_withSha256() {
        hashToSign.setHashInBase64(SHA256_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA256);
        assertThat(hashToSign.calculateVerificationCode(), is("0108"));
        assertThat(hashToSign.areFieldsFilled(), is(true));
    }

    @Test
    public void calculateVerificationCode_withSha384() {
        hashToSign.setHash(DigestCalculator.calculateDigest(HASH_TO_SIGN, HashType.SHA384));
        hashToSign.setHashType(HashType.SHA384);
        assertThat(hashToSign.calculateVerificationCode(), is("5775"));
        assertThat(hashToSign.areFieldsFilled(), is(true));
    }

    @Test
    public void setHashInBase64_calculateVerificationCode_withSha384() {
        hashToSign.setHashInBase64(SHA384_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA384);
        assertThat(hashToSign.calculateVerificationCode(), is("5775"));
        assertThat(hashToSign.areFieldsFilled(), is(true));
    }

    @Test
    public void calculateVerificationCode_withSha512() {
        hashToSign.setHash(DigestCalculator.calculateDigest(HASH_TO_SIGN, HashType.SHA512));
        hashToSign.setHashType(HashType.SHA512);
        assertThat(hashToSign.calculateVerificationCode(), is("4677"));
        assertThat(hashToSign.areFieldsFilled(), is(true));
    }

    @Test
    public void setHashInBase64_calculateVerificationCode_withSha512() {
        hashToSign.setHashInBase64(SHA512_HASH_IN_BASE64);
        hashToSign.setHashType(HashType.SHA512);
        assertThat(hashToSign.calculateVerificationCode(), is("4677"));
        assertThat(hashToSign.areFieldsFilled(), is(true));
    }

    @Test
    public void checkFields_withOutHashType() {
        hashToSign.setHashInBase64(SHA512_HASH_IN_BASE64);
        assertThat(hashToSign.areFieldsFilled(), is(false));
    }

    @Test
    public void checkFields_withOutHash() {
        hashToSign.setHashType(HashType.SHA512);
        assertThat(hashToSign.areFieldsFilled(), is(false));
    }

    @Test
    public void checkFields_withOutHash_andWithoutHashType() {
        assertThat(hashToSign.areFieldsFilled(), is(false));
    }
}
