package ee.sk.mid.rest.dao.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.sk.mid.HashType;
import ee.sk.mid.Language;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AuthenticationRequest extends AbstractRequest {

    @NotNull
    @Pattern(regexp = "^\\+\\d{8,30}$", message = "must contain of + and numbers(8-30)")
    private String phoneNumber;

    @NotNull
    @Size(min = 9, max = 100)
    private String nationalIdentityNumber;

    @NotNull
    @Size(min = 1, max = 128)
    private String hash;

    @NotNull(message = "allowed values are: SHA256, SHA512 or SHA384")
    private HashType hashType;

    @NotNull(message = "allowed values are: EST, ENG, RUS or LIT")
    private Language language;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 100)
    private String displayText;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationalIdentityNumber() {
        return nationalIdentityNumber;
    }

    public void setNationalIdentityNumber(String nationalIdentityNumber) {
        this.nationalIdentityNumber = nationalIdentityNumber;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public HashType getHashType() {
        return hashType;
    }

    public void setHashType(HashType hashType) {
        this.hashType = hashType;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", nationalIdentityNumber='" + nationalIdentityNumber + '\'' +
                ", hash='" + hash + '\'' +
                ", hashType=" + hashType +
                ", language=" + language +
                ", displayText='" + displayText + '\'' +
                '}';
    }
}
