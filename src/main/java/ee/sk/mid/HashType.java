package ee.sk.mid;

import java.util.Arrays;

public enum HashType {

    SHA256("SHA-256", "SHA256", 256, new byte[]{0x30, 0x31, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x01, 0x05, 0x00, 0x04, 0x20}),
    SHA384("SHA-384", "SHA384", 384, new byte[]{0x30, 0x41, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x02, 0x05, 0x00, 0x04, 0x30}),
    SHA512("SHA-512", "SHA512", 512, new byte[]{0x30, 0x51, 0x30, 0x0d, 0x06, 0x09, 0x60, (byte) 0x86, 0x48, 0x01, 0x65, 0x03, 0x04, 0x02, 0x03, 0x05, 0x00, 0x04, 0x40});

    private String algorithmName;
    private String hashTypeName;
    private int lengthInBits;
    private byte[] digestInfoPrefix;

    HashType(String algorithmName, String hashTypeName, int lengthInBits, byte[] digestInfoPrefix) {
        this.algorithmName = algorithmName;
        this.hashTypeName = hashTypeName;
        this.lengthInBits = lengthInBits;
        this.digestInfoPrefix = digestInfoPrefix;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public String getHashTypeName() {
        return hashTypeName;
    }

    public int getLengthInBytes() {
        return lengthInBits / 8;
    }

    public byte[] getDigestInfoPrefix() {
        return Arrays.copyOf(digestInfoPrefix, digestInfoPrefix.length);
    }
}
