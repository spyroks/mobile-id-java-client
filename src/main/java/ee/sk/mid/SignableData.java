package ee.sk.mid;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;

public class SignableData implements Serializable {

    private byte[] dataToSign;
    private HashType hashType = HashType.SHA512;

    public SignableData(byte[] dataToSign) {
        this.dataToSign = dataToSign;
    }

    public HashType getHashType() {
        return hashType;
    }

    public void setHashType(HashType hashType) {
        this.hashType = hashType;
    }

    public String calculateHashInBase64() {
        byte[] digest = calculateHash();
        return Base64.encodeBase64String(digest);
    }

    public byte[] calculateHash() {
        return DigestCalculator.calculateDigest(dataToSign, hashType);
    }

    public String calculateVerificationCode() {
        return VerificationCodeCalculator.calculate(calculateHash());
    }
}
