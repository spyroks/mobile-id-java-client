package ee.sk.mid;

import ee.sk.mid.exception.TechnicalErrorException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;

public class SignatureVerifier {

    private static final Logger logger = LoggerFactory.getLogger(SignatureVerifier.class);

    public static boolean verifyWithRSA(PublicKey signersPublicKey, MobileIdAuthentication authentication) throws TechnicalErrorException {
        try {
            Signature signature = Signature.getInstance("NONEwithRSA");
            signature.initVerify(signersPublicKey);
            byte[] signedHash = Base64.decodeBase64(authentication.getSignedHashInBase64());
            byte[] signedDigest = addPadding(authentication.getHashType().getDigestInfoPrefix(), signedHash);
            signature.update(signedDigest);
            return signature.verify(authentication.getSignatureValue());
        } catch (GeneralSecurityException e) {
            logger.error("Signature verification with RSA failed");
            throw new TechnicalErrorException("Signature verification with RSA failed", e);
        }
    }

    private static byte[] addPadding(byte[] digestInfoPrefix, byte[] digest) {
        return ArrayUtils.addAll(digestInfoPrefix, digest);
    }

    public static boolean verifyWithECDSA(PublicKey signersPublicKey, MobileIdAuthentication authentication) throws TechnicalErrorException {
        try {
            Security.addProvider(new BouncyCastleProvider());
            Signature signature = Signature.getInstance("NONEwithECDSA", "BC");
            signature.initVerify(signersPublicKey);
            byte[] signedDigest = Base64.decodeBase64(authentication.getSignedHashInBase64());
            signature.update(signedDigest);
            return signature.verify(fromCVCEncoding(authentication.getSignatureValue()));
        } catch (GeneralSecurityException e) {
            logger.error("Signature verification with ECDSA failed");
            throw new TechnicalErrorException("Signature verification with ECDSA failed", e);
        }
    }

    private static byte[] fromCVCEncoding(byte[] cvcEncoding) {
        byte[][] elements = splitArrayInTheMiddle(cvcEncoding);
        BigInteger r = new BigInteger(1, elements[0]);
        BigInteger s = new BigInteger(1, elements[1]);
        return encodeInAsn1(r, s);
    }

    private static byte[][] splitArrayInTheMiddle(byte[] array) {
        return new byte[][] {
                ArrayUtils.subarray(array, 0, array.length / 2),
                ArrayUtils.subarray(array, array.length / 2, array.length)
        };
    }

    private static byte[] encodeInAsn1(BigInteger r, BigInteger s) {
        ASN1EncodableVector sequence = new ASN1EncodableVector();
        sequence.add(new ASN1Integer(r));
        sequence.add(new ASN1Integer(s));
        try {
            return new DERSequence(sequence).getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
