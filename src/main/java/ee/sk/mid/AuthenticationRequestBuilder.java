package ee.sk.mid;

import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.ParameterMissingException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.SessionStatusPoller;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationRequestBuilder extends MobileIdRequestBuilder {

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

    public AuthenticationRequestBuilder withAuthenticationHash(MobileIdAuthenticationHash mobileIdAuthenticationHash) {
        super.withSignableHash(mobileIdAuthenticationHash);
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

    public AuthenticationRequest build() throws MobileIdException {
        validateParameters();
        return createAuthenticationRequest();
    }

    private AuthenticationRequest createAuthenticationRequest() {
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

    protected void validateParameters() throws ParameterMissingException {
        super.validateParameters();
        super.validateExtraParameters();
    }
}
