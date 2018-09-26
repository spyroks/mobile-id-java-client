package ee.sk.mid.rest.dao.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class AbstractRequest {

    @NotNull
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "has incorrect format")
    private String relyingPartyUUID;

    @NotNull
    @Size(min = 1, max = 100)
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
