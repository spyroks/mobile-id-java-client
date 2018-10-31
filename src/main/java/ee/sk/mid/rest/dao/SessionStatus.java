package ee.sk.mid.rest.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionStatus implements Serializable {

    private String state;
    private String result;
    private SessionSignature signature;

    @JsonProperty("cert")
    private String certificate;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public SessionSignature getSignature() {
        return signature;
    }

    public void setSignature(SessionSignature signature) {
        this.signature = signature;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @Override
    public String toString() {
        return "SessionStatus{" +
                "state='" + state + '\'' +
                ", result='" + result + '\'' +
                ", signature='" + signature + '\'' +
                ", certificate='" + certificate + '\'' +
                '}';
    }
}
