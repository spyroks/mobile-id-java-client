package ee.sk.mid;

import org.bouncycastle.util.encoders.Hex;

class VerificationCodeCalculator {

    private static final int MINIMUM_HASH_LENGTH = 40;

    static int calculate(String hash) {
        if (hash != null && hash.length() >= MINIMUM_HASH_LENGTH) {
            byte[] c = Hex.decode(hash.getBytes());
            return ((0xFC & c[0]) << 5) | (c[(hash.length() / 2) - 1] & 0x7F);
        } else {
            return 0;
        }
    }
}
