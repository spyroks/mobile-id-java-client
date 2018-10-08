package ee.sk.mid;

import ee.sk.mid.exception.ExpiredException;
import ee.sk.mid.exception.MobileIdException;
import ee.sk.mid.exception.CertificateNotPresentException;
import ee.sk.mid.exception.TechnicalErrorException;
import ee.sk.mid.rest.MobileIdConnector;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

    public X509Certificate fetch() throws MobileIdException {
        logger.debug("Starting to fetch certificate");
        validateParameters();
        CertificateRequest request = createCertificateRequest();
        CertificateChoiceResponse certificateChoiceResponse = fetchCertificateChoiceSessionResponse(request);
        return createMobileIdCertificate(certificateChoiceResponse);
    }

    private CertificateRequest createCertificateRequest() {
        CertificateRequest request = new CertificateRequest();
        request.setRelyingPartyUUID(getRelyingPartyUUID());
        request.setRelyingPartyName(getRelyingPartyName());
        request.setPhoneNumber(getPhoneNumber());
        request.setNationalIdentityNumber(getNationalIdentityNumber());
        return request;
    }

    private CertificateChoiceResponse fetchCertificateChoiceSessionResponse(CertificateRequest request) {
        return getConnector().getCertificate(request);
    }

    private X509Certificate createMobileIdCertificate(CertificateChoiceResponse certificateChoiceResponse) {
        validateResult(certificateChoiceResponse.getResult());
        validateResponse(certificateChoiceResponse);
        return CertificateParser.parseX509Certificate(certificateChoiceResponse.getCertificate());
    }

    protected void validateParameters() {
        super.validateParameters();
    }

    private void validateResponse(CertificateChoiceResponse certificateChoiceResponse) throws TechnicalErrorException {
        if (certificateChoiceResponse.getCertificate() == null || isBlank(certificateChoiceResponse.getCertificate())) {
            logger.error("Certificate was not present in the session status response");
            throw new TechnicalErrorException("Certificate was not present in the session status response");
        }
    }

    private void validateResult(String result) throws MobileIdException {
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
}
