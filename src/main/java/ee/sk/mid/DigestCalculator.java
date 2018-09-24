package ee.sk.mid;

import org.apache.commons.codec.digest.DigestUtils;

class DigestCalculator {

    static byte[] calculateDigest(byte[] dataToDigest, HashType hashType) {
        String algorithmName = hashType.getAlgorithmName();
        return DigestUtils.getDigest(algorithmName).digest(dataToDigest);
    }
}
