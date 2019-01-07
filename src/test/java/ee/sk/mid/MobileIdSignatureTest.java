package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MobileIdSignatureTest {

    @Test(expected = InvalidBase64CharacterException.class)
    public void setInvalidValueInBase64_shouldThrowException() {

        MobileIdSignature signature = MobileIdSignature.newBuilder()
                .withValueInBase64("!IsNotValidBase64Character")
                .build();

        signature.getValue();
    }

    @Test
    public void getSignatureValueInBase64() {

        MobileIdSignature signature = MobileIdSignature.newBuilder()
                .withValueInBase64("SEFDS0VSTUFO")
                .build();

        assertThat(signature.getValueInBase64(), is("SEFDS0VSTUFO"));
    }

    @Test
    public void getSignatureValueInBytes() {
        MobileIdSignature signature = MobileIdSignature.newBuilder()
                .withValueInBase64("SEFDS0VSTUFO")
                .build();

        assertThat(signature.getValue(), is("HACKERMAN".getBytes(StandardCharsets.UTF_8)));
    }
}
