package ee.sk.mid.rest.dao.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import ee.sk.mid.HashType;
import ee.sk.mid.Language;

import javax.validation.constraints.NotNull;

public class SignatureRequest extends AbstractRequest {

    @NotNull
    private String phoneNumber;

    @NotNull
    private String nationalIdentityNumber;

    @NotNull
    private String hash;

    @NotNull
    private HashType hashType;

    @NotNull
    private Language language;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String displayText;

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

    @NotNull
    public String getHash() {
        return hash;
    }

    public void setHash(@NotNull String hash) {
        this.hash = hash;
    }

    @NotNull
    public HashType getHashType() {
        return hashType;
    }

    public void setHashType(@NotNull HashType hashType) {
        this.hashType = hashType;
    }

    @NotNull
    public Language getLanguage() {
        return language;
    }

    public void setLanguage(@NotNull Language language) {
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
        return "SignatureRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", nationalIdentityNumber='" + nationalIdentityNumber + '\'' +
                ", hash='" + hash + '\'' +
                ", hashType=" + hashType +
                ", language=" + language +
                ", displayText='" + displayText + '\'' +
                '}';
    }
}