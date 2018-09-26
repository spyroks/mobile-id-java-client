package ee.sk.mid.rest;

import ee.sk.mid.exception.SessionNotFoundException;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

public interface MobileIdConnector {

    SignatureResponse sign(SignatureRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    SessionStatus getSessionStatus(SessionStatusRequest request, String path) throws SessionNotFoundException;
}
