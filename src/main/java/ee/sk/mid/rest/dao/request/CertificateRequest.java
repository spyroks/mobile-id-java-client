package ee.sk.mid.rest.dao.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CertificateRequest extends AbstractRequest {

    @NotNull
    @Pattern(regexp = "^\\+\\d{8,30}$", message = "must contain of + and numbers(8-30)")
    private String phoneNumber;

    @NotNull
    @Size(min = 9, max = 100)
    private String nationalIdentityNumber;

    @NotNull
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NotNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NotNull
    public String getNationalIdentityNumber() {
        return nationalIdentityNumber;
    }

    public void setNationalIdentityNumber(@NotNull String nationalIdentityNumber) {
        this.nationalIdentityNumber = nationalIdentityNumber;
    }

    @Override
    public String toString() {
        return "CertificateRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", nationalIdentityNumber='" + nationalIdentityNumber + '\'' +
                '}';
    }
}
