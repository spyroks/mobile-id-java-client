package ee.sk.mid;

import ee.sk.mid.exception.InvalidBase64CharacterException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MobileIdSignatureTest {

    private MobileIdSignature signature;

    @Before
    public void setUp() {
        signature = new MobileIdSignature();
    }

    @Test(expected = InvalidBase64CharacterException.class)
    public void setInvalidValueInBase64_shouldThrowException() {
        signature.setValueInBase64("!IsNotValidBase64Character");
        signature.getValue();
    }

    @Test
    public void getSignatureValueInBase64() {
        signature.setValueInBase64("SEFDS0VSTUFO");
        assertThat(signature.getValueInBase64(), is("SEFDS0VSTUFO"));
    }

    @Test
    public void getSignatureValueInBytes() {
        signature.setValueInBase64("SEFDS0VSTUFO");
        assertThat(signature.getValue(), is("HACKERMAN".getBytes()));
    }
}
