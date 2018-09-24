package ee.sk.mid.exception;

public class ParameterMissingException extends MobileIdException {

    public ParameterMissingException() {
    }

    public ParameterMissingException(String message) {
        super(message);
    }
}
