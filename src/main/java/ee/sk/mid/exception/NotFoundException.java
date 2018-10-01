package ee.sk.mid.exception;

public class NotFoundException extends MobileIdException {

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }
}
