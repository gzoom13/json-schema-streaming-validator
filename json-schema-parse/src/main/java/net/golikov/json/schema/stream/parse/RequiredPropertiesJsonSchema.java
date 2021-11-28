package net.golikov.json.schema.stream.parse;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import net.golikov.json.schema.stream.RequiredProperties;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            return new ParseResult<>(String.format("\"%s\" field contains %s instead of array",
                    FIELD_NAME, required.getNodeType()));
        }
        List<JsonNodeType> notTextualNodeTypes = new ArrayList<>(0);
        List<String> requiredPropertyNames = new ArrayList<>();
        for (JsonNode jsonNode : required) {
            if (jsonNode.isTextual()) {
                requiredPropertyNames.add(jsonNode.textValue());
            } else {
                notTextualNodeTypes.add(jsonNode.getNodeType());
            }
        }
        if (!notTextualNodeTypes.isEmpty()) {
            return new ParseResult<>(String.format("\"%s\" field contains array, " +
                            "including %s node types instead of only strings",
                    FIELD_NAME, notTextualNodeTypes));
        }
        List<String> duplicates = duplicates(requiredPropertyNames);
        if (!duplicates.isEmpty()) {
            return new ParseResult<>(String.format("\"%s\" field contains duplicates: %s",
                    FIELD_NAME, duplicates));
        }
        return new ParseResult<>(new RequiredProperties.ValidationContext(requiredPropertyNames));
    }

    private List<String> duplicates(List<String> requiredPropertyNames) {
        return requiredPropertyNames.stream()
                .collect(Collectors.groupingBy(Function.identity()))
                .values()
                .stream()
                .filter(l -> l.size() > 1)
                .map(l -> l.get(0))
                .collect(Collectors.toList());
    }

}
