package ee.sk.mid.mock;

import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;

import static ee.sk.mid.mock.TestData.AUTH_CERTIFICATE_EE;
import static ee.sk.mid.mock.TestData.SESSION_ID;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MobileIdRestServiceResponseDummy {


    public static void assertSignaturePolled(SessionStatus sessionStatus) {
        assertSessionStatusPolled(sessionStatus);
    }

    public static void assertAuthenticationPolled(SessionStatus sessionStatus) {
        assertSessionStatusPolled(sessionStatus);
        assertThat(sessionStatus.getCert(), not(isEmptyOrNullString()));
    }

    private static void assertSessionStatusPolled(SessionStatus sessionStatus) {
        assertThat(sessionStatus, is(notNullValue()));
        assertThat(sessionStatus.getState(), not(isEmptyOrNullString()));
        assertThat(sessionStatus.getResult(), not(isEmptyOrNullString()));
        assertThat(sessionStatus.getSignature(), is(notNullValue()));
        assertThat(sessionStatus.getSignature().getValue(), not(isEmptyOrNullString()));
    }
}
