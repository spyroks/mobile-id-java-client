package ee.sk.mid;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;
import java.util.Arrays;

public class SignableData implements Serializable {

    private byte[] dataToSign;
    private HashType hashType = HashType.SHA512;

    public SignableData(byte[] dataToSign) {
        this.dataToSign = Arrays.copyOf(dataToSign, dataToSign.length);
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
        return VerificationCodeCalculator.calculateMobileIdVerificationCode(calculateHash());
    }
}
