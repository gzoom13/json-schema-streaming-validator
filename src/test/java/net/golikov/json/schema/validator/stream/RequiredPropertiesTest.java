package net.golikov.json.schema.validator.stream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequiredPropertiesTest {

    private String testDirectoryName = "required";

    @Test
    void newValidationContextHasNoErrors() {
        assertFalse(new RequiredProperties.ValidationContext().hasErrors());
    }

    @Test
    void invalidRequiredObjectProperties() throws IOException {
        assertFalse(test("valid.json").hasErrors());
    }

    @Test
    void validRequiredObjectProperties() throws IOException {
        assertTrue(test("invalid.json").hasErrors());
    }

    private ValidationContext test(String fileName) throws IOException {
        JsonFactory factory = new JsonFactory();
        List<String> properties = Arrays.asList("latitude", "longitude");
        RequiredProperties.ValidationContext res = new RequiredProperties.ValidationContext(properties);
        try (JsonParser p = factory.createParser(getClass().getResource(resourceName(fileName)));
             JsonParserWrapper parser = new JsonParserWrapper(p)) {
            RequiredProperties validator = new RequiredProperties();
            JsonToken jsonToken = parser.nextToken();
            while (jsonToken != null) {
                res = validator.validate(res, parser);
                jsonToken = parser.nextToken();
            }
        }
        return res;
    }

    private String resourceName(String fileName) {
        return testDirectoryName + File.separatorChar + fileName;
    }

}