package net.golikov.json.schema.stream.required;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import net.golikov.json.schema.stream.JsonParserWrapper;
import net.golikov.json.schema.stream.RequiredProperties;
import net.golikov.json.schema.stream.ValidationContext;

import java.io.IOException;

public class RequiredPropertiesTestCase {

    private final String testCaseFileName;
    private final RequiredProperties.ValidationContext initialContext;

    public RequiredPropertiesTestCase(RequiredProperties.ValidationContext initialContext,
                                      String testCaseFileName) {
        this.testCaseFileName = testCaseFileName;
        this.initialContext = initialContext;
    }

    public ValidationContext result() throws IOException {
        JsonFactory factory = new JsonFactory();
        RequiredProperties.ValidationContext res = this.initialContext;
        try (JsonParser p = factory.createParser(getClass().getResource(testCaseFileName));
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

}