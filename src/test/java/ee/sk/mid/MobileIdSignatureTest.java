package ee.sk.mid;

import ee.sk.mid.exception.TechnicalErrorException;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class MobileIdSignatureTest {

    @Test
    public void getSignatureValueInBase64() {
        MobileIdSignature signature = new MobileIdSignature();
        signature.setValueInBase64("VGVyZSBNYWFpbG0=");
        assertEquals("VGVyZSBNYWFpbG0=", signature.getValueInBase64());
    }

    @Test
    public void getSignatureValueInBytes() {
        MobileIdSignature signature = new MobileIdSignature();
        signature.setValueInBase64("RGVkZ2Vob2c=");
        assertArrayEquals("Dedgehog".getBytes(), signature.getValue());
    }

    @Test(expected = TechnicalErrorException.class)
    public void incorrectBase64StringShouldThrowException() {
        MobileIdSignature signature = new MobileIdSignature();
        signature.setValueInBase64("äIsNotValidBase64Character");
        signature.getValue();
    }
}
