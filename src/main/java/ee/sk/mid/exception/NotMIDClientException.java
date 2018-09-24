package ee.sk.mid.exception;

public class NotMIDClientException extends MobileIdException {

    private final static String ERROR_MESSAGE = "User is not a Mobile-ID client";

    public NotMIDClientException() {
        super(ERROR_MESSAGE);
    }
}
