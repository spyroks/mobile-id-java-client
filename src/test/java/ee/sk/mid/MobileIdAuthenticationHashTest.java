package ee.sk.mid;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MobileIdAuthenticationHashTest {

    @Test
    public void shouldGenerateRandomHashOfDefaultType_hasSHA256HashType() {
        MobileIdAuthenticationHash mobileIdAuthenticationHash = MobileIdAuthenticationHash.generateRandomHashOfDefaultType();

        assertThat(mobileIdAuthenticationHash.getHashType(), is(HashType.SHA256));
        assertThat(mobileIdAuthenticationHash.getHashInBase64().length(), is(44));
    }

    @Test
    public void shouldGenerateRandomHashOfType_SHA256_hashHasCorrectTypeAndLength() {
        MobileIdAuthenticationHash mobileIdAuthenticationHash = MobileIdAuthenticationHash.generateRandomHashOfType(HashType.SHA256);

        assertThat(mobileIdAuthenticationHash.getHashType(), is(HashType.SHA256));
        assertThat(mobileIdAuthenticationHash.getHashInBase64().length(), is(44));
    }

    @Test
    public void shouldGenerateRandomHashOfType_SHA384_hashHasCorrectTypeAndLength() {
        MobileIdAuthenticationHash mobileIdAuthenticationHash = MobileIdAuthenticationHash.generateRandomHashOfType(HashType.SHA384);

        assertThat(mobileIdAuthenticationHash.getHashType(), is(HashType.SHA384));
        assertThat(mobileIdAuthenticationHash.getHashInBase64().length(), is(64));
    }

    @Test
    public void shouldGenerateRandomHashOfType_SHA512_hashHasCorrectTypeAndLength() {
        MobileIdAuthenticationHash mobileIdAuthenticationHash = MobileIdAuthenticationHash.generateRandomHashOfType(HashType.SHA512);

        assertThat(mobileIdAuthenticationHash.getHashType(), is(HashType.SHA512));
        assertThat(mobileIdAuthenticationHash.getHashInBase64().length(), is(88));
    }

}