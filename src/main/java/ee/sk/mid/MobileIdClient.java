package ee.sk.mid;

import ee.sk.mid.exception.*;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.SessionStatusPoller;
import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class MobileIdClient {

    private static final Logger logger = LoggerFactory.getLogger(MobileIdClient.class);

    private String relyingPartyUUID;
    private String relyingPartyName;
    private String hostUrl;
    private ClientConfig networkConnectionConfig;
    private TimeUnit pollingSleepTimeUnit = TimeUnit.SECONDS;
    private long pollingSleepTimeout = 1L;
    private MobileIdConnector connector;
    private SessionStatusPoller sessionStatusPoller;
    private MobileIdRequestBuilder builder;

    private MobileIdClient() {
    }

    public void setRelyingPartyUUID(String relyingPartyUUID) {
        this.relyingPartyUUID = relyingPartyUUID;
    }

    public void setRelyingPartyName(String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
    }

    public void setNetworkConnectionConfig(ClientConfig networkConnectionConfig) {
        this.networkConnectionConfig = networkConnectionConfig;
    }

    public void setPollingSleepTimeout(TimeUnit unit, long timeout) {
        pollingSleepTimeUnit = unit;
        pollingSleepTimeout = timeout;
    }

    public MobileIdConnector getMobileIdConnector() {
        if (null == connector) {
            setMobileIdConnector(new MobileIdRestConnector(hostUrl, networkConnectionConfig));
        }
        return connector;
    }

    public void setMobileIdConnector(MobileIdConnector mobileIdConnector) {
        this.connector = mobileIdConnector;
    }

    public SessionStatusPoller getSessionStatusPoller() {
        return sessionStatusPoller;
    }

    public CertificateRequestBuilder createCertificateRequestBuilder() {
        builder = new CertificateRequestBuilder(getMobileIdConnector());
        populateBuilderFields(builder);
        return (CertificateRequestBuilder) builder;
    }

    public SignatureRequestBuilder createSignatureRequestBuilder() {
        sessionStatusPoller = createSessionStatusPoller(getMobileIdConnector());
        builder = new SignatureRequestBuilder(getMobileIdConnector(), sessionStatusPoller);
        populateBuilderFields(builder);
        return (SignatureRequestBuilder) builder;
    }

    public AuthenticationRequestBuilder createAuthenticationRequestBuilder() {
        sessionStatusPoller = createSessionStatusPoller(getMobileIdConnector());
        builder = new AuthenticationRequestBuilder(getMobileIdConnector(), sessionStatusPoller);
        populateBuilderFields(builder);
        return (AuthenticationRequestBuilder) builder;
    }

    private SessionStatusPoller createSessionStatusPoller(MobileIdConnector connector) {
        SessionStatusPoller sessionStatusPoller = new SessionStatusPoller(connector);
        sessionStatusPoller.setPollingSleepTime(pollingSleepTimeUnit, pollingSleepTimeout);
        return sessionStatusPoller;
    }

    private void populateBuilderFields(MobileIdRequestBuilder builder) {
        builder.withRelyingPartyUUID(relyingPartyUUID);
        builder.withRelyingPartyName(relyingPartyName);
    }

    public X509Certificate createMobileIdCertificate(CertificateChoiceResponse certificateChoiceResponse) {
        validateCertificateResult(certificateChoiceResponse.getResult());
        validateCertificateResponse(certificateChoiceResponse);
        return CertificateParser.parseX509Certificate(certificateChoiceResponse.getCert());
    }

    public MobileIdSignature createMobileIdSignature(SessionStatus sessionStatus) {
        validateResponse(sessionStatus);
        SessionSignature sessionSignature = sessionStatus.getSignature();

        MobileIdSignature signature = new MobileIdSignature();
        signature.setValueInBase64(sessionSignature.getValue());
        signature.setAlgorithmName(sessionSignature.getAlgorithm());
        return signature;
    }

    public MobileIdAuthentication createMobileIdAuthentication(SessionStatus sessionStatus) {
        validateResponse(sessionStatus);
        String sessionResult = sessionStatus.getResult();
        SessionSignature sessionSignature = sessionStatus.getSignature();
        X509Certificate certificate = CertificateParser.parseX509Certificate(sessionStatus.getCert());

        MobileIdAuthentication authentication = new MobileIdAuthentication();
        authentication.setResult(sessionResult);
        authentication.setSignatureValueInBase64(sessionSignature.getValue());
        authentication.setAlgorithmName(sessionSignature.getAlgorithm());
        authentication.setCertificate(certificate);
        authentication.setSignedHashInBase64(builder.getHashInBase64());
        authentication.setHashType(builder.getHashType());
        return authentication;
    }

    private void validateCertificateResult(String result) throws MobileIdException {
        if (equalsIgnoreCase(result, "NOT_FOUND")) {
            logger.debug("No certificate for the user was found");
            throw new CertificateNotPresentException("No certificate for the user was found");
        } else if (equalsIgnoreCase(result, "NOT_ACTIVE")) {
            logger.debug("Inactive certificate found");
            throw new ExpiredException("Inactive certificate found");
        } else if (!equalsIgnoreCase(result, "OK")) {
            logger.warn("Session status end result is '" + result + "'");
            throw new TechnicalErrorException("Session status end result is '" + result + "'");
        }
    }

    private void validateCertificateResponse(CertificateChoiceResponse certificateChoiceResponse) throws TechnicalErrorException {
        if (certificateChoiceResponse.getCert() == null || isBlank(certificateChoiceResponse.getCert())) {
            logger.error("Certificate was not present in the session status response");
            throw new TechnicalErrorException("Certificate was not present in the session status response");
        }
    }

    private void validateResponse(SessionStatus sessionStatus) throws TechnicalErrorException {
        if (sessionStatus.getSignature() == null || isBlank(sessionStatus.getSignature().getValue())) {
            logger.error("Signature was not present in the response");
            throw new TechnicalErrorException("Signature was not present in the response");
        }
    }

    public static MobileIdClientBuilder createMobileIdClientBuilder() {
        return new MobileIdClient().new MobileIdClientBuilder();
    }

    public class MobileIdClientBuilder {

        private MobileIdClientBuilder() {}

        public MobileIdClientBuilder withRelyingPartyUUID(String relyingPartyUUID) {
            MobileIdClient.this.relyingPartyUUID = relyingPartyUUID;
            return this;
        }

        public MobileIdClientBuilder withRelyingPartyName(String relyingPartyName) {
            MobileIdClient.this.relyingPartyName = relyingPartyName;
            return this;
        }

        public MobileIdClientBuilder withHostUrl(String hostUrl) {
            MobileIdClient.this.hostUrl = hostUrl;
            return this;
        }

        public MobileIdClient build() {
            return MobileIdClient.this;
        }
    }
}
