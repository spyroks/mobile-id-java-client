package ee.sk.mid.exception;

public class ExpiredException extends MobileIdException {

    public ExpiredException() {
    }

    public ExpiredException(String message) {
        super(message);
    }
}
