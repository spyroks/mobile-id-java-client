package ee.sk.mid.rest.dao.request;

import ee.sk.mid.Language;
import ee.sk.mid.SignableData;
import ee.sk.mid.SignableHash;
import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.ParameterMissingException;

public class SignatureRequestBuilder extends AbstractAuthSignRequestBuilder {

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

    public SignatureRequest build() throws MobileIdException {
        validateParameters();
        return createSignatureRequest();
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

    protected void validateParameters() throws ParameterMissingException {
        super.validateParameters();
        super.validateExtraParameters();
    }
}
