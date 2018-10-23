package ee.sk.mid;

import java.util.ArrayList;
import java.util.List;

public class MobileIdAuthenticationResult {

    private AuthenticationIdentity authenticationIdentity;
    private boolean valid = true;
    private List<String> errors = new ArrayList<>();

    public AuthenticationIdentity getAuthenticationIdentity() {
        return authenticationIdentity;
    }

    public void setAuthenticationIdentity(AuthenticationIdentity authenticationIdentity) {
        this.authenticationIdentity = authenticationIdentity;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void addError(MobileIdAuthenticationError error) {
        errors.add(error.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
