package ee.sk.mid.mock;

import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

import static ee.sk.mid.mock.TestData.CERTIFICATE;
import static ee.sk.mid.mock.TestData.SESSION_ID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

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

    public static SessionStatus createDummySignatureSessionStatus() {
        SessionStatus sessionStatus = new SessionStatus();
        sessionStatus.setState("COMPLETE");
        sessionStatus.setResult("OK");
        SessionSignature signature = new SessionSignature();
        signature.setValueInBase64("luvjsi1+1iLN9yfDFEh/BE8h");
        signature.setAlgorithm("sha256WithRSAEncryption");
        sessionStatus.setSignature(signature);
        return sessionStatus;
    }

    public static AuthenticationResponse createDummyAuthenticationResponse() {
        return new AuthenticationResponse(SESSION_ID);
    }

    public static SessionStatus createDummyAuthenticationSessionStatus() {
        SessionSignature signature = new SessionSignature();
        signature.setValueInBase64("c2FtcGxlIHNpZ25hdHVyZQ0K");
        signature.setAlgorithm("sha512WithRSAEncryption");
        SessionStatus sessionStatus = new SessionStatus();
        sessionStatus.setState("COMPLETE");
        sessionStatus.setResult("OK");
        sessionStatus.setSignature(signature);
        sessionStatus.setCertificate(CERTIFICATE);
        return sessionStatus;
    }

    public static void assertCertificateChosen(CertificateChoiceResponse response) {
        assertThat(response, is(notNullValue()));
        assertThat(response.getResult(), is("OK"));
        assertThat(response.getCertificate(), not(isEmptyOrNullString()));
    }

    public static void assertSignaturePolled(SessionStatus sessionStatus) {
        System.out.println(sessionStatus);
        assertSessionStatusPolled(sessionStatus);
    }

    public static void assertAuthenticationPolled(SessionStatus sessionStatus) {
        assertSessionStatusPolled(sessionStatus);

        assertThat(sessionStatus.getCertificate(), not(isEmptyOrNullString()));
    }

    private static void assertSessionStatusPolled(SessionStatus sessionStatus) {
        assertThat(sessionStatus, is(notNullValue()));
        assertThat(sessionStatus.getState(), not(isEmptyOrNullString()));
        assertThat(sessionStatus.getResult(), not(isEmptyOrNullString()));
        assertThat(sessionStatus.getSignature(), is(notNullValue()));
        assertThat(sessionStatus.getSignature().getValueInBase64(), not(isEmptyOrNullString()));
    }
}
