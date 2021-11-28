package net.golikov.json.schema.validation.stream.parse;

import java.util.Collections;
import java.util.List;

public class ParseResult<T> {
    private T result = null;
    private List<String> errors = Collections.emptyList();

    public ParseResult() {
    }

    public ParseResult(List<String> errors) {
        this.errors = errors;
    }

    public ParseResult(T result) {
        this.result = result;
    }

    public boolean hasResult() {
        return errors.isEmpty() && result != null;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
