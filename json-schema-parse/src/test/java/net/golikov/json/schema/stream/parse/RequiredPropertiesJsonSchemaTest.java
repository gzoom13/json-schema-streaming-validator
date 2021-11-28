package net.golikov.json.schema.stream.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.golikov.json.schema.stream.RequiredProperties.ValidationContext;
import net.golikov.json.schema.stream.required.RequiredPropertiesTestCase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class RequiredPropertiesJsonSchemaTest {

    @Test
    void returnsNoResultIfNoRequiredField() throws IOException {
        assertThat(parse("empty.json").getErrors()).isEmpty();
        assertThat(parse("empty.json").getResult()).isEmpty();
    }

    @Test
    void returnsErrorIfRequiredFieldContainsNoArray() throws IOException {
        assertHasErrors(parse("not-array.json"));
    }

    @Test
    void returnsErrorIfRequiredFieldContainsArrayWithNotOnlyStrings() throws IOException {
        assertHasErrors(parse("not-strings.json"));
    }

    @Test
    void returnsResultWithRequiredFields() throws IOException {
        ValidationContext validationContext = parse("schema.json").getResult().get();
        assertThat(new RequiredPropertiesTestCase(validationContext, "invalid.json")
                .result().hasErrors()).isTrue();
        assertThat(new RequiredPropertiesTestCase(validationContext, "valid.json")
                .result().hasErrors()).isFalse();
    }

    private void assertHasErrors(ParseResult<ValidationContext> parse) {
        assertThat(parse.getErrors()).isNotEmpty();
    }

    private ParseResult<ValidationContext> parse(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(RequiredPropertiesTestCase.class.getResource("parse/" + fileName));
        return new RequiredPropertiesJsonSchema(jsonNode).read();
    }

}