package net.golikov.json.schema.validation.stream.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.golikov.json.schema.validation.stream.RequiredProperties;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

class RequiredPropertiesJsonSchemaTest {

    private static final String testDirectoryName = "required";

    @Test
    void returnsNoResultIfNoRequiredField() throws IOException {
        assertFalse(parse("empty.json").hasResult());
    }

    @Test
    void returnsErrorIfRequiredFieldContainsNoArray() throws IOException {
        assertFalse(parse("not-array.json").getErrors().isEmpty());
    }


    private ParseResult<RequiredProperties> parse(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(getClass().getResource(testDirectoryName + File.separatorChar + fileName));
        ParseResult<RequiredProperties> result = new RequiredPropertiesJsonSchema(jsonNode).read();
        return result;
    }

}