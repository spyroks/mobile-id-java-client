package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.apache.commons.codec.binary.Base64;

public class MobileIdSignature {

    private String valueInBase64;
    private String algorithmName;

    public byte[] getValue() throws InvalidBase64CharacterException {
        if (!Base64.isBase64(valueInBase64)) {
            throw new InvalidBase64CharacterException("Failed to parse signature value in base64. Probably incorrectly encoded base64 string: '" + valueInBase64 + "'");
        }
        return Base64.decodeBase64(valueInBase64);
    }

    private MobileIdSignature(MobileIdSignatureBuilder builder) {
        this.valueInBase64 = builder.valueInBase64;
        this.algorithmName = builder.algorithmName;
    }

    public String getValueInBase64() {
        return valueInBase64;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public static MobileIdSignatureBuilder newBuilder() {
        return new MobileIdSignatureBuilder();
    }

    public static class MobileIdSignatureBuilder {
        private String valueInBase64;
        private String algorithmName;

        private MobileIdSignatureBuilder() {
        }

        public MobileIdSignatureBuilder withValueInBase64(String valueInBase64) {
            this.valueInBase64 = valueInBase64;
            return this;
        }

        public MobileIdSignatureBuilder withAlgorithmName(String algorithmName) {
            this.algorithmName = algorithmName;
            return this;
        }

        public MobileIdSignature build() {
            return new MobileIdSignature(this);
        }
    }

}
