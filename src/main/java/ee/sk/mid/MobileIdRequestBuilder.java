package ee.sk.mid;

import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.SessionStatusPoller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.isBlank;

public abstract class MobileIdRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MobileIdRequestBuilder.class);

    private MobileIdConnector connector;
    private SessionStatusPoller sessionStatusPoller;
    private String relyingPartyName;
    private String relyingPartyUUID;
    private String phoneNumber;
    private String nationalIdentityNumber;
    private SignableData dataToSign;
    private SignableHash hashToSign;
    private Language language;
    private String displayText;

    protected MobileIdRequestBuilder(MobileIdConnector connector, SessionStatusPoller sessionStatusPoller) {
        this.connector = connector;
        this.sessionStatusPoller = sessionStatusPoller;
    }

    protected MobileIdRequestBuilder(MobileIdConnector connector) {
        this.connector = connector;
    }

    protected MobileIdRequestBuilder withRelyingPartyUUID(String relyingPartyUUID) {
        this.relyingPartyUUID = relyingPartyUUID;
        return this;
    }

    protected MobileIdRequestBuilder withRelyingPartyName(String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
        return this;
    }

    protected MobileIdRequestBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    protected MobileIdRequestBuilder withNationalIdentityNumber(String nationalIdentityNumber) {
        this.nationalIdentityNumber = nationalIdentityNumber;
        return this;
    }

    protected MobileIdRequestBuilder withSignableData(SignableData dataToSign) {
        this.dataToSign = dataToSign;
        return this;
    }

    protected MobileIdRequestBuilder withSignableHash(SignableHash hashToSign) {
        this.hashToSign = hashToSign;
        return this;
    }

    protected MobileIdRequestBuilder withLanguage(Language language) {
        this.language = language;
        return this;
    }

    protected MobileIdRequestBuilder withDisplayText(String displayText) {
        this.displayText = displayText;
        return this;
    }

    MobileIdConnector getConnector() {
        return connector;
    }

    SessionStatusPoller getSessionStatusPoller() {
        return sessionStatusPoller;
    }

    String getRelyingPartyUUID() {
        return relyingPartyUUID;
    }

    String getRelyingPartyName() {
        return relyingPartyName;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    String getNationalIdentityNumber() {
        return nationalIdentityNumber;
    }

    HashType getHashType() {
        if (hashToSign != null) {
            return hashToSign.getHashType();
        }
        return dataToSign.getHashType();
    }

    String getHashInBase64() {
        if (hashToSign != null) {
            return hashToSign.getHashInBase64();
        }
        return dataToSign.calculateHashInBase64();
    }

    Language getLanguage() {
        return language;
    }

    String getDisplayText() {
        return displayText;
    }

    boolean isHashSet() {
        return hashToSign != null && hashToSign.areFieldsFilled();
    }

    boolean isSignableDataSet() {
        return dataToSign != null;
    }

    boolean isLanguageSet() {
        return language != null;
    }

    protected void validateParameters() {
        if (isBlank(relyingPartyUUID)) {
            logger.error("Relying Party UUID parameter must be set");
            throw new ParameterMissingException("Relying Party UUID parameter must be set");
        }
        if (isBlank(relyingPartyName)) {
            logger.error("Relying Party Name parameter must be set");
            throw new ParameterMissingException("Relying Party Name parameter must be set");
        }
        if (isBlank(phoneNumber) || isBlank(nationalIdentityNumber)) {
            logger.error("Phone number and national identity must be set");
            throw new ParameterMissingException("Phone number and national identity must be set");
        }
    }
}
