package ee.sk.mid.rest.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionSignature implements Serializable {

    private String algorithm;

    @JsonProperty("value")
    private String valueInBase64;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getValueInBase64() {
        return valueInBase64;
    }

    public void setValueInBase64(String valueInBase64) {
        this.valueInBase64 = valueInBase64;
    }

    @Override
    public String toString() {
        return "SessionSignature{" +
                "algorithm='" + algorithm + '\'' +
                ", valueInBase64='" + valueInBase64 + '\'' +
                '}';
    }
}
