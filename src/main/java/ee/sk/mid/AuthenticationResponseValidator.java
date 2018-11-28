package ee.sk.mid;

import ee.sk.mid.exception.TechnicalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

import static ee.sk.mid.SignatureVerifier.verifyWithEC;
import static ee.sk.mid.SignatureVerifier.verifyWithRSA;

public class AuthenticationResponseValidator {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationResponseValidator.class);

    public MobileIdAuthenticationResult validate(MobileIdAuthentication authentication) {
        validateAuthentication(authentication);
        MobileIdAuthenticationResult authenticationResult = new MobileIdAuthenticationResult();
        AuthenticationIdentity identity = constructAuthenticationIdentity(authentication.getCertificate());
        authenticationResult.setAuthenticationIdentity(identity);
        if (!isResultOk(authentication)) {
            authenticationResult.setValid(false);
            authenticationResult.addError(MobileIdAuthenticationError.INVALID_RESULT);
        }
        if (!isSignatureValid(authentication)) {
            authenticationResult.setValid(false);
            authenticationResult.addError(MobileIdAuthenticationError.SIGNATURE_VERIFICATION_FAILURE);
        }
        if (!isCertificateValid(authentication.getCertificate())) {
            authenticationResult.setValid(false);
            authenticationResult.addError(MobileIdAuthenticationError.CERTIFICATE_EXPIRED);
        }
        return authenticationResult;
    }

    private void validateAuthentication(MobileIdAuthentication authentication) throws TechnicalErrorException {
        if (authentication.getCertificate() == null) {
            logger.error("Certificate is not present in the authentication response");
            throw new TechnicalErrorException("Certificate is not present in the authentication response");
        }
        if (authentication.getSignatureValueInBase64().isEmpty()) {
            logger.error("Signature is not present in the authentication response");
            throw new TechnicalErrorException("Signature is not present in the authentication response");
        }
        if (authentication.getHashType() == null) {
            logger.error("Hash type is not present in the authentication response");
            throw new TechnicalErrorException("Hash type is not present in the authentication response");
        }
    }

    AuthenticationIdentity constructAuthenticationIdentity(X509Certificate certificate) throws TechnicalErrorException {
        AuthenticationIdentity identity = new AuthenticationIdentity();
        try {
            LdapName ln = new LdapName(certificate.getSubjectDN().getName());
            for (Rdn rdn : ln.getRdns()) {
                String type = rdn.getType().toUpperCase();
                switch (type) {
                    case "GIVENNAME":
                        identity.setGivenName(rdn.getValue().toString());
                        break;
                    case "SURNAME":
                        identity.setSurName(rdn.getValue().toString());
                        break;
                    case "SERIALNUMBER":
                        identity.setIdentityCode(getIdentityNumber(rdn.getValue().toString()));
                        break;
                    case "C":
                        identity.setCountry(rdn.getValue().toString());
                        break;
                }
            }
            return identity;
        } catch (InvalidNameException e) {
            logger.error("Error getting authentication identity from the certificate", e);
            throw new TechnicalErrorException("Error getting authentication identity from the certificate", e);
        }
    }

    private String getIdentityNumber(String identityNumber) {
        if (identityNumber.contains("PNO")) {
            return identityNumber.split("-", 2)[1];
        } else {
            return identityNumber;
        }
    }

    private boolean isResultOk(MobileIdAuthentication authentication) {
        return "OK".equalsIgnoreCase(authentication.getResult());
    }

    private boolean isSignatureValid(MobileIdAuthentication authentication) {
        PublicKey publicKey = authentication.getCertificate().getPublicKey();
        return isAlgorithmRSA(publicKey) ? verifyWithRSA(publicKey, authentication) : verifyWithEC(publicKey, authentication);
    }

    private boolean isAlgorithmRSA(PublicKey publicKey) {
        return publicKey.getAlgorithm().equals("RSA");
    }

    private boolean isCertificateValid(X509Certificate certificate) {
        return !certificate.getNotAfter().before(new Date());
    }
}
