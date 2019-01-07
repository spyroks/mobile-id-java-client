package ee.sk.mid.rest.dao.request;

import ee.sk.mid.Language;
import ee.sk.mid.MobileIdAuthenticationHash;
import ee.sk.mid.SignableData;
import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.ParameterMissingException;

public class AuthenticationRequestBuilder extends AbstractAuthSignRequestBuilder {

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
