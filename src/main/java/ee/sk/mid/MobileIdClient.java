package ee.sk.mid;

import ee.sk.mid.rest.MobileIdRestConnector;
import ee.sk.mid.rest.SessionStatusPoller;
import org.glassfish.jersey.client.ClientConfig;

import java.util.concurrent.TimeUnit;

public class MobileIdClient {

    private String relyingPartyUUID;
    private String relyingPartyName;
    private String hostUrl;
    private ClientConfig networkConnectionConfig;
    private TimeUnit pollingSleepTimeUnit = TimeUnit.SECONDS;
    private long pollingSleepTimeout = 1L;
    private TimeUnit sessionStatusResponseSocketOpenTimeUnit;
    private long sessionStatusResponseSocketOpenTimeValue;

    public CertificateRequestBuilder getCertificate() {
        MobileIdRestConnector connector = new MobileIdRestConnector(hostUrl, networkConnectionConfig);
        CertificateRequestBuilder builder = new CertificateRequestBuilder(connector);
        populateBuilderFields(builder);
        return builder;
    }

    public SignatureRequestBuilder createSignature() {
        MobileIdRestConnector connector = new MobileIdRestConnector(hostUrl, networkConnectionConfig);
        SessionStatusPoller sessionStatusPoller = createSessionStatusPoller(connector);
        SignatureRequestBuilder builder = new SignatureRequestBuilder(connector, sessionStatusPoller);
        populateBuilderFields(builder);
        return builder;
    }

    public AuthenticationRequestBuilder createAuthentication() {
        MobileIdRestConnector connector = new MobileIdRestConnector(hostUrl, networkConnectionConfig);
        SessionStatusPoller sessionStatusPoller = createSessionStatusPoller(connector);
        AuthenticationRequestBuilder builder = new AuthenticationRequestBuilder(connector, sessionStatusPoller);
        populateBuilderFields(builder);
        return builder;
    }

    public void setRelyingPartyUUID(String relyingPartyUUID) {
        this.relyingPartyUUID = relyingPartyUUID;
    }

    public void setRelyingPartyName(String relyingPartyName) {
        this.relyingPartyName = relyingPartyName;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public void setNetworkConnectionConfig(ClientConfig networkConnectionConfig) {
        this.networkConnectionConfig = networkConnectionConfig;
    }

    public void setSessionStatusResponseSocketOpenTime(TimeUnit timeUnit, long timeValue) {
        sessionStatusResponseSocketOpenTimeUnit = timeUnit;
        sessionStatusResponseSocketOpenTimeValue = timeValue;
    }

    public void setPollingSleepTimeout(TimeUnit unit, long timeout) {
        pollingSleepTimeUnit = unit;
        pollingSleepTimeout = timeout;
    }

    private void populateBuilderFields(MobileIdRequestBuilder builder) {
        builder.withRelyingPartyUUID(relyingPartyUUID);
        builder.withRelyingPartyName(relyingPartyName);
    }

    private SessionStatusPoller createSessionStatusPoller(MobileIdRestConnector connector) {
        SessionStatusPoller sessionStatusPoller = new SessionStatusPoller(connector);
        sessionStatusPoller.setPollingSleepTime(pollingSleepTimeUnit, pollingSleepTimeout);
        sessionStatusPoller.setResponseSocketOpenTime(sessionStatusResponseSocketOpenTimeUnit, sessionStatusResponseSocketOpenTimeValue);
        return sessionStatusPoller;
    }
}
