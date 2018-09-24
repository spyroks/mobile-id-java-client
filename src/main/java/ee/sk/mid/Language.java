package ee.sk.mid;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = LanguageDeserializer.class)
public enum Language {

    EST,
    ENG,
    RUS,
    LIT
}
