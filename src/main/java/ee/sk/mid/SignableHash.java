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

    public String getHashInBase64() {
        return Base64.encodeBase64String(hash);
    }

    public void setHashInBase64(String hashInBase64) throws InvalidBase64CharacterException {
        if (isBase64(hashInBase64)) {
            hash = Base64.decodeBase64(hashInBase64);
        } else {
            throw new InvalidBase64CharacterException();
        }
    }

    public HashType getHashType() {
        return hashType;
    }

    public void setHashType(HashType hashType) {
        this.hashType = hashType;
    }

    public String calculateVerificationCode() {
        return VerificationCodeCalculator.calculateMobileIdVerificationCode(hash);
    }

    public boolean areFieldsFilled() {
        return hashType != null && hash != null && hash.length > 0;
    }
}
