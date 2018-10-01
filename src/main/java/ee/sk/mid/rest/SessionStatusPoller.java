package ee.sk.mid.rest;

import ee.sk.mid.exception.*;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class SessionStatusPoller {

    private static final Logger logger = LoggerFactory.getLogger(SessionStatusPoller.class);
    private MobileIdConnector connector;
    private TimeUnit pollingSleepTimeUnit = TimeUnit.SECONDS;
    private long pollingSleepTimeout = 1L;
    private TimeUnit responseSocketOpenTimeUnit;
    private long responseSocketOpenTimeValue;

    public SessionStatusPoller(MobileIdConnector connector) {
        this.connector = connector;
    }

    public SessionStatus fetchFinalSessionStatus(String sessionId, String path) throws MobileIdException {
        logger.debug("Starting to poll session status for session " + sessionId);
        try {
            SessionStatus status = pollForFinalSessionStatus(sessionId, path);
            validateResult(status);
            return status;
        } catch (InterruptedException e) {
            logger.error("Failed to poll session status: " + e.getMessage());
            throw new TechnicalErrorException("Failed to poll session status: " + e.getMessage(), e);
        }
    }

    private SessionStatus pollForFinalSessionStatus(String sessionId, String path) throws InterruptedException {
        SessionStatus sessionStatus = null;
        while (sessionStatus == null || equalsIgnoreCase("RUNNING", sessionStatus.getState())) {
            sessionStatus = pollSessionStatus(sessionId, path);
            if (equalsIgnoreCase("COMPLETE", sessionStatus.getState())) {
                break;
            }
            logger.debug("Sleeping for " + pollingSleepTimeout + " " + pollingSleepTimeUnit);
            pollingSleepTimeUnit.sleep(pollingSleepTimeout);
        }
        logger.debug("Got session final session status response");
        return sessionStatus;
    }

    private SessionStatus pollSessionStatus(String sessionId, String path) {
        logger.debug("Polling session status");
        SessionStatusRequest request = createSessionStatusRequest(sessionId);
        return connector.getSessionStatus(request, path);
    }

    private SessionStatusRequest createSessionStatusRequest(String sessionId) {
        SessionStatusRequest request = new SessionStatusRequest(sessionId);
        if (responseSocketOpenTimeUnit != null && responseSocketOpenTimeValue > 0) {
            request.setResponseSocketOpenTime(responseSocketOpenTimeUnit, responseSocketOpenTimeValue);
        }
        return request;
    }

    private void validateResult(SessionStatus status) throws MobileIdException {
        String result = status.getResult();
        if (result == null) {
            logger.error("Result is missing in the session status response");
            throw new TechnicalErrorException("Result is missing in the session status response");
        }
        validateResult(result);
    }

    private void validateResult(String result) throws MobileIdException {
        if (equalsIgnoreCase(result, "TIMEOUT")) {
            logger.debug("Session timeout");
            throw new SessionTimeoutException();
        } else if (equalsIgnoreCase(result, "ERROR")) {
            logger.debug("Error getting response from cert-store/MSSP");
            throw new ResponseRetrievingException();
        } else if (equalsIgnoreCase(result, "NOT_MID_CLIENT")) {
            logger.debug("Given user has no active certificates and is not M-ID client");
            throw new NotMIDClientException();
        } else if (equalsIgnoreCase(result, "EXPIRED_TRANSACTION")) {
            logger.debug("MSSP transaction timed out");
            throw new ExpiredException();
        } else if (equalsIgnoreCase(result, "USER_CANCELLED")) {
            logger.debug("User cancelled the operation");
            throw new UserCancellationException();
        } else if (equalsIgnoreCase(result, "MID_NOT_READY")) {
            logger.debug("Mobile-ID not ready");
            throw new MIDNotReadyException();
        } else if (equalsIgnoreCase(result, "PHONE_ABSENT")) {
            logger.debug("Sim not available");
            throw new SimNotAvailableException();
        } else if (equalsIgnoreCase(result, "DELIVERY_ERROR")) {
            logger.debug("SMS sending error");
            throw new DeliveryException();
        } else if (equalsIgnoreCase(result, "SIM_ERROR")) {
            logger.debug("Invalid response from card");
            throw new InvalidCardResponseException();
        } else if (equalsIgnoreCase(result, "SIGNATURE_HASH_MISMATCH")) {
            logger.debug("Hash does not match with certificate type");
            throw new SignatureHashMismatchException();
        } else if (!equalsIgnoreCase(result, "OK")) {
            logger.warn("Session status end result is '" + result + "'");
            throw new TechnicalErrorException("Session status end result is '" + result + "'");
        }
    }

    public void setPollingSleepTime(TimeUnit unit, long timeout) {
        logger.debug("Polling sleep time is " + timeout + " " + unit.toString());
        pollingSleepTimeUnit = unit;
        pollingSleepTimeout = timeout;
    }

    public void setResponseSocketOpenTime(TimeUnit timeUnit, long timeValue) {
        this.responseSocketOpenTimeUnit = timeUnit;
        this.responseSocketOpenTimeValue = timeValue;
    }
}
