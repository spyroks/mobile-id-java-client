package ee.sk.mid.rest.dao.request;

import javax.validation.constraints.NotNull;

public class CertificateRequest extends AbstractRequest {

    @NotNull
    private String phoneNumber;

    @NotNull
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
