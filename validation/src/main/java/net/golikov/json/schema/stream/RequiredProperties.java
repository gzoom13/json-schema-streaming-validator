package net.golikov.json.schema.stream;

import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequiredProperties {

    public RequiredProperties.ValidationContext validate(RequiredProperties.ValidationContext context, CurrentToken token) throws IOException {
        if (context.notFoundYet.isEmpty()) {
            return context;
        }
        if (token.currentToken() == JsonToken.FIELD_NAME) {
            ArrayList<String> strings = new ArrayList<>(context.notFoundYet);
            if (strings.remove(token.getText())) {
                return new ValidationContext(strings);
            }
        }
        return context;
    }

    public static class ValidationContext implements net.golikov.json.schema.stream.ValidationContext {

        private final List<String> notFoundYet;

        public ValidationContext(List<String> requiredPropertyNames) {
            this.notFoundYet = requiredPropertyNames;
        }

        @Override
        public boolean hasErrors() {
            return !notFoundYet.isEmpty();
        }

        @Override
        public boolean isValidated() {
            return false;
        }

    }

}
