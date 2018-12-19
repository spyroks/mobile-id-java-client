package ee.sk.mid;

import java.security.SecureRandom;

import static ee.sk.mid.HashType.SHA256;

public class MobileIdAuthenticationHash extends SignableHash {

    private static HashType DEFAULT_HASH_TYPE = SHA256;

    public static MobileIdAuthenticationHash generateRandomHashOfDefaultType() {
        return generateRandomHashOfType(DEFAULT_HASH_TYPE);
    }

    public static MobileIdAuthenticationHash generateRandomHashOfType(HashType hashType) {
        MobileIdAuthenticationHash mobileIdAuthenticationHash = new MobileIdAuthenticationHash();
        byte[] randomHash = getRandomBytes(hashType.getLengthInBytes());
        mobileIdAuthenticationHash.setHash(randomHash);
        mobileIdAuthenticationHash.setHashType(hashType);
        return mobileIdAuthenticationHash;
    }

    private static byte[] getRandomBytes(int lengthInBytes) {
        byte[] randomBytes = new byte[lengthInBytes];
        new SecureRandom().nextBytes(randomBytes);
        return randomBytes;
    }
}
