package ee.sk.mid;

class VerificationCodeCalculator {

    private static final int MINIMUM_HASH_LENGTH = 20;

    static String calculate(byte[] hash) {
        return String.format("%04d", validateHash(hash) ? ((0xFC & hash[0]) << 5) | (hash[(hash.length / 2) - 1] & 0x7F) : 0);
    }

    private static boolean validateHash(byte[] hash) {
        return hash != null && hash.length >= MINIMUM_HASH_LENGTH;
    }
}
