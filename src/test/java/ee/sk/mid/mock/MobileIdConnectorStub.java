package ee.sk.mid.mock;

import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

import java.util.ArrayList;
import java.util.List;

public class MobileIdConnectorStub implements MobileIdConnector {

    private String sessionIdUsed;
    private SessionStatusRequest requestUsed;
    private List<SessionStatus> responses = new ArrayList<>();
    private int responseNumber = 0;

    public String getSessionIdUsed() {
        return sessionIdUsed;
    }

    public SessionStatusRequest getRequestUsed() {
        return requestUsed;
    }

    public List<SessionStatus> getResponses() {
        return responses;
    }

    public int getResponseNumber() {
        return responseNumber;
    }

    @Override
    public CertificateChoiceResponse getCertificate(CertificateRequest request) {
        return null;
    }

    @Override
    public SignatureResponse sign(SignatureRequest request) {
        return null;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return null;
    }

    @Override
    public SessionStatus getSessionStatus(SessionStatusRequest request, String path) throws SessionNotFoundException {
        sessionIdUsed = request.getSessionID();
        requestUsed = request;
        return responses.get(responseNumber++);
    }
}
