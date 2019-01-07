package ee.sk.mid.rest.dao.request;

import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.ParameterMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class CertificateRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CertificateRequestBuilder.class);

    private String relyingPartyName;
    private String relyingPartyUUID;
    private String phoneNumber;
    private String nationalIdentityNumber;

    public CertificateRequestBuilder withRelyingPartyUUID(String relyingPartyUUID) {
        this.relyingPartyUUID = relyingPartyUUID;
        return this;
    }

    public CertificateRequestBuilder withRelyingPartyName(String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
        return this;
    }

    public CertificateRequestBuilder withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public CertificateRequestBuilder withNationalIdentityNumber(String nationalIdentityNumber) {
        this.nationalIdentityNumber = nationalIdentityNumber;
        return this;
    }

    public CertificateRequest build() throws MobileIdException {
        validateParameters();

        CertificateRequest request = new CertificateRequest();
        request.setRelyingPartyUUID(relyingPartyUUID);
        request.setRelyingPartyName(relyingPartyName);
        request.setPhoneNumber(phoneNumber);
        request.setNationalIdentityNumber(nationalIdentityNumber);
        return request;
    }

    private void validateParameters() {
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
