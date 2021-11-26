package net.golikov.json.schema.validation.stream.parse;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import net.golikov.json.schema.validation.stream.RequiredProperties;

public class RequiredPropertiesJsonSchema {

    private static final JsonPointer JSON_POINTER = JsonPointer.compile("/required");
    private final JsonNode node;

    public RequiredPropertiesJsonSchema(JsonNode node) {
        this.node = node;
    }

    public ParseResult<RequiredProperties> read() {
        return new ParseResult<>();
    }

}
