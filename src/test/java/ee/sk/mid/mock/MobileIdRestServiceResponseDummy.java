package ee.sk.mid.mock;

import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

import static ee.sk.mid.mock.SessionStatusResultDummy.CERTIFICATE;
import static ee.sk.mid.mock.SessionStatusResultDummy.createSessionResult;

public class MobileIdRestServiceResponseDummy {

    public static SignatureResponse createDummySignatureResponse() {
        return new SignatureResponse("97f5058e-e308-4c83-ac14-7712b0eb9d86");
    }

    public static SessionStatus createDummySignatureSessionStatusResponse() {
        SessionStatus status = new SessionStatus();
        status.setState("COMPLETE");
        status.setResult(createSessionResult());
        SessionSignature signature = new SessionSignature();
        signature.setValueInBase64("luvjsi1+1iLN9yfDFEh/BE8h");
        signature.setAlgorithm("sha256WithRSAEncryption");
        status.setSignature(signature);
        return status;
    }

    public static AuthenticationResponse createDummyAuthenticationResponse() {
        return new AuthenticationResponse("97f5058e-e308-4c83-ac14-7712b0eb9d86");
    }

    public static SessionStatus createDummyAuthenticationSessionStatusResponse() {
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
