package ee.sk.mid.rest.dao.request;

import javax.validation.constraints.NotNull;

public class AbstractRequest {

    @NotNull
    private String relyingPartyUUID;

    @NotNull
    private String relyingPartyName;

    @NotNull
    public String getRelyingPartyUUID() {
        return relyingPartyUUID;
    }

    public void setRelyingPartyUUID(@NotNull String relyingPartyUUID) {
        this.relyingPartyUUID = relyingPartyUUID;
    }

    @NotNull
    public String getRelyingPartyName() {
        return relyingPartyName;
    }

    public void setRelyingPartyName(@NotNull String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
    }

    @Override
    public String toString() {
        return "AbstractRequest{" +
                "relyingPartyUUID='" + relyingPartyUUID + '\'' +
                "relyingPartyName='" + relyingPartyName + '\'' +
                '}';
    }
}
