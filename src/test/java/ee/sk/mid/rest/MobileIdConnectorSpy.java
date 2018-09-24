package ee.sk.mid.rest;

import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;

public class MobileIdConnectorSpy implements MobileIdConnector {

    public SessionStatus sessionStatusToRespond;
    public AuthenticationResponse authenticationSessionResponseToRespond;

    public String sessionIdUsed;
    public String pathUsed;
    public AuthenticationRequest authenticationSessionRequestUsed;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationSessionRequestUsed = request;
        return authenticationSessionResponseToRespond;
    }

    @Override
    public SessionStatus getSessionStatus(SessionStatusRequest request, String path) throws SessionNotFoundException {
        sessionIdUsed = request.getSessionId();
        pathUsed = path;
        return sessionStatusToRespond;
    }
}
