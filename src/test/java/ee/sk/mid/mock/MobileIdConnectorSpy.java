package ee.sk.mid.mock;

import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

public class MobileIdConnectorSpy implements MobileIdConnector {

    private SessionStatus sessionStatusToRespond;
    private AuthenticationResponse authenticationResponseToRespond;
    private SignatureResponse signatureResponseToRespond;

    private String sessionIdUsed;
    private AuthenticationRequest authenticationRequestUsed;
    private SignatureRequest signatureRequestUsed;

    public SessionStatus getSessionStatusToRespond() {
        return sessionStatusToRespond;
    }

    public void setSessionStatusToRespond(SessionStatus sessionStatusToRespond) {
        this.sessionStatusToRespond = sessionStatusToRespond;
    }

    public void setAuthenticationResponseToRespond(AuthenticationResponse authenticationResponseToRespond) {
        this.authenticationResponseToRespond = authenticationResponseToRespond;
    }

    public void setSignatureResponseToRespond(SignatureResponse signatureResponseToRespond) {
        this.signatureResponseToRespond = signatureResponseToRespond;
    }

    public String getSessionIdUsed() {
        return sessionIdUsed;
    }

    public AuthenticationRequest getAuthenticationRequestUsed() {
        return authenticationRequestUsed;
    }

    public SignatureRequest getSignatureRequestUsed() {
        return signatureRequestUsed;
    }

    @Override
    public SignatureResponse sign(SignatureRequest request) {
        signatureRequestUsed = request;
        return signatureResponseToRespond;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationRequestUsed = request;
        return authenticationResponseToRespond;
    }

    @Override
    public SessionStatus getSessionStatus(SessionStatusRequest request, String path) throws SessionNotFoundException {
        sessionIdUsed = request.getSessionId();
        return sessionStatusToRespond;
    }
}
