package ee.sk.mid;

import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CertificateRequestBuilder extends MobileIdRequestBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CertificateRequestBuilder.class);

    public CertificateRequestBuilder(MobileIdConnector connector) {
        super(connector);
        logger.debug("Instantiating certificate request builder");
    }

    public CertificateRequestBuilder withRelyingPartyUUID(String relyingPartyUUID) {
        super.withRelyingPartyUUID(relyingPartyUUID);
        return this;
    }

    public CertificateRequestBuilder withRelyingPartyName(String relyingPartyName) {
        super.withRelyingPartyName(relyingPartyName);
        return this;
    }

    public CertificateRequestBuilder withPhoneNumber(String phoneNumber) {
        super.withPhoneNumber(phoneNumber);
        return this;
    }

    public CertificateRequestBuilder withNationalIdentityNumber(String nationalIdentityNumber) {
        super.withNationalIdentityNumber(nationalIdentityNumber);
        return this;
    }

    public CertificateRequest build() throws MobileIdException {
        validateParameters();
        return createCertificateRequest();
    }

    private CertificateRequest createCertificateRequest() {
        CertificateRequest request = new CertificateRequest();
        request.setRelyingPartyUUID(getRelyingPartyUUID());
        request.setRelyingPartyName(getRelyingPartyName());
        request.setPhoneNumber(getPhoneNumber());
        request.setNationalIdentityNumber(getNationalIdentityNumber());
        return request;
    }

    protected void validateParameters() {
        super.validateParameters();
    }
}
