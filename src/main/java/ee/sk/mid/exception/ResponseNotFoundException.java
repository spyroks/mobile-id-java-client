package ee.sk.mid.exception;

public class ResponseNotFoundException extends MobileIdException {

    public ResponseNotFoundException() {
    }

    public ResponseNotFoundException(String message) {
        super(message);
    }
}
