package ee.sk.mid;

import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.exception.TechnicalErrorException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.SessionStatusPoller;
import ee.sk.mid.rest.dao.SessionSignature;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.SignatureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class SignatureRequestBuilder extends MobileIdRequestBuilder {

    private static final String SIGNATURE_SESSION_PATH = "/mid-api/signature/session/{sessionId}";
    private static final Logger logger = LoggerFactory.getLogger(SignatureRequestBuilder.class);

    public SignatureRequestBuilder(MobileIdConnector connector, SessionStatusPoller sessionStatusPoller) {
        super(connector, sessionStatusPoller);
        logger.debug("Instantiating signature request builder");
    }

    public SignatureRequestBuilder withRelyingPartyUUID(String relyingPartyUUID) {
        super.withRelyingPartyUUID(relyingPartyUUID);
        return this;
    }

    public SignatureRequestBuilder withRelyingPartyName(String relyingPartyName) {
        super.withRelyingPartyName(relyingPartyName);
        return this;
    }

    public SignatureRequestBuilder withPhoneNumber(String phoneNumber) {
        super.withPhoneNumber(phoneNumber);
        return this;
    }

    public SignatureRequestBuilder withNationalIdentityNumber(String nationalIdentityNumber) {
        super.withNationalIdentityNumber(nationalIdentityNumber);
        return this;
    }

    public SignatureRequestBuilder withSignableData(SignableData dataToSign) {
        super.withSignableData(dataToSign);
        return this;
    }

    public SignatureRequestBuilder withSignableHash(SignableHash hashToSign) {
        super.withSignableHash(hashToSign);
        return this;
    }

    public SignatureRequestBuilder withLanguage(Language language) {
        super.withLanguage(language);
        return this;
    }

    public SignatureRequestBuilder withDisplayText(String displayText) {
        super.withDisplayText(displayText);
        return this;
    }

    public MobileIdSignature sign() throws MobileIdException {
        validateParameters();
        SignatureRequest request = createSignatureRequest();
        SignatureResponse response = getSignatureResponse(request);
        SessionStatus sessionStatus = getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionId(), SIGNATURE_SESSION_PATH);
        validateResponse(sessionStatus);
        return createMobileIdSignature(sessionStatus);
    }

    private SignatureRequest createSignatureRequest() {
        SignatureRequest request = new SignatureRequest();
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

    private SignatureResponse getSignatureResponse(SignatureRequest request) {
        return getConnector().sign(request);
    }

    private MobileIdSignature createMobileIdSignature(SessionStatus sessionStatus) {
        SessionSignature sessionSignature = sessionStatus.getSignature();
        MobileIdSignature signature = new MobileIdSignature();
        signature.setValueInBase64(sessionSignature.getValueInBase64());
        signature.setAlgorithmName(sessionSignature.getAlgorithm());
        return signature;
    }

    protected void validateParameters() throws ParameterMissingException {
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

    private void validateResponse(SessionStatus sessionStatus) throws TechnicalErrorException {
        if (sessionStatus.getSignature() == null || isBlank(sessionStatus.getSignature().getValueInBase64())) {
            logger.error("Signature was not present in the response");
            throw new TechnicalErrorException("Signature was not present in the response");
        }
    }
}
