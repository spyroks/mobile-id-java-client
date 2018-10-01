package ee.sk.mid.mock;

import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

import static ee.sk.mid.mock.SessionStatusResultDummy.CERTIFICATE;
import static ee.sk.mid.mock.SessionStatusResultDummy.createSessionResult;
import static ee.sk.mid.mock.TestData.SESSION_ID;

public class MobileIdRestServiceResponseDummy {

    public static CertificateChoiceResponse createDummyCertificateChoiceResponse() {
        CertificateChoiceResponse certificateChoiceResponse = new CertificateChoiceResponse();
        certificateChoiceResponse.setResult("OK");
        certificateChoiceResponse.setCertificate(CERTIFICATE);
        return certificateChoiceResponse;
    }

    public static SignatureResponse createDummySignatureResponse() {
        return new SignatureResponse(SESSION_ID);
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
        return new AuthenticationResponse(SESSION_ID);
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
