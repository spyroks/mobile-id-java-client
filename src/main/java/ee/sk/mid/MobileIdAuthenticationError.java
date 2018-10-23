package ee.sk.mid;

public enum MobileIdAuthenticationError {

    INVALID_RESULT("Response result verification failed."),
    SIGNATURE_VERIFICATION_FAILURE("Signature verification failed."),
    CERTIFICATE_EXPIRED("Signer's certificate expired.");

    private String message;

    MobileIdAuthenticationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
