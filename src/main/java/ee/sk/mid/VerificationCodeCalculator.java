package ee.sk.mid;

import org.bouncycastle.util.encoders.Hex;

class VerificationCodeCalculator {

    private static final int MINIMUM_HASH_LENGTH = 20;

    static int calculate(byte[] hash) {
        if (hash != null && hash.length >= MINIMUM_HASH_LENGTH) {
            byte[] c = Hex.decode(hash);
            return ((0xFC & c[0]) << 5) | (c[(hash.length / 2) - 1] & 0x7F);
        } else {
            return 0;
        }
    }
}
