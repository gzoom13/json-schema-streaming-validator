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
        assertFalse(new RequiredPropertiesTestCase(new ValidationContext(Collections.emptyList()), "invalid.json")
                .result().hasErrors());
    }

    @Test
    public void invalidRequiredObjectProperties() throws Exception {
        assertTrue(invalid()
                .result().hasErrors());
    }

    public static RequiredPropertiesTestCase invalid() {
        return new RequiredPropertiesTestCase(new ValidationContext(Arrays.asList("latitude", "longitude")), "invalid.json");
    }

    @Test
    public void validRequiredObjectProperties() throws Exception {
        assertFalse(valid()
                .result().hasErrors());
    }

    public static RequiredPropertiesTestCase valid() {
        return new RequiredPropertiesTestCase(new ValidationContext(Arrays.asList("latitude", "longitude")), "valid.json");
    }

}