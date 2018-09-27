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

    public byte[] getSignatureValue() {
        if (!Base64.isBase64(signatureValueInBase64)) {
            throw new InvalidBase64CharacterException("Failed to parse signature value in base64. Probably incorrectly encoded base64 string: '" + signatureValueInBase64);
        }
        return Base64.decodeBase64(signatureValueInBase64);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSignatureValueInBase64() {
        return signatureValueInBase64;
    }

    public void setSignatureValueInBase64(String signatureValueInBase64) {
        this.signatureValueInBase64 = signatureValueInBase64;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public String getSignedHashInBase64() {
        return signedHashInBase64;
    }

    public void setSignedHashInBase64(String signedHashInBase64) {
        this.signedHashInBase64 = signedHashInBase64;
    }

    public HashType getHashType() {
        return hashType;
    }

    public void setHashType(HashType hashType) {
        this.hashType = hashType;
    }
}
