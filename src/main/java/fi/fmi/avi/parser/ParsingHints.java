package fi.fmi.avi.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * ParsingHints provides lexing, parsing and serializing related
 * implementation hints for aviation weather message processing operations.
 *
 * Note that since these keys and values are <i>hints</i>, there is
 * no requirement that a given implementation supports all possible
 * choices indicated below or that it can respond to requests to
 * modify its functionality.
 *
 * Implementations are free to ignore the hints completely, but should
 * try to use an implementation option that is as close as possible
 * to the request.
 *
 * The keys used to control the hints are all special values that
 * subclass the associated {@link ParsingHints.Key} class.
 *
 * Many common hints are expressed below as static constants in this
 * class, but the list is not meant to be exhaustive.
 * Other hints may be created by other packages by defining new objects
 * which subclass the {@code Key} class and defining the associated values.
 *
 * This class is heavily influenced by {@link java.awt.RenderingHints}
 *
 * @author Ilkka Rinne / Spatineo 2017
 */
public class ParsingHints implements Map<Object, Object>, Cloneable {

    /**
     * Key for explicitly specifying the Aviation weather message type.
     */
    public static final Key KEY_MESSAGE_TYPE;

    /**
     * Message type METAR
     */
    public static final Object VALUE_MESSAGE_TYPE_METAR = "METAR";

    /**
     * Message type TAF
     */
    public static final Object VALUE_MESSAGE_TYPE_TAF = "TAF";

    /**
     * Message type SPECI
     */
    public static final Object VALUE_MESSAGE_TYPE_SPECI = "SPECI";

    /**
     * Message type SIGMET
     */
    public static final Object VALUE_MESSAGE_TYPE_SIGMET = "SIGMET";

    /**
     * Message type AIRMET
     */
    public static final Object VALUE_MESSAGE_TYPE_AIRMET = "AIRMET";

    /**
     * Message type ARS
     */
    public static final Object VALUE_MESSAGE_TYPE_ARS = "ARS";

    /**
     * Key for asking for more strict or loose processing of the provided input
     */
    public static final Key KEY_PARSING_MODE;

    /**
     * Strict mode, no errors or warnings should be ignored and the specification requirements followed meticulously.
     */
    public static final Object VALUE_PARSING_MODE_STRICT = "STRICT";

    /**
     * Operation should allows (some) missing input data for the operation.
     */
    public static final Object VALUE_PARSING_MODE_ALLOW_MISSING = "ALLOW_MISSING";

    /**
     * Operation should allow (some) syntax errors and missing data for the operation.
     */
    public static final Object VALUE_PARSING_MODE_ALLOW_SYNTAX_ERRORS = "ALLOW_SYNTAX_ERRORS";

    /**
     * Operation should allow (some) logical errors and missing data for the operation.
     */
    public static final Object VALUE_PARSING_MODE_ALLOW_LOGICAL_ERRORS = "ALLOW_LOGICAL_ERRORS";

    /**
     * Loose mode, operation should allow as much errors and missing data as possible.
     */
    public static final Object VALUE_PARSING_MODE_ALLOW_ANY_ERRORS = "ALLOW_ANY_ERRORS";

    /**
     * Key for using/preferring the long ((with start and end day number) or short valid time format.
     */
    public static final Key KEY_VALIDTIME_FORMAT;

    /**
     * The operation should prefer/expect the long format even if the short one could be used.
     */
    public static final Object VALUE_VALIDTIME_FORMAT_PREFER_LONG = "PREFER_LONG";

    /**
     * The operation should prefer/expect the short format when the long format is not necessary.
     */
    public static final Object VALUE_VALIDTIME_FORMAT_PREFER_SHORT = "PREFER_SHORT";

    /**
     * Key for preferring/expecting timezone ID (typically 'Z' for UTC) to be used for
     * timestamps.
     */
    public static final Key KEY_TIMEZONE_ID_POLICY;

    /**
     * Prefer/expect the timezone ID to be used for timestamps when ever allowed by the specification, even if mandated by it.
     */
    public static final Object VALUE_TIMEZONE_ID_POLICY_STRICT = "STRICT";

    /**
     * Silently ignore the missing timezone ID or leave it out when allowed, but not required by the specification.
     */
    public static final Object VALUE_TIMEZONE_ID_POLICY_LOOSE = "LOOSE";

    /**
     * A convenience ParsingHints including only the {@link ParsingHints#KEY_MESSAGE_TYPE} with value {@link ParsingHints#VALUE_MESSAGE_TYPE_METAR}
     */
    public static final ParsingHints METAR;

    /**
     * A convenience ParsingHints including only the {@link ParsingHints#KEY_MESSAGE_TYPE} with value {@link ParsingHints#VALUE_MESSAGE_TYPE_TAF}
     */
    public static final ParsingHints TAF;

    /**
     * A convenience ParsingHints including only the {@link ParsingHints#KEY_MESSAGE_TYPE} with value {@link ParsingHints#VALUE_MESSAGE_TYPE_SPECI}
     */
    public static final ParsingHints SPECI;

    /**
     * A convenience ParsingHints including only the {@link ParsingHints#KEY_MESSAGE_TYPE} with value {@link ParsingHints#VALUE_MESSAGE_TYPE_SIGMET}
     */
    public static final ParsingHints SIGMET;

    /**
     * A convenience ParsingHints including only the {@link ParsingHints#KEY_MESSAGE_TYPE} with value {@link ParsingHints#VALUE_MESSAGE_TYPE_ARS}
     */
    public static final ParsingHints ARS;

    /**
     * A convenience parsingHints including only the {@link ParsingHints#KEY_PARSING_MODE} with value {@link ParsingHints#VALUE_PARSING_MODE_STRICT}
     */
    public static final ParsingHints STRICT_PARSING;

    /**
     * A convenience parsingHints including only the {@link ParsingHints#KEY_PARSING_MODE} with value {@link ParsingHints#VALUE_PARSING_MODE_ALLOW_ANY_ERRORS}
     */
    public static final ParsingHints ALLOW_ERRORS;

    static {
        KEY_MESSAGE_TYPE = new KeyImpl(1, "Aviation message type hint", VALUE_MESSAGE_TYPE_METAR, VALUE_MESSAGE_TYPE_SPECI, VALUE_MESSAGE_TYPE_TAF,
                VALUE_MESSAGE_TYPE_SIGMET, VALUE_MESSAGE_TYPE_AIRMET, VALUE_MESSAGE_TYPE_ARS);
        KEY_PARSING_MODE = new KeyImpl(3, "Parsing mode hint", VALUE_PARSING_MODE_STRICT, VALUE_PARSING_MODE_ALLOW_MISSING, VALUE_PARSING_MODE_ALLOW_SYNTAX_ERRORS, VALUE_PARSING_MODE_ALLOW_LOGICAL_ERRORS, VALUE_PARSING_MODE_ALLOW_ANY_ERRORS);
        KEY_VALIDTIME_FORMAT = new KeyImpl(4, "Valid time format preference", VALUE_VALIDTIME_FORMAT_PREFER_SHORT, VALUE_VALIDTIME_FORMAT_PREFER_LONG);
        KEY_TIMEZONE_ID_POLICY = new KeyImpl(5, "Controls whether the UTZ indicator 'Z' is required/created when not strictly mandated",
                VALUE_TIMEZONE_ID_POLICY_LOOSE, VALUE_TIMEZONE_ID_POLICY_STRICT);

        METAR = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_METAR);
        TAF = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_TAF);
        SPECI = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_SPECI);
        SIGMET = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_SIGMET);
        ARS = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_ARS);
        
        STRICT_PARSING = new ParsingHints(KEY_PARSING_MODE, VALUE_PARSING_MODE_STRICT);
        ALLOW_ERRORS = new ParsingHints(KEY_PARSING_MODE, VALUE_PARSING_MODE_ALLOW_ANY_ERRORS);
    }

    /**
     * Key for a specific hint. The {@link ParsingHints#put} methods only accept key-value pairs
     * where the {@link #isCompatibleValue(Object)} returns true for the given value.
     */
    public abstract static class Key {
        private final int key;

        protected Key(final int privateKey) {
            this.key = privateKey;
        }

        /**
         * Check if using the <code>value</code> with this Key makes sense.
         *
         * @param value value to check
         * @return true if the value is one of the allowed ones, false otherwise
         */
        public abstract boolean isCompatibleValue(Object value);

        /**
         * Overridden to force using {@link System#identityHashCode(Object)} for all
         * Key implementations.
         *
         * @return
         */
        public final int hashCode() {
            return System.identityHashCode(this);
        }

        /**
         * Overridden to force using Object (hashCode) equality for all
         * Key implementations.
         * */
        public final boolean equals(final Object other) {
            return this == other;
        }

        protected final int intKey() {
            return this.key;
        }
    }

    private static final class KeyImpl extends Key {
        final String description;
        final Object[] fixedOptions;

        KeyImpl(final int privateKey, final String description, final Object... option) {
            super(privateKey);
            this.description = description;
            this.fixedOptions = option;
        }

        KeyImpl(final int privateKey, final String description) {
            this(privateKey, description, (Object[]) null);
        }

        public boolean isCompatibleValue(final Object value) {
            boolean retval = true;
            if (this.fixedOptions != null) {
                retval = false;
                for (int i = 0; i < this.fixedOptions.length; i++) {
                    if (value == this.fixedOptions[i]) {
                        retval = true;
                        break;
                    }
                }
            }
            return retval;
        }

        public String toString() {
            return this.description;
        }
    }

    private HashMap<Object, Object> hintMap = new HashMap<Object, Object>();

    /**
     * The default constructor, creates an empty ParsingHints.
     */
    public ParsingHints() {
    }

    /**
     * Creates ParsingHints with the given key-value pairs.
     *
     * @param init the map of key-values
     */
    public ParsingHints(final Map<Key, ?> init) {
        if (init != null) {
            putAll(init);
        }
    }

    /**
     * Creates a ParsingHints with only a single key-value pair.
     * @param key
     * @param value
     */
    public ParsingHints(final Key key, final Object value) {
        put(key, value);
    }


    @Override
    public int size() {
        return this.hintMap.size();
    }


    @Override
    public boolean isEmpty() {
        return this.hintMap.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return hintMap.containsKey((Key) key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return hintMap.containsValue(value);
    }

    @Override
    public Object get(final Object key) {
        return this.hintMap.get(key);
    }

    public Object get(final Key key) {
        return this.hintMap.get(key);
    }

    public <T> T get(final Key key, final Class<T> clz) {
        final Object o = this.hintMap.get(key);
        if (o != null) {
            if (clz.isAssignableFrom(o.getClass())) {
                return (T) o;
            } else {
                throw new ClassCastException("Could not return '" + o + "' as " + clz.getCanonicalName());
            }
        } else {
            return null;
        }
    }

    @Override
    public Object put(final Object key, final Object value) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
        if (key instanceof Key) {
            return this.put((Key) key, value);
        } else {
            throw new ClassCastException("Key must be of type " + Key.class.getCanonicalName());
        }
    }

    public Object put(final Key key, final Object value) {
        if (key == null) {
            throw new NullPointerException("Key must not be null");
        }
        if (!key.isCompatibleValue(value)) {
            throw new IllegalArgumentException();
        }
        return hintMap.put(key, value);
    }

    @Override
    public Object remove(final Object key) {
        return hintMap.remove((Key) key);
    }

    @Override
    public void putAll(final Map<?, ?> m) {
        final Iterator<?> iterator = m.keySet().iterator();
        while (iterator.hasNext()) {
            final Key key = (Key) iterator.next();
            if (!key.isCompatibleValue(m.get(key))) {
                throw new IllegalArgumentException();
            }
        }
        hintMap.putAll(m);
    }

    @Override
    public void clear() {
        this.hintMap.clear();
    }

    @Override
    public Set<Object> keySet() {
        return this.hintMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return this.hintMap.values();
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return Collections.unmodifiableSet(hintMap.entrySet());
    }

    @Override
    public boolean equals(final Object o) {
        return this.hintMap.equals(o);
    }

    @Override
    public int hashCode() {
        return this.hintMap.hashCode();
    }

    public Object clone() {
        try {
            final ParsingHints copy = (ParsingHints) super.clone();
            copy.hintMap = new HashMap<Object, Object>(hintMap);
            return copy;
        } catch (final CloneNotSupportedException e) {
            throw (Error) new InternalError().initCause(e);
        }
    }

    public String toString() {
        return this.hintMap.toString();
    }

}
