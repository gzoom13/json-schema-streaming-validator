package net.golikov.json.schema.stream.required;

import net.golikov.json.schema.stream.RequiredProperties.ValidationContext;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequiredPropertiesTest {

    @Test
    public void noRequiredPropertiesReturnNoErrors() throws Exception {
        assertFalse(RequiredPropertiesTestCase.invalid(new ValidationContext(Collections.emptyList()))
                .result().hasErrors());
    }

    @Test
    public void invalidRequiredObjectProperties() throws Exception {
        assertTrue(RequiredPropertiesTestCase.invalid(new ValidationContext(Arrays.asList("latitude", "longitude")))
                .result().hasErrors());
    }

    @Test
    public void validRequiredObjectProperties() throws Exception {
        assertFalse(RequiredPropertiesTestCase.valid(new ValidationContext(Arrays.asList("latitude", "longitude")))
                .result().hasErrors());
    }

}