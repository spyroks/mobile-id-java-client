package ee.sk.mid.exception;

class MobileIdException extends RuntimeException {

    MobileIdException() {
    }

    MobileIdException(String message) {
        super(message);
    }

    MobileIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
