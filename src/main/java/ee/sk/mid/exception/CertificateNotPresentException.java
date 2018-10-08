package ee.sk.mid.exception;

public class CertificateNotPresentException extends MobileIdException {

    public CertificateNotPresentException() {
    }

    public CertificateNotPresentException(String message) {
        super(message);
    }
}
