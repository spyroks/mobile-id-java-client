package ee.sk.mid.rest;

import ee.sk.mid.exception.*;
import ee.sk.mid.exception.NotFoundException;
import ee.sk.mid.rest.dao.SessionStatus;
import ee.sk.mid.rest.dao.request.AuthenticationRequest;
import ee.sk.mid.rest.dao.request.CertificateRequest;
import ee.sk.mid.rest.dao.request.SessionStatusRequest;
import ee.sk.mid.rest.dao.request.SignatureRequest;
import ee.sk.mid.rest.dao.response.AuthenticationResponse;
import ee.sk.mid.rest.dao.response.CertificateChoiceResponse;
import ee.sk.mid.rest.dao.response.SignatureResponse;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class MobileIdRestConnector implements MobileIdConnector {

    private static final Logger logger = LoggerFactory.getLogger(MobileIdRestConnector.class);
    private static final String CERTIFICATE_PATH = "/mid-api/certificate";
    private static final String SIGNATURE_PATH = "/mid-api/signature";
    private static final String AUTHENTICATION_PATH = "/mid-api/authentication";

    private String endpointUrl;
    private ClientConfig clientConfig;

    public MobileIdRestConnector(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public MobileIdRestConnector(String endpointUrl, ClientConfig clientConfig) {
        this(endpointUrl);
        this.clientConfig = clientConfig;
    }

    @Override
    public CertificateChoiceResponse getCertificate(CertificateRequest request) {
        logger.debug("Getting certificate for phone number: " + request.getPhoneNumber());
        URI uri = UriBuilder
                .fromUri(endpointUrl)
                .path(CERTIFICATE_PATH)
                .build();
        return postCertificateRequest(uri, request);
    }

    @Override
    public SignatureResponse sign(SignatureRequest request) {
        logger.debug("Signing for phone number: " + request.getPhoneNumber());
        URI uri = UriBuilder
                .fromUri(endpointUrl)
                .path(SIGNATURE_PATH)
                .build();
        return postSignatureRequest(uri, request);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.debug("Authenticating for phone number " + request.getPhoneNumber());
        URI uri = UriBuilder
                .fromUri(endpointUrl)
                .path(AUTHENTICATION_PATH)
                .build();
        return postAuthenticationRequest(uri, request);
    }

    @Override
    public SessionStatus getSessionStatus(SessionStatusRequest request, String path) throws SessionNotFoundException {
        logger.debug("Getting session status for " + request.getSessionId());
        UriBuilder uriBuilder = UriBuilder
                .fromUri(endpointUrl)
                .path(path);
        addResponseSocketOpenTimeUrlParameter(request, uriBuilder);
        URI uri = uriBuilder.build(request.getSessionId());
        try {
            return prepareClient(uri).get(SessionStatus.class);
        } catch (javax.ws.rs.NotFoundException e) {
            logger.warn("Session " + request + " not found: " + e.getMessage());
            throw new SessionNotFoundException();
        }
    }

    private CertificateChoiceResponse postCertificateRequest(URI uri, CertificateRequest request) {
        return postRequest(uri, request, CertificateChoiceResponse.class);
    }

    private SignatureResponse postSignatureRequest(URI uri, SignatureRequest request) {
        return postRequest(uri, request, SignatureResponse.class);
    }

    private AuthenticationResponse postAuthenticationRequest(URI uri, AuthenticationRequest request) {
        return postRequest(uri, request, AuthenticationResponse.class);
    }

    private <T, V> T postRequest(URI uri, V request, Class<T> responseType) {
        try {
            Entity<V> requestEntity = Entity.entity(request, MediaType.APPLICATION_JSON);
            return prepareClient(uri).post(requestEntity, responseType);
        } catch (InternalServerErrorException e) {
            logger.warn("Error getting response from cert-store/MSSP for URI " + uri + ": " + e.getMessage());
            throw new ResponseRetrievingException();
        } catch (javax.ws.rs.NotFoundException e) {
            logger.warn("Response not found for URI " + uri + ": " + e.getMessage());
            throw new NotFoundException();
        } catch (BadRequestException e) {
            logger.warn("Request is invalid for URI " + uri + ": " + e.getMessage());
            throw new ParameterMissingException();
        } catch (NotAuthorizedException e) {
            logger.warn("Request is unauthorized for URI " + uri + ": " + e.getMessage());
            throw new UnauthorizedException();
        }
    }

    private Invocation.Builder prepareClient(URI uri) {
        Client client = clientConfig == null ? ClientBuilder.newClient() : ClientBuilder.newClient(clientConfig);
        return client
                .register(new LoggingFilter())
                .target(uri)
                .request()
                .accept(APPLICATION_JSON_TYPE);
    }

    private void addResponseSocketOpenTimeUrlParameter(SessionStatusRequest request, UriBuilder uriBuilder) {
        if (request.isResponseSocketOpenTimeSet()) {
            TimeUnit timeUnit = request.getResponseSocketOpenTimeUnit();
            long timeValue = request.getResponseSocketOpenTimeValue();
            long queryTimeoutInMilliseconds = timeUnit.toMillis(timeValue);
            uriBuilder.queryParam("timeoutMs", queryTimeoutInMilliseconds);
        }
    }
}
