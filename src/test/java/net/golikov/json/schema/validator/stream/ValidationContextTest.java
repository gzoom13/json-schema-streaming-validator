package net.golikov.json.schema.validator.stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ValidationContextTest {

    @Test
    void newValidationContextHasNoErrors() {
        assertFalse(new RequiredProperties.ValidationContext().hasErrors());
    }
}