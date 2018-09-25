package ee.sk.mid.test;

import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;

import static ee.sk.mid.test.DummyData.CERTIFICATE;
import static ee.sk.mid.test.DummyData.createSessionResult;

public class TestUtils {

    public static AuthenticationResponse createDummyAuthenticationSessionResponse() {
        return new AuthenticationResponse("97f5058e-e308-4c83-ac14-7712b0eb9d86");
    }

    public static SessionStatus createDummySessionStatusResponse() {
        SessionSignature signature = new SessionSignature();
        signature.setValueInBase64("c2FtcGxlIHNpZ25hdHVyZQ0K");
        signature.setAlgorithm("sha512WithRSAEncryption");
        SessionStatus status = new SessionStatus();
        status.setState("COMPLETE");
        status.setResult(createSessionResult());
        status.setSignature(signature);
        status.setCertificate(CERTIFICATE);
        return status;
    }
}