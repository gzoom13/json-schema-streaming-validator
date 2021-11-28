package net.golikov.json.schema.validation.stream.parse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public Optional<T> getResult() {
        return Optional.ofNullable(result);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
