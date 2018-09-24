package ee.sk.mid;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class HashTypeDeserializer extends JsonDeserializer {

    @Override
    public HashType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final String jsonValue = jsonParser.getValueAsString();
        for (final HashType enumValue : HashType.values()) {
            if (enumValue.name().toLowerCase().equals(jsonValue.toLowerCase())) {
                return enumValue;
            }
        }
        return null;
    }
}
