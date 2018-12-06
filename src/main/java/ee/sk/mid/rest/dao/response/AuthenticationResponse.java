package ee.sk.mid.rest.dao.response;

public class AuthenticationResponse extends AbstractResponse {

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String sessionID) {
        super(sessionID);
    }
}
