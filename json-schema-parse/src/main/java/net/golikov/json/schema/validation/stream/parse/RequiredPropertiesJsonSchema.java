package net.golikov.json.schema.validation.stream.parse;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import net.golikov.json.schema.validation.stream.RequiredProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequiredPropertiesJsonSchema {

    private static final String FIELD_NAME = "required";
    private static final JsonPointer JSON_POINTER = JsonPointer.compile("/" + FIELD_NAME);
    private final JsonNode node;

    public RequiredPropertiesJsonSchema(JsonNode node) {
        this.node = node;
    }

    public ParseResult<RequiredProperties.ValidationContext> read() {
        JsonNode required = node.at(JSON_POINTER);
        if (required.isMissingNode()) {
            return new ParseResult<>();
        }
        if (!required.isArray()) {
            return new ParseResult<>(Collections.singletonList(String.format("\"%s\" field contains %s instead of array",
                    FIELD_NAME, required.getNodeType())));
        }
        List<JsonNodeType> notTextualNodeTypes = new ArrayList<>(0);
        List<String> requiredPropertyNames = new ArrayList<>(4);
        for (JsonNode jsonNode : required) {
            if (jsonNode.isTextual()) {
                requiredPropertyNames.add(jsonNode.textValue());
            } else {
                notTextualNodeTypes.add(jsonNode.getNodeType());
            }
        }
        if (!notTextualNodeTypes.isEmpty()) {
            return new ParseResult<>(Collections.singletonList(String.format("\"%s\" field contains %s instead of array",
                    FIELD_NAME, required.getNodeType())));
        } else {
            return new ParseResult<>(new RequiredProperties.ValidationContext(requiredPropertyNames));
        }
    }

}
