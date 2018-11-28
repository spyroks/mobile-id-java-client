package ee.sk.mid.rest.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionStatus implements Serializable {

    private String state;
    private String result;
    private SessionSignature signature;
    private String cert;

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

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    @Override
    public String toString() {
        return "SessionStatus{" +
                "state='" + state + '\'' +
                ", result='" + result + '\'' +
                ", signature='" + signature + '\'' +
                ", cert='" + cert + '\'' +
                '}';
    }
}
