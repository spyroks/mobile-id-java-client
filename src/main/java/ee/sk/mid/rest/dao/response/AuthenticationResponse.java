package ee.sk.mid.rest.dao.response;

public class AuthenticationResponse extends AbstractResponse {

    public AuthenticationResponse(String sessionId) {
        super(sessionId);
    }

    public AuthenticationResponse() {
    }
}