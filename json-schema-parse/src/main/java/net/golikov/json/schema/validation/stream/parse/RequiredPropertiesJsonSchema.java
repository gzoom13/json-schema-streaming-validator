package net.golikov.json.schema.validation.stream.parse;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import net.golikov.json.schema.validation.stream.RequiredProperties;

import java.util.Collections;

public class RequiredPropertiesJsonSchema {

    private static final String FIELD_NAME = "required";
    private static final JsonPointer JSON_POINTER = JsonPointer.compile("/" + FIELD_NAME);
    private final JsonNode node;

    public RequiredPropertiesJsonSchema(JsonNode node) {
        this.node = node;
    }

    public ParseResult<RequiredProperties> read() {
        JsonNode required = node.at(JSON_POINTER);
        if (!required.isArray()) {
            return new ParseResult<>(Collections.singletonList(String.format("\"%s\" field contains %s instead of array",
                    FIELD_NAME, required.getNodeType())));
        }
        return new ParseResult<>();
    }

}
