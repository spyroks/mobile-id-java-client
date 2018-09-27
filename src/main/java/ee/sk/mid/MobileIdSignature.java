package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.apache.commons.codec.binary.Base64;

public class MobileIdSignature {

    private String valueInBase64;
    private String algorithmName;

    public byte[] getValue() {
        if (!Base64.isBase64(valueInBase64)) {
            throw new InvalidBase64CharacterException("Failed to parse signature value in base64. Probably incorrectly encoded base64 string: '" + valueInBase64);
        }
        return Base64.decodeBase64(valueInBase64);
    }

    public String getValueInBase64() {
        return valueInBase64;
    }

    public void setValueInBase64(String valueInBase64) {
        this.valueInBase64 = valueInBase64;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }
}
