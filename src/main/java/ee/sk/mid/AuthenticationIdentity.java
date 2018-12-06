package ee.sk.mid;

public class AuthenticationIdentity {

    private String givenName;
    private String surName;
    private String identityCode;
    private String country;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getIdentityCode() {
        return identityCode;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "AuthenticationIdentity{" +
                "givenName='" + givenName + '\'' +
                ", surName='" + surName + '\'' +
                ", identityCode='" + identityCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
