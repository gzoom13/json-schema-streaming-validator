package net.golikov.json.schema.validation.stream.parse;

public class ParseResult<T> {
    private T result = null;

    public ParseResult() {
    }

    public ParseResult(T result) {
        this.result = result;
    }

    public boolean hasResult() {
        return result != null;
    }

}
