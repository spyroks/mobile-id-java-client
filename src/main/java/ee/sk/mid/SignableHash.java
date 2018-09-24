package ee.sk.mid;

import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;

public class SignableHash implements Serializable {

    private byte[] hash;
    private HashType hashType;

    void setHash(byte[] hash) {
        this.hash = hash;

    }

    public void setHashInBase64(String hashInBase64) {
        hash = Base64.decodeBase64(hashInBase64);
    }

    String getHashInBase64() {
        return Base64.encodeBase64String(hash);
    }

    HashType getHashType() {
        return hashType;
    }

    void setHashType(HashType hashType) {
        this.hashType = hashType;
    }

    boolean areFieldsFilled() {
        return hashType != null && hash != null && hash.length > 0;
    }
}
