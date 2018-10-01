package ee.sk.mid;

import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.TechnicalErrorException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.SessionStatusPoller;
import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AuthenticationRequestBuilder extends MobileIdRequestBuilder {

    private static final String AUTHENTICATION_SESSION_PATH = "/mid-api/authentication/session/{sessionId}";
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationRequestBuilder.class);

    public AuthenticationRequestBuilder(MobileIdConnector connector, SessionStatusPoller sessionStatusPoller) {
        super(connector, sessionStatusPoller);
        logger.debug("Instantiating authentication request builder");
    }

    public AuthenticationRequestBuilder withRelyingPartyUUID(String relyingPartyUUID) {
        super.withRelyingPartyUUID(relyingPartyUUID);
        return this;
    }

    public AuthenticationRequestBuilder withRelyingPartyName(String relyingPartyName) {
        super.withRelyingPartyName(relyingPartyName);
        return this;
    }

    public AuthenticationRequestBuilder withPhoneNumber(String phoneNumber) {
        super.withPhoneNumber(phoneNumber);
        return this;
    }

    public AuthenticationRequestBuilder withNationalIdentityNumber(String nationalIdentityNumber) {
        super.withNationalIdentityNumber(nationalIdentityNumber);
        return this;
    }

    public AuthenticationRequestBuilder withSignableData(SignableData dataToSign) {
        super.withSignableData(dataToSign);
        return this;
    }

    public AuthenticationRequestBuilder withAuthenticationHash(AuthenticationHash authenticationHash) {
        super.withSignableHash(authenticationHash);
        return this;
    }

    public AuthenticationRequestBuilder withLanguage(Language language) {
        super.withLanguage(language);
        return this;
    }

    public AuthenticationRequestBuilder withDisplayText(String displayText) {
        super.withDisplayText(displayText);
        return this;
    }

    public MobileIdAuthentication authenticate() throws MobileIdException {
        validateParameters();
        AuthenticationRequest request = createAuthenticationSessionRequest();
        AuthenticationResponse response = getAuthenticationResponse(request);
        SessionStatus sessionStatus = getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionId(), AUTHENTICATION_SESSION_PATH);
        validateResponse(sessionStatus);
        return createMobileIdAuthentication(sessionStatus);
    }

    private AuthenticationRequest createAuthenticationSessionRequest() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setRelyingPartyUUID(getRelyingPartyUUID());
        request.setRelyingPartyName(getRelyingPartyName());
        request.setPhoneNumber(getPhoneNumber());
        request.setNationalIdentityNumber(getNationalIdentityNumber());
        request.setHash(getHashInBase64());
        request.setHashType(getHashType());
        request.setLanguage(getLanguage());
        request.setDisplayText(getDisplayText());
        return request;
    }

    private AuthenticationResponse getAuthenticationResponse(AuthenticationRequest request) {
        return getConnector().authenticate(request);
    }

    private MobileIdAuthentication createMobileIdAuthentication(SessionStatus sessionStatus) {
        String sessionResult = sessionStatus.getResult();
        SessionSignature sessionSignature = sessionStatus.getSignature();
        String certificate = sessionStatus.getCertificate();
        MobileIdAuthentication authentication = new MobileIdAuthentication();
        authentication.setResult(sessionResult);
        authentication.setSignatureValueInBase64(sessionSignature.getValueInBase64());
        authentication.setAlgorithmName(sessionSignature.getAlgorithm());
        authentication.setCertificate(CertificateParser.parseX509Certificate(certificate));
        authentication.setSignedHashInBase64(getHashInBase64());
        authentication.setHashType(getHashType());
        return authentication;
    }

    protected void validateParameters() {
        super.validateParameters();
        if (isHashSet() && isSignableDataSet()) {
            logger.error("Signable data or hash with hash type must be set");
            throw new ParameterMissingException("Signable data or hash with hash type must be set");
        }
        if (isLanguageSet()) {
            logger.error("Language for user dialog in mobile phone must be set");
            throw new ParameterMissingException("Language for user dialog in mobile phone must be set");
        }
    }

    private void validateResponse(SessionStatus sessionStatus) {
        if (sessionStatus.getSignature() == null || isBlank(sessionStatus.getSignature().getValueInBase64())) {
            logger.error("Signature was not present in the response");
            throw new TechnicalErrorException("Signature was not present in the response");
        }
        if (sessionStatus.getCertificate() == null || isBlank(sessionStatus.getCertificate())) {
            logger.error("Certificate was not present in the response");
            throw new TechnicalErrorException("Certificate was not present in the response");
        }
    }
}
