package ee.sk.mid;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class LanguageDeserializer extends JsonDeserializer {

    @Override
    public Language deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final String jsonValue = jsonParser.getValueAsString();
        for (final Language enumValue : Language.values()) {
            if (enumValue.name().toLowerCase().equals(jsonValue.toLowerCase())) {
                return enumValue;
            }
        }
        return null;
    }
}
