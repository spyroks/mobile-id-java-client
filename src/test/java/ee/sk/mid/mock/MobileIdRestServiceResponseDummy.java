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
        SessionStatus status = new SessionStatus();
        status.setState("COMPLETE");
        status.setResult("OK");
        SessionSignature signature = new SessionSignature();
        signature.setValueInBase64("luvjsi1+1iLN9yfDFEh/BE8h");
        signature.setAlgorithm("sha256WithRSAEncryption");
        status.setSignature(signature);
        return status;
    }

    public static AuthenticationResponse createDummyAuthenticationResponse() {
        return new AuthenticationResponse(SESSION_ID);
    }

    public static SessionStatus createDummyAuthenticationSessionStatus() {
        SessionSignature signature = new SessionSignature();
        signature.setValueInBase64("c2FtcGxlIHNpZ25hdHVyZQ0K");
        signature.setAlgorithm("sha512WithRSAEncryption");
        SessionStatus status = new SessionStatus();
        status.setState("COMPLETE");
        status.setResult("OK");
        status.setSignature(signature);
        status.setCertificate(CERTIFICATE);
        return status;
    }

    public static void assertCertificateChosen(CertificateChoiceResponse response) {
        assertThat(response, is(notNullValue()));
        assertThat(response.getResult(), is("OK"));
        assertThat(response.getCertificate(), not(isEmptyOrNullString()));
    }

    public static void assertSignaturePolled(SessionStatus status) {
        assertThat(status, is(notNullValue()));
        assertThat(status.getResult(), not(isEmptyOrNullString()));
        assertThat(status.getSignature(), is(notNullValue()));
        assertThat(status.getSignature().getValueInBase64(), not(isEmptyOrNullString()));
    }

    public static void assertAuthenticationPolled(SessionStatus status) {
        assertThat(status, is(notNullValue()));
        assertThat(status.getResult(), not(isEmptyOrNullString()));
        assertThat(status.getSignature().getValueInBase64(), not(isEmptyOrNullString()));
        assertThat(status.getCertificate(), not(isEmptyOrNullString()));
    }
}
