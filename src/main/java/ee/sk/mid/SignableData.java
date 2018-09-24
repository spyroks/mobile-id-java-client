package ee.sk.mid;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;

public class SignableData implements Serializable {

    private byte[] dataToSign;
    private HashType hashType = HashType.SHA512;

    public SignableData(byte[] dataToSign) {
        this.dataToSign = dataToSign;
    }

    String calculateHashInBase64() {
        byte[] digest = calculateHash();
        return Base64.encodeBase64String(digest);
    }

    byte[] calculateHash() {
        return DigestCalculator.calculateDigest(dataToSign, hashType);
    }

    public void setHashType(HashType hashType) {
        this.hashType = hashType;
    }

    HashType getHashType() {
        return hashType;
    }
}
