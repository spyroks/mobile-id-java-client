package ee.sk.mid.exception;

public class InvalidBase64CharacterException extends MobileIdException {

    public InvalidBase64CharacterException() {
    }

    public InvalidBase64CharacterException(String message) {
        super(message);
    }
}
