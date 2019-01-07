package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;
import java.security.cert.X509Certificate;

public class MobileIdAuthentication implements Serializable {

    private String result;
    private String signedHashInBase64;
    private HashType hashType;
    private String signatureValueInBase64;
    private String algorithmName;
    private X509Certificate certificate;

    private MobileIdAuthentication(MobileIdAuthenticationBuilder builder) {
        this.result = builder.result;
        this.signedHashInBase64 = builder.signedHashInBase64;
        this.hashType = builder.hashType;
        this.signatureValueInBase64 = builder.signatureValueInBase64;
        this.algorithmName = builder.algorithmName;
        this.certificate = builder.certificate;
    }

    public byte[] getSignatureValue() throws InvalidBase64CharacterException {
        if (!Base64.isBase64(signatureValueInBase64)) {
            throw new InvalidBase64CharacterException("Failed to parse signature value in base64. Probably incorrectly encoded base64 string: '" + signatureValueInBase64 + "'");
        }
        return Base64.decodeBase64(signatureValueInBase64);
    }

    public String getResult() {
        return result;
    }

    public String getSignatureValueInBase64() {
        return signatureValueInBase64;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public String getSignedHashInBase64() {
        return signedHashInBase64;
    }

    public HashType getHashType() {
        return hashType;
    }

    public static MobileIdAuthenticationBuilder newBuilder() {
        return new MobileIdAuthenticationBuilder();
    }

    public static class MobileIdAuthenticationBuilder {
        private String result;
        private String signedHashInBase64;
        private HashType hashType;
        private String signatureValueInBase64;
        private String algorithmName;
        private X509Certificate certificate;

        private MobileIdAuthenticationBuilder() {
        }

        public MobileIdAuthenticationBuilder withResult(String result) {
            this.result = result;
            return this;
        }

        public MobileIdAuthenticationBuilder withSignedHashInBase64(String signedHashInBase64) {
            this.signedHashInBase64 = signedHashInBase64;
            return this;
        }

        public MobileIdAuthenticationBuilder withHashType(HashType hashType) {
            this.hashType = hashType;
            return this;
        }

        public MobileIdAuthenticationBuilder withSignatureValueInBase64(String signatureValueInBase64) {
            this.signatureValueInBase64 = signatureValueInBase64;
            return this;
        }

        public MobileIdAuthenticationBuilder withAlgorithmName(String algorithmName) {
            this.algorithmName = algorithmName;
            return this;
        }

        public MobileIdAuthenticationBuilder withCertificate(X509Certificate certificate) {
            this.certificate = certificate;
            return this;
        }

        public MobileIdAuthentication build() {
            return new MobileIdAuthentication(this);
        }
    }

}
