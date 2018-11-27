package ee.sk.mid;

import ee.sk.mid.exception.CertificateNotPresentException;
import ee.sk.mid.exception.ExpiredException;
import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.TechnicalErrorException;
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
    private MobileIdRestConnector connector;
    private SessionStatusPoller sessionStatusPoller;
    private MobileIdRequestBuilder builder;

    public void setRelyingPartyUUID(String relyingPartyUUID) {
        this.relyingPartyUUID = relyingPartyUUID;
    }

    public void setRelyingPartyName(String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public void setNetworkConnectionConfig(ClientConfig networkConnectionConfig) {
        this.networkConnectionConfig = networkConnectionConfig;
    }

    public void setPollingSleepTimeout(TimeUnit unit, long timeout) {
        pollingSleepTimeUnit = unit;
        pollingSleepTimeout = timeout;
    }

    public MobileIdRestConnector getConnector() {
        return connector;
    }

    public SessionStatusPoller getSessionStatusPoller() {
        return sessionStatusPoller;
    }

    public CertificateRequestBuilder createCertificateRequestBuilder() {
        connector = new MobileIdRestConnector(hostUrl, networkConnectionConfig);
        builder = new CertificateRequestBuilder(connector);
        populateBuilderFields(builder);
        return (CertificateRequestBuilder) builder;
    }

    public SignatureRequestBuilder createSignatureRequestBuilder() {
        connector = new MobileIdRestConnector(hostUrl, networkConnectionConfig);
        sessionStatusPoller = createSessionStatusPoller(connector);
        builder = new SignatureRequestBuilder(connector, sessionStatusPoller);
        populateBuilderFields(builder);
        return (SignatureRequestBuilder) builder;
    }

    public AuthenticationRequestBuilder createAuthenticationRequestBuilder() {
        connector = new MobileIdRestConnector(hostUrl, networkConnectionConfig);
        sessionStatusPoller = createSessionStatusPoller(connector);
        builder = new AuthenticationRequestBuilder(connector, sessionStatusPoller);
        populateBuilderFields(builder);
        return (AuthenticationRequestBuilder) builder;
    }

    private SessionStatusPoller createSessionStatusPoller(MobileIdRestConnector connector) {
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
        return CertificateParser.parseX509Certificate(certificateChoiceResponse.getCertificate());
    }

    public MobileIdSignature createMobileIdSignature(SessionStatus sessionStatus) {
        validateResponse(sessionStatus);
        SessionSignature sessionSignature = sessionStatus.getSignature();

        MobileIdSignature signature = new MobileIdSignature();
        signature.setValueInBase64(sessionSignature.getValueInBase64());
        signature.setAlgorithmName(sessionSignature.getAlgorithm());
        return signature;
    }

    public MobileIdAuthentication createMobileIdAuthentication(SessionStatus sessionStatus) {
        validateResponse(sessionStatus);
        String sessionResult = sessionStatus.getResult();
        SessionSignature sessionSignature = sessionStatus.getSignature();
        X509Certificate certificate = CertificateParser.parseX509Certificate(sessionStatus.getCertificate());

        MobileIdAuthentication authentication = new MobileIdAuthentication();
        authentication.setResult(sessionResult);
        authentication.setSignatureValueInBase64(sessionSignature.getValueInBase64());
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
        if (certificateChoiceResponse.getCertificate() == null || isBlank(certificateChoiceResponse.getCertificate())) {
            logger.error("Certificate was not present in the session status response");
            throw new TechnicalErrorException("Certificate was not present in the session status response");
        }
    }

    private void validateResponse(SessionStatus sessionStatus) throws TechnicalErrorException {
        if (sessionStatus.getSignature() == null || isBlank(sessionStatus.getSignature().getValueInBase64())) {
            logger.error("Signature was not present in the response");
            throw new TechnicalErrorException("Signature was not present in the response");
        }
    }
}
