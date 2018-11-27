package ee.sk.mid;

import java.security.SecureRandom;

public class MobileIdAuthenticationHash extends SignableHash {

    public static MobileIdAuthenticationHash generateRandomHash() {
        MobileIdAuthenticationHash mobileIdAuthenticationHash = new MobileIdAuthenticationHash();
        byte[] generatedDigest = DigestCalculator.calculateDigest(getRandomBytes(), HashType.SHA512);
        mobileIdAuthenticationHash.setHash(generatedDigest);
        mobileIdAuthenticationHash.setHashType(HashType.SHA512);
        return mobileIdAuthenticationHash;
    }

    private static byte[] getRandomBytes() {
        byte[] randomBytes = new byte[64];
        new SecureRandom().nextBytes(randomBytes);
        return randomBytes;
    }
}
