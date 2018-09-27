package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;

import static org.apache.commons.codec.binary.Base64.isBase64;

public class SignableHash implements Serializable {

    private byte[] hash;
    private HashType hashType;

    void setHash(byte[] hash) {
        this.hash = hash;

    }

    String getHashInBase64() {
        return Base64.encodeBase64String(hash);
    }

    public void setHashInBase64(String hashInBase64) {
        if (isBase64(hashInBase64)) {
            hash = Base64.decodeBase64(hashInBase64);
        } else {
            throw new InvalidBase64CharacterException();
        }
    }

    HashType getHashType() {
        return hashType;
    }

    void setHashType(HashType hashType) {
        this.hashType = hashType;
    }

    public String calculateVerificationCode() {
        return VerificationCodeCalculator.calculate(hash);
    }

    boolean areFieldsFilled() {
        return hashType != null && hash != null && hash.length > 0;
    }
}
