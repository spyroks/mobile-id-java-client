package ee.sk.mid.rest;

import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;

import java.util.ArrayList;
import java.util.List;

public class MobileIdConnectorStub implements MobileIdConnector {

    String sessionIdUsed;
    SessionStatusRequest requestUsed;
    List<SessionStatus> responses = new ArrayList<>();
    int responseNumber = 0;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return null;
    }

    @Override
    public SessionStatus getSessionStatus(SessionStatusRequest request, String path) throws SessionNotFoundException {
        sessionIdUsed = request.getSessionId();
        requestUsed = request;
        return responses.get(responseNumber++);
    }
}
