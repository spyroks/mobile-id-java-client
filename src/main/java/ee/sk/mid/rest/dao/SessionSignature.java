package ee.sk.mid.rest.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionSignature implements Serializable {

    private String algorithm;
    private String value;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SessionSignature{" +
                "algorithm='" + algorithm + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
