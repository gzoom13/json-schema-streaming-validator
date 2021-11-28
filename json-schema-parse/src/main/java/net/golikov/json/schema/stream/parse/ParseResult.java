package net.golikov.json.schema.stream.parse;

import java.util.Optional;

public class ParseResult<T> {
    private T result = null;
    private Optional<String> error = Optional.empty();

    public ParseResult() {
    }

    public ParseResult(String error) {
        this.error = Optional.of(error);
    }

    public ParseResult(T result) {
        this.result = result;
    }

    public Optional<T> getResult() {
        return Optional.ofNullable(result);
    }

    public Optional<String> getError() {
        return error;
    }
}
