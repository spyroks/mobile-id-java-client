
[![Build Status](https://travis-ci.org/spyroks/mobile-id-java-client.svg?branch=master)](https://travis-ci.org/spyroks/mobile-id-java-client)
[![Coverage Status](https://img.shields.io/codecov/c/github/spyroks/mobile-id-java-client.svg)](https://codecov.io/gh/spyroks/mobile-id-java-client)
[![License: MIT](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)

# Mobile-ID Java client
Mobile-ID Java client is a Java library that can be used for easy integration of the [Mobile-ID](https://www.id.ee/index.php?id=36809) solutions to information systems or e-services.

* [Features](#features)
* [Requirements](#requirements)
* [Documentation](#documentation)
* [Usage](#usage)
* [License](#license)

## Features
* Simple interface for user authentication
* Simple interface for digital signature services

## Requirements
* Java 1.7
* Internet access to Mobile-ID demo environment

## Documentation
MID REST interface docs: https://github.com/SK-EID/MID

## Usage
* [Configure the client](#configure-the-client)
* [Configure client network connection](#configure-client-network-connection)
* [Retrieve signing certificate](#retrieve-signing-certificate)
* [Create a signature](#create-a-signature)
  - [Create a signature from existing hash](#create-a-signature-from-existing-hash)
  - [Create a signature from unhashed data](#create-a-signature-from-unhashed-data)
* [Authenticate](#authenticate)
  - [Get an authentication response](#get-an-authentication-response)
  - [Verify an authentication response](#verify-an-authentication-response)

### Configure the client
```java
MobileIdClient client = new MobileIdClient();
client.setRelyingPartyUUID("e8189051-5634-4fbe-a6e8-fe1b9a9ef445");
client.setRelyingPartyName("ToomasBank");
client.setHostUrl("http://sk-mid-test2:9000");
```

> **Note** that these values are test environment specific.

> UUID, name and host URL of the relying party – previously agreed with Application Provider and DigiDocService operator.

### Configure client network connection
Under the hood operations as signing and authentication consist of 2 request steps:

* Initiation request
* Session status request

Session status request by default is a long poll method, meaning it might not return until a timeout expires. The caller can tune the request parameters inside the bounds set by a service operator by using the `setPollingSleepTimeout(TimeUnit, long)`:

```java
client.setPollingSleepTimeout(TimeUnit.SECONDS, 2L);
```

> Check [Long polling](https://github.com/SK-EID/MID#334-long-polling) documentation chapter for more information.

### Retrieve signing certificate
```java
CertificateRequest request = client
        .createCertificateRequestBuilder()
        .withPhoneNumber("+37200000433")
        .withNationalIdentityNumber("14212128021")
        .build();

CertificateChoiceResponse response = client.getMobileIdConnector().getCertificate(request);

X509Certificate certificate = client.createMobileIdCertificate(response);
```

> **Note** that the certificate retrieving process (before the actual singing) is necessary for the AdES-style digital signatures which require knowledge of the certificate beforehand.

### Create a signature

#### Create a signature from existing hash
```java
SignableHash hashToSign = new SignableHash();
hashToSign.setHashInBase64("AE7S1QxYjqtVv+Tgukv2bMMi9gDCbc9ca2vy/iIG6ug=");
hashToSign.setHashType(HashType.SHA256);

String verificationCode = hashToSign.calculateVerificationCode();

SignatureRequest request = client
        .createSignatureRequestBuilder()
        .withPhoneNumber("+37200000433")
        .withNationalIdentityNumber("14212128021")
        .withSignableHash(hashToSign)
        .withLanguage(Language.EST)
        .build();

SignatureResponse response = client.getMobileIdConnector().sign(request);

SessionStatus sessionStatus = client.getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionId(),
"/mid-api/signature/session/{sessionId}");

MobileIdSignature signature = client.createMobileIdSignature(sessionStatus);
```

> **Note** that `verificationCode` of the service should be displayed on the screen, so the person could verify if the verification code displayed on the screen and code sent him as a text message are identical.

#### Create a signature from unhashed data
This is a good case when we have some data that we want to sign, but it isn't transformed into a hash yet. We can use `SignableData`, simply providing it with the data and the wanted hash algorithm and the client will deal with hashing for you.

```java
SignableData dataToSign = new SignableData("HACKERMAN".getBytes(StandardCharsets.UTF_8));
dataToSign.setHashType(HashType.SHA256);

String verificationCode = dataToSign.calculateVerificationCode();

SignatureRequest request = client
        .createSignatureRequestBuilder()
        .withPhoneNumber("+37200000433")
        .withNationalIdentityNumber("14212128021")
        .withSignableData(dataToSign)
        .withLanguage(Language.EST)
        .build();

SignatureResponse response = client.getMobileIdConnector().sign(request);

SessionStatus sessionStatus = client.getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionId(),
"/mid-api/signature/session/{sessionId}");

MobileIdSignature signature = client.createMobileIdSignature(sessionStatus);
```

> **Note** that `verificationCode` of the service should be displayed on the screen, so the person could verify if the verification code displayed on the screen and code sent him as a text message are identical.

### Authenticate

#### Get an authentication response
For security reasons, a new hash value must be created for each new authentication request.

```java
MobileIdAuthenticationHash authenticationHash = createRandomAuthenticationHash();

String verificationCode = authenticationHash.calculateVerificationCode();

AuthenticationRequest request = client
        .createAuthenticationRequestBuilder()
        .withPhoneNumber("+37200000433")
        .withNationalIdentityNumber("14212128021")
        .withAuthenticationHash(authenticationHash)
        .withLanguage(Language.EST)
        .build();

AuthenticationResponse response = client.getMobileIdConnector().authenticate(request);

SessionStatus sessionStatus = client.getSessionStatusPoller().fetchFinalSessionStatus(response.getSessionId(),
"/mid-api/authentication/session/{sessionId}");

MobileIdAuthentication authentication = client.createMobileIdAuthentication(sessionStatus);
```

> **Note** that `verificationCode` of the service should be displayed on the screen, so the person could verify if the verification code displayed on the screen and code sent him as a text message are identical.

#### Verify an authentication response
```java
AuthenticationResponseValidator validator = new AuthenticationResponseValidator();
MobileIdAuthenticationResult authenticationResult = validator.validate(authentication);

assertThat(authenticationResult.isValid(), is(true));
assertThat(authenticationResult.getErrors().isEmpty(), is(true));
```

When the authentication result is valid a session could be created now within the e-service or application. As the session logic is dependent on the implementation and may vary from system to system, this is something integrator has to do himself.

When the authentication result is not valid then the reasons for invalidity are obtainable like this:

```java
List <String> errors = authenticationResult.getErrors();
```

`AuthenticationIdentity` could be helpful for obtaining information about the authenticated person when constructing the session.

```java
AuthenticationIdentity authenticationIdentity = authenticationResult.getAuthenticationIdentity();
String givenName = authenticationIdentity.getGivenName();
String surName = authenticationIdentity.getSurName();
String identityCode = authenticationIdentity.getIdentityCode();
String country = authenticationIdentity.getCountry();
```

## License
This project is licensed under the terms of the [MIT license](LICENSE).