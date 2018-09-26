package ee.sk.mid;

import java.security.SecureRandom;

/**
 * Class containing the hash and its hash type used for authentication
 */
class AuthenticationHash extends SignableHash {

    static AuthenticationHash generateRandomHash() {
        AuthenticationHash authenticationHash = new AuthenticationHash();
        byte[] generatedDigest = DigestCalculator.calculateDigest(getRandomBytes(), HashType.SHA512);
        authenticationHash.setHash(generatedDigest);
        authenticationHash.setHashType(HashType.SHA512);
        return authenticationHash;
    }

    private static byte[] getRandomBytes() {
        byte randBytes[] = new byte[64];
        new SecureRandom().nextBytes(randBytes);
        return randBytes;
    }
}
