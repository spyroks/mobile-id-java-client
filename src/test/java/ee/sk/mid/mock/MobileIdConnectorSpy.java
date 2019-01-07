package ee.sk.mid.mock;

import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.SessionStatusPoller;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

public class MobileIdConnectorSpy implements MobileIdConnector {

    private SessionStatus sessionStatusToRespond;
    private CertificateChoiceResponse certificateChoiceResponseToRespond;
    private AuthenticationResponse authenticationResponseToRespond;
    private SignatureResponse signatureResponseToRespond;

    private String sessionIdUsed;
    private CertificateRequest certificateRequestUsed;
    private AuthenticationRequest authenticationRequestUsed;
    private SignatureRequest signatureRequestUsed;

    public SessionStatus getSessionStatusToRespond() {
        return sessionStatusToRespond;
    }

    public void setSessionStatusToRespond(SessionStatus sessionStatusToRespond) {
        this.sessionStatusToRespond = sessionStatusToRespond;
    }

    public CertificateChoiceResponse getCertificateChoiceResponseToRespond() {
        return certificateChoiceResponseToRespond;
    }

    public void setCertificateChoiceResponseToRespond(CertificateChoiceResponse certificateChoiceResponseToRespond) {
        this.certificateChoiceResponseToRespond = certificateChoiceResponseToRespond;
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

    public CertificateRequest getCertificateRequestUsed() {
        return certificateRequestUsed;
    }

    public AuthenticationRequest getAuthenticationRequestUsed() {
        return authenticationRequestUsed;
    }

    public SignatureRequest getSignatureRequestUsed() {
        return signatureRequestUsed;
    }

    @Override
    public CertificateChoiceResponse getCertificate(CertificateRequest request) {
        certificateRequestUsed = request;
        return certificateChoiceResponseToRespond;
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
    public SessionStatus getAuthenticationSessionStatus(SessionStatusRequest request) throws SessionNotFoundException {
        return getSessionStatus(request, SessionStatusPoller.AUTHENTICATION_SESSION_PATH);
    }

    @Override
    public SessionStatus getSignatureSessionStatus(SessionStatusRequest request) {
        return getSessionStatus(request, SessionStatusPoller.SIGNATURE_SESSION_PATH);
    }

    @Override
    public SessionStatus getSessionStatus(SessionStatusRequest request, String path) throws SessionNotFoundException {
        sessionIdUsed = request.getSessionID();
        return sessionStatusToRespond;
    }
}
