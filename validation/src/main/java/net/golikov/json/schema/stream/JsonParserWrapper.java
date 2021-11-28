package net.golikov.json.schema.stream;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.RequestPayload;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JsonParserWrapper extends JsonParser implements CurrentToken {

    private final JsonParser delegate;

    public JsonParserWrapper(JsonParser delegate) {
        super(delegate.getFeatureMask());
        this.delegate = delegate;
    }

    /**
     * Accessor for {@link ObjectCodec} associated with this
     * parser, if any. Codec is used by {@link #readValueAs(Class)}
     * method (and its variants).
     *
     * @return Codec assigned to this parser, if any; {@code null} if none
     */
    @Override
    public ObjectCodec getCodec() {
        return delegate.getCodec();
    }

    /**
     * Setter that allows defining {@link ObjectCodec} associated with this
     * parser, if any. Codec is used by {@link #readValueAs(Class)}
     * method (and its variants).
     *
     * @param oc Codec to assign, if any; {@code null} if none
     */
    @Override
    public void setCodec(ObjectCodec oc) {
        delegate.setCodec(oc);
    }

    /**
     * Method that can be used to get access to object that is used
     * to access input being parsed; this is usually either
     * {@link InputStream} or {@link Reader}, depending on what
     * parser was constructed with.
     * Note that returned value may be null in some cases; including
     * case where parser implementation does not want to exposed raw
     * source to caller.
     * In cases where input has been decorated, object returned here
     * is the decorated version; this allows some level of interaction
     * between users of parser and decorator object.
     *<p>
     * In general use of this accessor should be considered as
     * "last effort", i.e. only used if no other mechanism is applicable.
     *
     * @return Input source this parser was configured with
     */
    @Override
    public Object getInputSource() {
        return delegate.getInputSource();
    }

    /**
     * Helper method, usually equivalent to:
     *<code>
     *   getParsingContext().getCurrentValue();
     *</code>
     *<p>
     * Note that "current value" is NOT populated (or used) by Streaming parser;
     * it is only used by higher-level data-binding functionality.
     * The reason it is included here is that it can be stored and accessed hierarchically,
     * and gets passed through data-binding.
     *
     * @return "Current value" associated with the current input context (state) of this parser
     *
     * @since 2.5
     */
    @Override
    public Object getCurrentValue() {
        return delegate.getCurrentValue();
    }

    /**
     * Helper method, usually equivalent to:
     *<code>
     *   getParsingContext().setCurrentValue(v);
     *</code>
     *
     * @param v Current value to assign for the current input context of this parser
     *
     * @since 2.5
     */
    @Override
    public void setCurrentValue(Object v) {
        delegate.setCurrentValue(v);
    }

    /**
     * Sets the payload to be passed if {@link JsonParseException} is thrown.
     *
     * @param payload Payload to pass
     *
     * @since 2.8
     */
    @Override
    public void setRequestPayloadOnError(RequestPayload payload) {
        delegate.setRequestPayloadOnError(payload);
    }

    /**
     * Sets the byte[] request payload and the charset
     *
     * @param payload Payload to pass
     * @param charset Character encoding for (lazily) decoding payload
     *
     * @since 2.8
     */
    @Override
    public void setRequestPayloadOnError(byte[] payload, String charset) {
        delegate.setRequestPayloadOnError(payload, charset);
    }

    /**
     * Sets the String request payload
     *
     * @param payload Payload to pass
     *
     * @since 2.8
     */
    @Override
    public void setRequestPayloadOnError(String payload) {
        delegate.setRequestPayloadOnError(payload);
    }

    /**
     * Method for accessing Schema that this parser uses, if any.
     * Default implementation returns null.
     *
     * @return Schema in use by this parser, if any; {@code null} if none
     *
     * @since 2.1
     */
    @Override
    public FormatSchema getSchema() {
        return delegate.getSchema();
    }

    /**
     * Method to call to make this parser use specified schema. Method must
     * be called before trying to parse any content, right after parser instance
     * has been created.
     * Note that not all parsers support schemas; and those that do usually only
     * accept specific types of schemas: ones defined for data format parser can read.
     *<p>
     * If parser does not support specified schema, {@link UnsupportedOperationException}
     * is thrown.
     *
     * @param schema Schema to use
     *
     * @throws UnsupportedOperationException if parser does not support schema
     */
    @Override
    public void setSchema(FormatSchema schema) {
        delegate.setSchema(schema);
    }

    /**
     * Method that can be used to verify that given schema can be used with
     * this parser (using {@link #setSchema}).
     *
     * @param schema Schema to check
     *
     * @return True if this parser can use given schema; false if not
     */
    @Override
    public boolean canUseSchema(FormatSchema schema) {
        return delegate.canUseSchema(schema);
    }

    /**
     * Method that can be called to determine if a custom
     * {@link ObjectCodec} is needed for binding data parsed
     * using {@link JsonParser} constructed by this factory
     * (which typically also implies the same for serialization
     * with {@link JsonGenerator}).
     *
     * @return True if format-specific codec is needed with this parser; false if a general
     *   {@link ObjectCodec} is enough
     *
     * @since 2.1
     */
    @Override
    public boolean requiresCustomCodec() {
        return delegate.requiresCustomCodec();
    }

    /**
     * Accessor for getting version of the core package, given a parser instance.
     * Left for sub-classes to implement.
     *
     * @return Version of this generator (derived from version declared for
     *   {@code jackson-core} jar that contains the class
     */
    @Override
    public Version version() {
        return delegate.version();
    }

    /**
     * Closes the parser so that no further iteration or data access
     * can be made; will also close the underlying input source
     * if parser either <b>owns</b> the input source, or feature
     * {@link Feature#AUTO_CLOSE_SOURCE} is enabled.
     * Whether parser owns the input source depends on factory
     * method that was used to construct instance (so check
     * {@link JsonFactory} for details,
     * but the general
     * idea is that if caller passes in closable resource (such
     * as {@link InputStream} or {@link Reader}) parser does NOT
     * own the source; but if it passes a reference (such as
     * {@link File} or {@link URL} and creates
     * stream or reader it does own them.
     *
     * @throws IOException if there is either an underlying I/O problem
     */
    @Override
    public void close() throws IOException {
        delegate.close();
    }

    /**
     * Method that can be called to determine whether this parser
     * is closed or not. If it is closed, no new tokens can be
     * retrieved by calling {@link #nextToken} (and the underlying
     * stream may be closed). Closing may be due to an explicit
     * call to {@link #close} or because parser has encountered
     * end of input.
     *
     * @return {@code True} if this parser instance has been closed
     */
    @Override
    public boolean isClosed() {
        return delegate.isClosed();
    }

    /**
     * Method that can be used to access current parsing context reader
     * is in. There are 3 different types: root, array and object contexts,
     * with slightly different available information. Contexts are
     * hierarchically nested, and can be used for example for figuring
     * out part of the input document that correspond to specific
     * array or object (for highlighting purposes, or error reporting).
     * Contexts can also be used for simple xpath-like matching of
     * input, if so desired.
     *
     * @return Stream input context ({@link JsonStreamContext}) associated with this parser
     */
    @Override
    public JsonStreamContext getParsingContext() {
        return delegate.getParsingContext();
    }

    /**
     * Method that return the <b>starting</b> location of the current
     * token; that is, position of the first character from input
     * that starts the current token.
     *<p>
     * Note that the location is not guaranteed to be accurate (although most
     * implementation will try their best): some implementations may only
     * return {@link JsonLocation#NA} due to not having access
     * to input location information (when delegating actual decoding work
     * to other library)
     *
     * @return Starting location of the token parser currently points to
     */
    @Override
    public JsonLocation getTokenLocation() {
        return delegate.getTokenLocation();
    }

    /**
     * Method that returns location of the last processed character;
     * usually for error reporting purposes.
     *<p>
     * Note that the location is not guaranteed to be accurate (although most
     * implementation will try their best): some implementations may only
     * report specific boundary locations (start or end locations of tokens)
     * and others only return {@link JsonLocation#NA} due to not having access
     * to input location information (when delegating actual decoding work
     * to other library)
     *
     * @return Location of the last processed input unit (byte or character)
     */
    @Override
    public JsonLocation getCurrentLocation() {
        return delegate.getCurrentLocation();
    }

    /**
     * Method that can be called to push back any content that
     * has been read but not consumed by the parser. This is usually
     * done after reading all content of interest using parser.
     * Content is released by writing it to given stream if possible;
     * if underlying input is byte-based it can released, if not (char-based)
     * it can not.
     *
     * @param out OutputStream to which buffered, undecoded content is written to
     *
     * @return -1 if the underlying content source is not byte based
     *    (that is, input can not be sent to {@link OutputStream};
     *    otherwise number of bytes released (0 if there was nothing to release)
     *
     * @throws IOException if write to stream threw exception
     */
    @Override
    public int releaseBuffered(OutputStream out) throws IOException {
        return delegate.releaseBuffered(out);
    }

    /**
     * Method that can be called to push back any content that
     * has been read but not consumed by the parser.
     * This is usually
     * done after reading all content of interest using parser.
     * Content is released by writing it to given writer if possible;
     * if underlying input is char-based it can released, if not (byte-based)
     * it can not.
     *
     * @param w Writer to which buffered but unprocessed content is written to
     *
     * @return -1 if the underlying content source is not char-based
     *    (that is, input can not be sent to {@link Writer};
     *    otherwise number of chars released (0 if there was nothing to release)
     *
     * @throws IOException if write using Writer threw exception
     */
    @Override
    public int releaseBuffered(Writer w) throws IOException {
        return delegate.releaseBuffered(w);
    }

    /**
     * Method for enabling specified parser feature
     * (check {@link Feature} for list of features)
     *
     * @param f Feature to enable
     *
     * @return This parser, to allow call chaining
     */
    @Override
    public JsonParser enable(Feature f) {
        return delegate.enable(f);
    }

    /**
     * Method for disabling specified  feature
     * (check {@link Feature} for list of features)
     *
     * @param f Feature to disable
     *
     * @return This parser, to allow call chaining
     */
    @Override
    public JsonParser disable(Feature f) {
        return delegate.disable(f);
    }

    /**
     * Method for enabling or disabling specified feature
     * (check {@link Feature} for list of features)
     *
     * @param f Feature to enable or disable
     * @param state Whether to enable feature ({@code true}) or disable ({@code false})
     *
     * @return This parser, to allow call chaining
     */
    @Override
    public JsonParser configure(Feature f, boolean state) {
        return delegate.configure(f, state);
    }

    /**
     * Method for checking whether specified {@link Feature} is enabled.
     *
     * @param f Feature to check
     *
     * @return {@code True} if feature is enabled; {@code false} otherwise
     */
    @Override
    public boolean isEnabled(Feature f) {
        return delegate.isEnabled(f);
    }

    /**
     * Bulk access method for getting state of all standard {@link Feature}s.
     *
     * @return Bit mask that defines current states of all standard {@link Feature}s.
     *
     * @since 2.3
     */
    @Override
    public int getFeatureMask() {
        return delegate.getFeatureMask();
    }

    /**
     * Bulk set method for (re)setting states of all standard {@link Feature}s
     *
     * @param mask Bit mask that defines set of features to enable
     *
     * @return This parser, to allow call chaining
     *
     * @since 2.3
     * @deprecated Since 2.7, use {@link #overrideStdFeatures(int, int)} instead
     */
    @Override
    @Deprecated
    public JsonParser setFeatureMask(int mask) {
        return delegate.setFeatureMask(mask);
    }

    /**
     * Bulk set method for (re)setting states of features specified by <code>mask</code>.
     * Functionally equivalent to
     *<code>
     *    int oldState = getFeatureMask();
     *    int newState = (oldState &amp; ~mask) | (values &amp; mask);
     *    setFeatureMask(newState);
     *</code>
     * but preferred as this lets caller more efficiently specify actual changes made.
     *
     * @param values Bit mask of set/clear state for features to change
     * @param mask Bit mask of features to change
     *
     * @return This parser, to allow call chaining
     *
     * @since 2.6
     */
    @Override
    public JsonParser overrideStdFeatures(int values, int mask) {
        return delegate.overrideStdFeatures(values, mask);
    }

    /**
     * Bulk access method for getting state of all {@link FormatFeature}s, format-specific
     * on/off configuration settings.
     *
     * @return Bit mask that defines current states of all standard {@link FormatFeature}s.
     *
     * @since 2.6
     */
    @Override
    public int getFormatFeatures() {
        return delegate.getFormatFeatures();
    }

    /**
     * Bulk set method for (re)setting states of {@link FormatFeature}s,
     * by specifying values (set / clear) along with a mask, to determine
     * which features to change, if any.
     *<p>
     * Default implementation will simply throw an exception to indicate that
     * the parser implementation does not support any {@link FormatFeature}s.
     *
     * @param values Bit mask of set/clear state for features to change
     * @param mask Bit mask of features to change
     *
     * @return This parser, to allow call chaining
     *
     * @since 2.6
     */
    @Override
    public JsonParser overrideFormatFeatures(int values, int mask) {
        return delegate.overrideFormatFeatures(values, mask);
    }

    /**
     * Main iteration method, which will advance stream enough
     * to determine type of the next token, if any. If none
     * remaining (stream has no content other than possible
     * white space before ending), null will be returned.
     *
     * @return Next token from the stream, if any found, or null
     *   to indicate end-of-input
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public JsonToken nextToken() throws IOException {
        return delegate.nextToken();
    }

    /**
     * Iteration method that will advance stream enough
     * to determine type of the next token that is a value type
     * (including JSON Array and Object start/end markers).
     * Or put another way, nextToken() will be called once,
     * and if {@link JsonToken#FIELD_NAME} is returned, another
     * time to get the value for the field.
     * Method is most useful for iterating over value entries
     * of JSON objects; field name will still be available
     * by calling {@link #getCurrentName} when parser points to
     * the value.
     *
     * @return Next non-field-name token from the stream, if any found,
     *   or null to indicate end-of-input (or, for non-blocking
     *   parsers, {@link JsonToken#NOT_AVAILABLE} if no tokens were
     *   available yet)
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public JsonToken nextValue() throws IOException {
        return delegate.nextValue();
    }

    /**
     * Method that fetches next token (as if calling {@link #nextToken}) and
     * verifies whether it is {@link JsonToken#FIELD_NAME} with specified name
     * and returns result of that comparison.
     * It is functionally equivalent to:
     *<pre>
     *  return (nextToken() == JsonToken.FIELD_NAME) &amp;&amp; str.getValue().equals(getCurrentName());
     *</pre>
     * but may be faster for parser to verify, and can therefore be used if caller
     * expects to get such a property name from input next.
     *
     * @param str Property name to compare next token to (if next token is
     *   <code>JsonToken.FIELD_NAME</code>)
     *
     * @return {@code True} if parser advanced to {@code JsonToken.FIELD_NAME} with
     *    specified name; {@code false} otherwise (different token or non-matching name)
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public boolean nextFieldName(SerializableString str) throws IOException {
        return delegate.nextFieldName(str);
    }

    /**
     * Method that fetches next token (as if calling {@link #nextToken}) and
     * verifies whether it is {@link JsonToken#FIELD_NAME}; if it is,
     * returns same as {@link #getCurrentName()}, otherwise null.
     *
     * @return Name of the the {@code JsonToken.FIELD_NAME} parser advanced to, if any;
     *   {@code null} if next token is of some other type
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.5
     */
    @Override
    public String nextFieldName() throws IOException {
        return delegate.nextFieldName();
    }

    /**
     * Method that fetches next token (as if calling {@link #nextToken}) and
     * if it is {@link JsonToken#VALUE_STRING} returns contained String value;
     * otherwise returns null.
     * It is functionally equivalent to:
     *<pre>
     *  return (nextToken() == JsonToken.VALUE_STRING) ? getText() : null;
     *</pre>
     * but may be faster for parser to process, and can therefore be used if caller
     * expects to get a String value next from input.
     *
     * @return Text value of the {@code JsonToken.VALUE_STRING} token parser advanced
     *   to; or {@code null} if next token is of some other type
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public String nextTextValue() throws IOException {
        return delegate.nextTextValue();
    }

    /**
     * Method that fetches next token (as if calling {@link #nextToken}) and
     * if it is {@link JsonToken#VALUE_NUMBER_INT} returns 32-bit int value;
     * otherwise returns specified default value
     * It is functionally equivalent to:
     *<pre>
     *  return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getIntValue() : defaultValue;
     *</pre>
     * but may be faster for parser to process, and can therefore be used if caller
     * expects to get an int value next from input.
     *<p>
     * NOTE: value checks are performed similar to {@link #getIntValue()}
     *
     * @param defaultValue Value to return if next token is NOT of type {@code JsonToken.VALUE_NUMBER_INT}
     *
     * @return Integer ({@code int}) value of the {@code JsonToken.VALUE_NUMBER_INT} token parser advanced
     *   to; or {@code defaultValue} if next token is of some other type
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     * @throws InputCoercionException if integer number does not fit in Java {@code int}
     */
    @Override
    public int nextIntValue(int defaultValue) throws IOException {
        return delegate.nextIntValue(defaultValue);
    }

    /**
     * Method that fetches next token (as if calling {@link #nextToken}) and
     * if it is {@link JsonToken#VALUE_NUMBER_INT} returns 64-bit long value;
     * otherwise returns specified default value
     * It is functionally equivalent to:
     *<pre>
     *  return (nextToken() == JsonToken.VALUE_NUMBER_INT) ? getLongValue() : defaultValue;
     *</pre>
     * but may be faster for parser to process, and can therefore be used if caller
     * expects to get a long value next from input.
     *<p>
     * NOTE: value checks are performed similar to {@link #getLongValue()}
     *
     * @param defaultValue Value to return if next token is NOT of type {@code JsonToken.VALUE_NUMBER_INT}
     *
     * @return {@code long} value of the {@code JsonToken.VALUE_NUMBER_INT} token parser advanced
     *   to; or {@code defaultValue} if next token is of some other type
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     * @throws InputCoercionException if integer number does not fit in Java {@code long}
     */
    @Override
    public long nextLongValue(long defaultValue) throws IOException {
        return delegate.nextLongValue(defaultValue);
    }

    /**
     * Method that fetches next token (as if calling {@link #nextToken}) and
     * if it is {@link JsonToken#VALUE_TRUE} or {@link JsonToken#VALUE_FALSE}
     * returns matching Boolean value; otherwise return null.
     * It is functionally equivalent to:
     *<pre>
     *  JsonToken t = nextToken();
     *  if (t == JsonToken.VALUE_TRUE) return Boolean.TRUE;
     *  if (t == JsonToken.VALUE_FALSE) return Boolean.FALSE;
     *  return null;
     *</pre>
     * but may be faster for parser to process, and can therefore be used if caller
     * expects to get a Boolean value next from input.
     *
     * @return {@code Boolean} value of the {@code JsonToken.VALUE_TRUE} or {@code JsonToken.VALUE_FALSE}
     *   token parser advanced to; or {@code null} if next token is of some other type
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public Boolean nextBooleanValue() throws IOException {
        return delegate.nextBooleanValue();
    }

    /**
     * Method that will skip all child tokens of an array or
     * object token that the parser currently points to,
     * iff stream points to
     * {@link JsonToken#START_OBJECT} or {@link JsonToken#START_ARRAY}.
     * If not, it will do nothing.
     * After skipping, stream will point to <b>matching</b>
     * {@link JsonToken#END_OBJECT} or {@link JsonToken#END_ARRAY}
     * (possibly skipping nested pairs of START/END OBJECT/ARRAY tokens
     * as well as value tokens).
     * The idea is that after calling this method, application
     * will call {@link #nextToken} to point to the next
     * available token, if any.
     *
     * @return This parser, to allow call chaining
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public JsonParser skipChildren() throws IOException {
        return delegate.skipChildren();
    }

    /**
     * Method that may be used to force full handling of the current token
     * so that even if lazy processing is enabled, the whole contents are
     * read for possible retrieval. This is usually used to ensure that
     * the token end location is available, as well as token contents
     * (similar to what calling, say {@link #getTextCharacters()}, would
     * achieve).
     *<p>
     * Note that for many dataformat implementations this method
     * will not do anything; this is the default implementation unless
     * overridden by sub-classes.
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.8
     */
    @Override
    public void finishToken() throws IOException {
        delegate.finishToken();
    }

    /**
     * Accessor to find which token parser currently points to, if any;
     * null will be returned if none.
     * If return value is non-null, data associated with the token
     * is available via other accessor methods.
     *
     * @return Type of the token this parser currently points to,
     *   if any: null before any tokens have been read, and
     *   after end-of-input has been encountered, as well as
     *   if the current token has been explicitly cleared.
     *
     * @since 2.8
     */
    @Override
    public JsonToken currentToken() {
        return delegate.currentToken();
    }

    /**
     * Method similar to {@link #getCurrentToken()} but that returns an
     * <code>int</code> instead of {@link JsonToken} (enum value).
     *<p>
     * Use of int directly is typically more efficient on switch statements,
     * so this method may be useful when building low-overhead codecs.
     * Note, however, that effect may not be big enough to matter: make sure
     * to profile performance before deciding to use this method.
     *
     * @since 2.8
     *
     * @return {@code int} matching one of constants from {@link JsonTokenId}.
     */
    @Override
    public int currentTokenId() {
        return delegate.currentTokenId();
    }

    /**
     * Alias for {@link #currentToken()}, may be deprecated sometime after
     * Jackson 2.12 (will be removed from 3.0).
     *
     * @return Type of the token this parser currently points to,
     *   if any: null before any tokens have been read, and
     */
    @Override
    public JsonToken getCurrentToken() {
        return delegate.getCurrentToken();
    }

    /**
     * Alias for {@link #currentTokenId()}.
     *
     * @return {@code int} matching one of constants from {@link JsonTokenId}.
     *
     * @deprecated Since 2.12 use {@link #currentTokenId} instead
     */
    @Override
    @Deprecated
    public int getCurrentTokenId() {
        return delegate.getCurrentTokenId();
    }

    /**
     * Method for checking whether parser currently points to
     * a token (and data for that token is available).
     * Equivalent to check for <code>parser.getCurrentToken() != null</code>.
     *
     * @return True if the parser just returned a valid
     *   token via {@link #nextToken}; false otherwise (parser
     *   was just constructed, encountered end-of-input
     *   and returned null from {@link #nextToken}, or the token
     *   has been consumed)
     */
    @Override
    public boolean hasCurrentToken() {
        return delegate.hasCurrentToken();
    }

    /**
     * Method that is functionally equivalent to:
     *<code>
     *  return currentTokenId() == id
     *</code>
     * but may be more efficiently implemented.
     *<p>
     * Note that no traversal or conversion is performed; so in some
     * cases calling method like {@link #isExpectedStartArrayToken()}
     * is necessary instead.
     *
     * @param id Token id to match (from (@link JsonTokenId})
     *
     * @return {@code True} if the parser current points to specified token
     *
     * @since 2.5
     */
    @Override
    public boolean hasTokenId(int id) {
        return delegate.hasTokenId(id);
    }

    /**
     * Method that is functionally equivalent to:
     *<code>
     *  return currentToken() == t
     *</code>
     * but may be more efficiently implemented.
     *<p>
     * Note that no traversal or conversion is performed; so in some
     * cases calling method like {@link #isExpectedStartArrayToken()}
     * is necessary instead.
     *
     * @param t Token to match
     *
     * @return {@code True} if the parser current points to specified token
     *
     * @since 2.6
     */
    @Override
    public boolean hasToken(JsonToken t) {
        return delegate.hasToken(t);
    }

    /**
     * Specialized accessor that can be used to verify that the current
     * token indicates start array (usually meaning that current token
     * is {@link JsonToken#START_ARRAY}) when start array is expected.
     * For some specialized parsers this can return true for other cases
     * as well; this is usually done to emulate arrays in cases underlying
     * format is ambiguous (XML, for example, has no format-level difference
     * between Objects and Arrays; it just has elements).
     *<p>
     * Default implementation is equivalent to:
     *<pre>
     *   currentToken() == JsonToken.START_ARRAY
     *</pre>
     * but may be overridden by custom parser implementations.
     *
     * @return True if the current token can be considered as a
     *   start-array marker (such {@link JsonToken#START_ARRAY});
     *   {@code false} if not
     */
    @Override
    public boolean isExpectedStartArrayToken() {
        return delegate.isExpectedStartArrayToken();
    }

    /**
     * Similar to {@link #isExpectedStartArrayToken()}, but checks whether stream
     * currently points to {@link JsonToken#START_OBJECT}.
     *
     * @return True if the current token can be considered as a
     *   start-array marker (such {@link JsonToken#START_OBJECT});
     *   {@code false} if not
     *
     * @since 2.5
     */
    @Override
    public boolean isExpectedStartObjectToken() {
        return delegate.isExpectedStartObjectToken();
    }

    /**
     * Method called to "consume" the current token by effectively
     * removing it so that {@link #hasCurrentToken} returns false, and
     * {@link #getCurrentToken} null).
     * Cleared token value can still be accessed by calling
     * {@link #getLastClearedToken} (if absolutely needed), but
     * usually isn't.
     *<p>
     * Method was added to be used by the optional data binder, since
     * it has to be able to consume last token used for binding (so that
     * it will not be used again).
     */
    @Override
    public void clearCurrentToken() {
        delegate.clearCurrentToken();
    }

    /**
     * Method that can be called to get the last token that was
     * cleared using {@link #clearCurrentToken}. This is not necessarily
     * the latest token read.
     * Will return null if no tokens have been cleared,
     * or if parser has been closed.
     *
     * @return Last cleared token, if any; {@code null} otherwise
     */
    @Override
    public JsonToken getLastClearedToken() {
        return delegate.getLastClearedToken();
    }

    /**
     * Method that can be used to change what is considered to be
     * the current (field) name.
     * May be needed to support non-JSON data formats or unusual binding
     * conventions; not needed for typical processing.
     *<p>
     * Note that use of this method should only be done as sort of last
     * resort, as it is a work-around for regular operation.
     *
     * @param name Name to use as the current name; may be null.
     */
    @Override
    public void overrideCurrentName(String name) {
        delegate.overrideCurrentName(name);
    }

    /**
     * See {@link #currentName()}.
     *
     * @return Name of the current field in the parsing context
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public String getCurrentName() throws IOException {
        return delegate.getCurrentName();
    }

    /**
     * Method for accessing textual representation of the current token;
     * if no current token (before first call to {@link #nextToken}, or
     * after encountering end-of-input), returns null.
     * Method can be called for any token type.
     *
     * @return Textual value associated with the current token (one returned
     *   by {@link #nextToken()} or other iteration methods)
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public String getText() throws IOException {
        return delegate.getText();
    }

    /**
     * Method to read the textual representation of the current token in chunks and
     * pass it to the given Writer.
     * Conceptually same as calling:
     *<pre>
     *  writer.write(parser.getText());
     *</pre>
     * but should typically be more efficient as longer content does need to
     * be combined into a single <code>String</code> to return, and write
     * can occur directly from intermediate buffers Jackson uses.
     *
     * @param writer Writer to write textual content to
     *
     * @return The number of characters written to the Writer
     *
     * @throws IOException for low-level read issues or writes using passed
     *   {@code writer}, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.8
     */
    @Override
    public int getText(Writer writer) throws IOException, UnsupportedOperationException {
        return delegate.getText(writer);
    }

    /**
     * Method similar to {@link #getText}, but that will return
     * underlying (unmodifiable) character array that contains
     * textual value, instead of constructing a String object
     * to contain this information.
     * Note, however, that:
     *<ul>
     * <li>Textual contents are not guaranteed to start at
     *   index 0 (rather, call {@link #getTextOffset}) to
     *   know the actual offset
     *  </li>
     * <li>Length of textual contents may be less than the
     *  length of returned buffer: call {@link #getTextLength}
     *  for actual length of returned content.
     *  </li>
     * </ul>
     *<p>
     * Note that caller <b>MUST NOT</b> modify the returned
     * character array in any way -- doing so may corrupt
     * current parser state and render parser instance useless.
     *<p>
     * The only reason to call this method (over {@link #getText})
     * is to avoid construction of a String object (which
     * will make a copy of contents).
     *
     * @return Buffer that contains the current textual value (but not necessarily
     *    at offset 0, and not necessarily until the end of buffer)
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public char[] getTextCharacters() throws IOException {
        return delegate.getTextCharacters();
    }

    /**
     * Accessor used with {@link #getTextCharacters}, to know length
     * of String stored in returned buffer.
     *
     * @return Number of characters within buffer returned
     *   by {@link #getTextCharacters} that are part of
     *   textual content of the current token.
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public int getTextLength() throws IOException {
        return delegate.getTextLength();
    }

    /**
     * Accessor used with {@link #getTextCharacters}, to know offset
     * of the first text content character within buffer.
     *
     * @return Offset of the first character within buffer returned
     *   by {@link #getTextCharacters} that is part of
     *   textual content of the current token.
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public int getTextOffset() throws IOException {
        return delegate.getTextOffset();
    }

    /**
     * Method that can be used to determine whether calling of
     * {@link #getTextCharacters} would be the most efficient
     * way to access textual content for the event parser currently
     * points to.
     *<p>
     * Default implementation simply returns false since only actual
     * implementation class has knowledge of its internal buffering
     * state.
     * Implementations are strongly encouraged to properly override
     * this method, to allow efficient copying of content by other
     * code.
     *
     * @return True if parser currently has character array that can
     *   be efficiently returned via {@link #getTextCharacters}; false
     *   means that it may or may not exist
     */
    @Override
    public boolean hasTextCharacters() {
        return delegate.hasTextCharacters();
    }

    /**
     * Generic number value accessor method that will work for
     * all kinds of numeric values. It will return the optimal
     * (simplest/smallest possible) wrapper object that can
     * express the numeric value just parsed.
     *
     * @return Numeric value of the current token in its most optimal
     *   representation
     *
     * @throws IOException Problem with access: {@link JsonParseException} if
     *    the current token is not numeric, or if decoding of the value fails
     *    (invalid format for numbers); plain {@link IOException} if underlying
     *    content read fails (possible if values are extracted lazily)
     */
    @Override
    public Number getNumberValue() throws IOException {
        return delegate.getNumberValue();
    }

    /**
     * If current token is of type
     * {@link JsonToken#VALUE_NUMBER_INT} or
     * {@link JsonToken#VALUE_NUMBER_FLOAT}, returns
     * one of {@link NumberType} constants; otherwise returns null.
     *
     * @return Type of current number, if parser points to numeric token; {@code null} otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public NumberType getNumberType() throws IOException {
        return delegate.getNumberType();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_INT} and
     * it can be expressed as a value of Java byte primitive type.
     * Note that in addition to "natural" input range of {@code [-128, 127]},
     * this also allows "unsigned 8-bit byte" values {@code [128, 255]}:
     * but for this range value will be translated by truncation, leading
     * to sign change.
     *<p>
     * It can also be called for {@link JsonToken#VALUE_NUMBER_FLOAT};
     * if so, it is equivalent to calling {@link #getDoubleValue}
     * and then casting; except for possible overflow/underflow
     * exception.
     *<p>
     * Note: if the resulting integer value falls outside range of
     * {@code [-128, 255]},
     * a {@link InputCoercionException}
     * will be thrown to indicate numeric overflow/underflow.
     *
     * @return Current number value as {@code byte} (if numeric token within
     *   range of {@code [-128, 255]}); otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public byte getByteValue() throws IOException {
        return delegate.getByteValue();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_INT} and
     * it can be expressed as a value of Java short primitive type.
     * It can also be called for {@link JsonToken#VALUE_NUMBER_FLOAT};
     * if so, it is equivalent to calling {@link #getDoubleValue}
     * and then casting; except for possible overflow/underflow
     * exception.
     *<p>
     * Note: if the resulting integer value falls outside range of
     * Java short, a {@link InputCoercionException}
     * will be thrown to indicate numeric overflow/underflow.
     *
     * @return Current number value as {@code short} (if numeric token within
     *   Java 16-bit signed {@code short} range); otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public short getShortValue() throws IOException {
        return delegate.getShortValue();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_INT} and
     * it can be expressed as a value of Java int primitive type.
     * It can also be called for {@link JsonToken#VALUE_NUMBER_FLOAT};
     * if so, it is equivalent to calling {@link #getDoubleValue}
     * and then casting; except for possible overflow/underflow
     * exception.
     *<p>
     * Note: if the resulting integer value falls outside range of
     * Java {@code int}, a {@link InputCoercionException}
     * may be thrown to indicate numeric overflow/underflow.
     *
     * @return Current number value as {@code int} (if numeric token within
     *   Java 32-bit signed {@code int} range); otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public int getIntValue() throws IOException {
        return delegate.getIntValue();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_INT} and
     * it can be expressed as a Java long primitive type.
     * It can also be called for {@link JsonToken#VALUE_NUMBER_FLOAT};
     * if so, it is equivalent to calling {@link #getDoubleValue}
     * and then casting to int; except for possible overflow/underflow
     * exception.
     *<p>
     * Note: if the token is an integer, but its value falls
     * outside of range of Java long, a {@link InputCoercionException}
     * may be thrown to indicate numeric overflow/underflow.
     *
     * @return Current number value as {@code long} (if numeric token within
     *   Java 32-bit signed {@code long} range); otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public long getLongValue() throws IOException {
        return delegate.getLongValue();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_INT} and
     * it can not be used as a Java long primitive type due to its
     * magnitude.
     * It can also be called for {@link JsonToken#VALUE_NUMBER_FLOAT};
     * if so, it is equivalent to calling {@link #getDecimalValue}
     * and then constructing a {@link BigInteger} from that value.
     *
     * @return Current number value as {@link BigInteger} (if numeric token);
     *     otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        return delegate.getBigIntegerValue();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_FLOAT} and
     * it can be expressed as a Java float primitive type.
     * It can also be called for {@link JsonToken#VALUE_NUMBER_INT};
     * if so, it is equivalent to calling {@link #getLongValue}
     * and then casting; except for possible overflow/underflow
     * exception.
     *<p>
     * Note: if the value falls
     * outside of range of Java float, a {@link InputCoercionException}
     * will be thrown to indicate numeric overflow/underflow.
     *
     * @return Current number value as {@code float} (if numeric token within
     *   Java {@code float} range); otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public float getFloatValue() throws IOException {
        return delegate.getFloatValue();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_FLOAT} and
     * it can be expressed as a Java double primitive type.
     * It can also be called for {@link JsonToken#VALUE_NUMBER_INT};
     * if so, it is equivalent to calling {@link #getLongValue}
     * and then casting; except for possible overflow/underflow
     * exception.
     *<p>
     * Note: if the value falls
     * outside of range of Java double, a {@link InputCoercionException}
     * will be thrown to indicate numeric overflow/underflow.
     *
     * @return Current number value as {@code double} (if numeric token within
     *   Java {@code double} range); otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public double getDoubleValue() throws IOException {
        return delegate.getDoubleValue();
    }

    /**
     * Numeric accessor that can be called when the current
     * token is of type {@link JsonToken#VALUE_NUMBER_FLOAT} or
     * {@link JsonToken#VALUE_NUMBER_INT}. No under/overflow exceptions
     * are ever thrown.
     *
     * @return Current number value as {@link BigDecimal} (if numeric token);
     *   otherwise exception thrown
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public BigDecimal getDecimalValue() throws IOException {
        return delegate.getDecimalValue();
    }

    /**
     * Convenience accessor that can be called when the current
     * token is {@link JsonToken#VALUE_TRUE} or
     * {@link JsonToken#VALUE_FALSE}, to return matching {@code boolean}
     * value.
     * If the current token is of some other type, {@link JsonParseException}
     * will be thrown
     *
     * @return {@code True} if current token is {@code JsonToken.VALUE_TRUE},
     *   {@code false} if current token is {@code JsonToken.VALUE_FALSE};
     *   otherwise throws {@link JsonParseException}
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public boolean getBooleanValue() throws IOException {
        return delegate.getBooleanValue();
    }

    /**
     * Accessor that can be called if (and only if) the current token
     * is {@link JsonToken#VALUE_EMBEDDED_OBJECT}. For other token types,
     * null is returned.
     *<p>
     * Note: only some specialized parser implementations support
     * embedding of objects (usually ones that are facades on top
     * of non-streaming sources, such as object trees). One exception
     * is access to binary content (whether via base64 encoding or not)
     * which typically is accessible using this method, as well as
     * {@link #getBinaryValue()}.
     *
     * @return Embedded value (usually of "native" type supported by format)
     *   for the current token, if any; {@code null otherwise}
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public Object getEmbeddedObject() throws IOException {
        return delegate.getEmbeddedObject();
    }

    /**
     * Method that can be used to read (and consume -- results
     * may not be accessible using other methods after the call)
     * base64-encoded binary data
     * included in the current textual JSON value.
     * It works similar to getting String value via {@link #getText}
     * and decoding result (except for decoding part),
     * but should be significantly more performant.
     *<p>
     * Note that non-decoded textual contents of the current token
     * are not guaranteed to be accessible after this method
     * is called. Current implementation, for example, clears up
     * textual content during decoding.
     * Decoded binary content, however, will be retained until
     * parser is advanced to the next event.
     *
     * @param bv Expected variant of base64 encoded
     *   content (see {@link Base64Variants} for definitions
     *   of "standard" variants).
     *
     * @return Decoded binary data
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public byte[] getBinaryValue(Base64Variant bv) throws IOException {
        return delegate.getBinaryValue(bv);
    }

    /**
     * Convenience alternative to {@link #getBinaryValue(Base64Variant)}
     * that defaults to using
     * {@link Base64Variants#getDefaultVariant} as the default encoding.
     *
     * @return Decoded binary data
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public byte[] getBinaryValue() throws IOException {
        return delegate.getBinaryValue();
    }

    /**
     * Method that can be used as an alternative to {@link #getBigIntegerValue()},
     * especially when value can be large. The main difference (beyond method
     * of returning content using {@link OutputStream} instead of as byte array)
     * is that content will NOT remain accessible after method returns: any content
     * processed will be consumed and is not buffered in any way. If caller needs
     * buffering, it has to implement it.
     *
     * @param out Output stream to use for passing decoded binary data
     *
     * @return Number of bytes that were decoded and written via {@link OutputStream}
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.1
     */
    @Override
    public int readBinaryValue(OutputStream out) throws IOException {
        return delegate.readBinaryValue(out);
    }

    /**
     * Similar to {@link #readBinaryValue(OutputStream)} but allows explicitly
     * specifying base64 variant to use.
     *
     * @param bv base64 variant to use
     * @param out Output stream to use for passing decoded binary data
     *
     * @return Number of bytes that were decoded and written via {@link OutputStream}
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.1
     */
    @Override
    public int readBinaryValue(Base64Variant bv, OutputStream out) throws IOException {
        return delegate.readBinaryValue(bv, out);
    }

    /**
     * Method that will try to convert value of current token to a
     * Java {@code int} value.
     * Numbers are coerced using default Java rules; booleans convert to 0 (false)
     * and 1 (true), and Strings are parsed using default Java language integer
     * parsing rules.
     *<p>
     * If representation can not be converted to an int (including structured type
     * markers like start/end Object/Array)
     * default value of <b>0</b> will be returned; no exceptions are thrown.
     *
     * @return {@code int} value current token is converted to, if possible; exception thrown
     *    otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public int getValueAsInt() throws IOException {
        return delegate.getValueAsInt();
    }

    /**
     * Method that will try to convert value of current token to a
     * <b>int</b>.
     * Numbers are coerced using default Java rules; booleans convert to 0 (false)
     * and 1 (true), and Strings are parsed using default Java language integer
     * parsing rules.
     *<p>
     * If representation can not be converted to an int (including structured type
     * markers like start/end Object/Array)
     * specified <b>def</b> will be returned; no exceptions are thrown.
     *
     * @param def Default value to return if conversion to {@code int} is not possible
     *
     * @return {@code int} value current token is converted to, if possible; {@code def} otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public int getValueAsInt(int def) throws IOException {
        return delegate.getValueAsInt(def);
    }

    /**
     * Method that will try to convert value of current token to a
     * <b>long</b>.
     * Numbers are coerced using default Java rules; booleans convert to 0 (false)
     * and 1 (true), and Strings are parsed using default Java language integer
     * parsing rules.
     *<p>
     * If representation can not be converted to a long (including structured type
     * markers like start/end Object/Array)
     * default value of <b>0L</b> will be returned; no exceptions are thrown.
     *
     * @return {@code long} value current token is converted to, if possible; exception thrown
     *    otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public long getValueAsLong() throws IOException {
        return delegate.getValueAsLong();
    }

    /**
     * Method that will try to convert value of current token to a
     * <b>long</b>.
     * Numbers are coerced using default Java rules; booleans convert to 0 (false)
     * and 1 (true), and Strings are parsed using default Java language integer
     * parsing rules.
     *<p>
     * If representation can not be converted to a long (including structured type
     * markers like start/end Object/Array)
     * specified <b>def</b> will be returned; no exceptions are thrown.
     *
     * @param def Default value to return if conversion to {@code long} is not possible
     *
     * @return {@code long} value current token is converted to, if possible; {@code def} otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public long getValueAsLong(long def) throws IOException {
        return delegate.getValueAsLong(def);
    }

    /**
     * Method that will try to convert value of current token to a Java
     * <b>double</b>.
     * Numbers are coerced using default Java rules; booleans convert to 0.0 (false)
     * and 1.0 (true), and Strings are parsed using default Java language floating
     * point parsing rules.
     *<p>
     * If representation can not be converted to a double (including structured types
     * like Objects and Arrays),
     * default value of <b>0.0</b> will be returned; no exceptions are thrown.
     *
     * @return {@code double} value current token is converted to, if possible; exception thrown
     *    otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public double getValueAsDouble() throws IOException {
        return delegate.getValueAsDouble();
    }

    /**
     * Method that will try to convert value of current token to a
     * Java <b>double</b>.
     * Numbers are coerced using default Java rules; booleans convert to 0.0 (false)
     * and 1.0 (true), and Strings are parsed using default Java language floating
     * point parsing rules.
     *<p>
     * If representation can not be converted to a double (including structured types
     * like Objects and Arrays),
     * specified <b>def</b> will be returned; no exceptions are thrown.
     *
     * @param def Default value to return if conversion to {@code double} is not possible
     *
     * @return {@code double} value current token is converted to, if possible; {@code def} otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public double getValueAsDouble(double def) throws IOException {
        return delegate.getValueAsDouble(def);
    }

    /**
     * Method that will try to convert value of current token to a
     * <b>boolean</b>.
     * JSON booleans map naturally; integer numbers other than 0 map to true, and
     * 0 maps to false
     * and Strings 'true' and 'false' map to corresponding values.
     *<p>
     * If representation can not be converted to a boolean value (including structured types
     * like Objects and Arrays),
     * default value of <b>false</b> will be returned; no exceptions are thrown.
     *
     * @return {@code boolean} value current token is converted to, if possible; exception thrown
     *    otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public boolean getValueAsBoolean() throws IOException {
        return delegate.getValueAsBoolean();
    }

    /**
     * Method that will try to convert value of current token to a
     * <b>boolean</b>.
     * JSON booleans map naturally; integer numbers other than 0 map to true, and
     * 0 maps to false
     * and Strings 'true' and 'false' map to corresponding values.
     *<p>
     * If representation can not be converted to a boolean value (including structured types
     * like Objects and Arrays),
     * specified <b>def</b> will be returned; no exceptions are thrown.
     *
     * @param def Default value to return if conversion to {@code boolean} is not possible
     *
     * @return {@code boolean} value current token is converted to, if possible; {@code def} otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     */
    @Override
    public boolean getValueAsBoolean(boolean def) throws IOException {
        return delegate.getValueAsBoolean(def);
    }

    /**
     * Method that will try to convert value of current token to a
     * {@link String}.
     * JSON Strings map naturally; scalar values get converted to
     * their textual representation.
     * If representation can not be converted to a String value (including structured types
     * like Objects and Arrays and null token), default value of
     * <b>null</b> will be returned; no exceptions are thrown.
     *
     * @return {@link String} value current token is converted to, if possible; {@code null} otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.1
     */
    @Override
    public String getValueAsString() throws IOException {
        return delegate.getValueAsString();
    }

    /**
     * Method that will try to convert value of current token to a
     * {@link String}.
     * JSON Strings map naturally; scalar values get converted to
     * their textual representation.
     * If representation can not be converted to a String value (including structured types
     * like Objects and Arrays and null token), specified default value
     * will be returned; no exceptions are thrown.
     *
     * @param def Default value to return if conversion to {@code String} is not possible
     *
     * @return {@link String} value current token is converted to, if possible; {@code def} otherwise
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.1
     */
    @Override
    public String getValueAsString(String def) throws IOException {
        return delegate.getValueAsString(def);
    }

    /**
     * Introspection method that may be called to see if the underlying
     * data format supports some kind of Object Ids natively (many do not;
     * for example, JSON doesn't).
     *<p>
     * Default implementation returns true; overridden by data formats
     * that do support native Object Ids. Caller is expected to either
     * use a non-native notation (explicit property or such), or fail,
     * in case it can not use native object ids.
     *
     * @return {@code True} if the format being read supports native Object Ids;
     *    {@code false} if not
     *
     * @since 2.3
     */
    @Override
    public boolean canReadObjectId() {
        return delegate.canReadObjectId();
    }

    /**
     * Introspection method that may be called to see if the underlying
     * data format supports some kind of Type Ids natively (many do not;
     * for example, JSON doesn't).
     *<p>
     * Default implementation returns true; overridden by data formats
     * that do support native Type Ids. Caller is expected to either
     * use a non-native notation (explicit property or such), or fail,
     * in case it can not use native type ids.
     *
     * @return {@code True} if the format being read supports native Type Ids;
     *    {@code false} if not
     *
     * @since 2.3
     */
    @Override
    public boolean canReadTypeId() {
        return delegate.canReadTypeId();
    }

    /**
     * Method that can be called to check whether current token
     * (one that was just read) has an associated Object id, and if
     * so, return it.
     * Note that while typically caller should check with {@link #canReadObjectId}
     * first, it is not illegal to call this method even if that method returns
     * true; but if so, it will return null. This may be used to simplify calling
     * code.
     *<p>
     * Default implementation will simply return null.
     *
     * @return Native Object id associated with the current token, if any; {@code null} if none
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.3
     */
    @Override
    public Object getObjectId() throws IOException {
        return delegate.getObjectId();
    }

    /**
     * Method that can be called to check whether current token
     * (one that was just read) has an associated type id, and if
     * so, return it.
     * Note that while typically caller should check with {@link #canReadTypeId}
     * first, it is not illegal to call this method even if that method returns
     * true; but if so, it will return null. This may be used to simplify calling
     * code.
     *<p>
     * Default implementation will simply return null.
     *
     * @return Native Type Id associated with the current token, if any; {@code null} if none
     *
     * @throws IOException for low-level read issues, or
     *   {@link JsonParseException} for decoding problems
     *
     * @since 2.3
     */
    @Override
    public Object getTypeId() throws IOException {
        return delegate.getTypeId();
    }

    /**
     * Method to deserialize JSON content into a non-container
     * type (it can be an array type, however): typically a bean, array
     * or a wrapper type (like {@link Boolean}).
     * <b>Note</b>: method can only be called if the parser has
     * an object codec assigned; this is true for parsers constructed
     * by <code>MappingJsonFactory</code> (from "jackson-databind" jar)
     * but not for {@link JsonFactory} (unless its <code>setCodec</code>
     * method has been explicitly called).
     *<p>
     * This method may advance the event stream, for structured types
     * the current token will be the closing end marker (END_ARRAY,
     * END_OBJECT) of the bound structure. For non-structured Json types
     * (and for {@link JsonToken#VALUE_EMBEDDED_OBJECT})
     * stream is not advanced.
     *<p>
     * Note: this method should NOT be used if the result type is a
     * container ({@link Collection} or {@link Map}.
     * The reason is that due to type erasure, key and value types
     * can not be introspected when using this method.
     *
     * @param valueType Java type to read content as (passed to ObjectCodec that
     *    deserializes content)
     *
     * @return Java value read from content
     *
     * @throws IOException if there is either an underlying I/O problem or decoding
     *    issue at format layer
     */
    @Override
    public <T> T readValueAs(Class<T> valueType) throws IOException {
        return delegate.readValueAs(valueType);
    }

    /**
     * Method to deserialize JSON content into a Java type, reference
     * to which is passed as argument. Type is passed using so-called
     * "super type token"
     * and specifically needs to be used if the root type is a
     * parameterized (generic) container type.
     * <b>Note</b>: method can only be called if the parser has
     * an object codec assigned; this is true for parsers constructed
     * by <code>MappingJsonFactory</code> (defined in 'jackson-databind' bundle)
     * but not for {@link JsonFactory} (unless its <code>setCodec</code>
     * method has been explicitly called).
     *<p>
     * This method may advance the event stream, for structured types
     * the current token will be the closing end marker (END_ARRAY,
     * END_OBJECT) of the bound structure. For non-structured Json types
     * (and for {@link JsonToken#VALUE_EMBEDDED_OBJECT})
     * stream is not advanced.
     *
     * @param valueTypeRef Java type to read content as (passed to ObjectCodec that
     *    deserializes content)
     *
     * @return Java value read from content
     *
     * @throws IOException if there is either an underlying I/O problem or decoding
     *    issue at format layer
     */
    @Override
    public <T> T readValueAs(TypeReference<?> valueTypeRef) throws IOException {
        return delegate.readValueAs(valueTypeRef);
    }

    /**
     * Method for reading sequence of Objects from parser stream,
     * all with same specified value type.
     *
     * @param valueType Java type to read content as (passed to ObjectCodec that
     *    deserializes content)
     *
     * @return Iterator for reading multiple Java values from content
     *
     * @throws IOException if there is either an underlying I/O problem or decoding
     *    issue at format layer
     */
    @Override
    public <T> Iterator<T> readValuesAs(Class<T> valueType) throws IOException {
        return delegate.readValuesAs(valueType);
    }

    /**
     * Method to deserialize JSON content into equivalent "tree model",
     * represented by root {@link TreeNode} of resulting model.
     * For JSON Arrays it will an array node (with child nodes),
     * for objects object node (with child nodes), and for other types
     * matching leaf node type. Empty or whitespace documents are null.
     *
     * @return root of the document, or null if empty or whitespace.
     *
     * @throws IOException if there is either an underlying I/O problem or decoding
     *    issue at format layer
     */
    @Override
    public <T extends TreeNode> T readValueAsTree() throws IOException {
        return delegate.readValueAsTree();
    }


}
