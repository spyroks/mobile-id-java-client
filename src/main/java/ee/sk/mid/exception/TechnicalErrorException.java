package ee.sk.mid.exception;

public class TechnicalErrorException extends MobileIdException {

    public TechnicalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalErrorException(String message) {
        super(message);
    }
}
