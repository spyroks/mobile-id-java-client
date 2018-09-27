package ee.sk.mid;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

import static ee.sk.mid.mock.TestData.DATA_TO_SIGN;
import static ee.sk.mid.mock.TestData.SHA256_HASH_IN_BASE64;
import static ee.sk.mid.mock.TestData.SHA384_HASH_IN_BASE64;
import static ee.sk.mid.mock.TestData.SHA512_HASH_IN_BASE64;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SignableDataTest {

    private SignableData signableData;

    @Before
    public void setUp() {
        signableData = new SignableData(DATA_TO_SIGN);
    }

    @Test
    public void signableData_withSha256() {
        signableData.setHashType(HashType.SHA256);
        assertThat(signableData.getHashType().getHashTypeName(), is("SHA256"));
        assertThat(signableData.calculateHashInBase64(), is(SHA256_HASH_IN_BASE64));
        assertThat(signableData.calculateHash(), is(Base64.decodeBase64(SHA256_HASH_IN_BASE64)));
        assertThat(signableData.calculateVerificationCode(), is("0108"));
    }

    @Test
    public void signableData_withSha384() {
        signableData.setHashType(HashType.SHA384);
        assertThat(signableData.getHashType().getHashTypeName(), is("SHA384"));
        assertThat(signableData.calculateHashInBase64(), is(SHA384_HASH_IN_BASE64));
        assertThat(signableData.calculateHash(), is(Base64.decodeBase64(SHA384_HASH_IN_BASE64)));
        assertThat(signableData.calculateVerificationCode(), is("5775"));
    }

    @Test
    public void signableData_withSha512() {
        signableData.setHashType(HashType.SHA512);
        assertThat(signableData.getHashType().getHashTypeName(), is("SHA512"));
        assertThat(signableData.calculateHashInBase64(), is(SHA512_HASH_IN_BASE64));
        assertThat(signableData.calculateHash(), is(Base64.decodeBase64(SHA512_HASH_IN_BASE64)));
        assertThat(signableData.calculateVerificationCode(), is("4677"));
    }
}
