package fi.fmi.avi.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by rinne on 19/12/16.
 *
 * Heavily influenced by java.awt.RenderingHints
 */
public class ParsingHints implements Map<Object, Object>, Cloneable {

    public static final Key KEY_MESSAGE_TYPE;
    public static final Key KEY_REFERENCE_DATA;
    public static final Key KEY_PARSING_MODE;
    public static final Key KEY_VALIDTIME_FORMAT;

    public static final Object VALUE_MESSAGE_TYPE_METAR = "METAR";
    public static final Object VALUE_MESSAGE_TYPE_TAF = "TAF";
    public static final Object VALUE_MESSAGE_TYPE_SPECI = "SPECI";
    public static final Object VALUE_MESSAGE_TYPE_SIGMET = "SIGMET";
    public static final Object VALUE_MESSAGE_TYPE_AIRMET = "AIRMET";
    public static final Object VALUE_MESSAGE_TYPE_ARS = "ARS";

    public static final Object VALUE_PARSING_MODE_STRICT = "STRICT";
    public static final Object VALUE_PARSING_MODE_ALLOW_MISSING = "ALLOW_MISSING";
    public static final Object VALUE_PARSING_MODE_ALLOW_SYNTAX_ERRORS = "ALLOW_SYNTAX_ERRORS";
    public static final Object VALUE_PARSING_MODE_ALLOW_LOGICAL_ERRORS = "ALLOW_LOGICAL_ERRORS";
    public static final Object VALUE_PARSING_MODE_ALLOW_ANY_ERRORS = "ALLOW_ANY_ERRORS";
    
    public static final Object VALUE_VALIDTIME_FORMAT_PREFER_LONG = "PREFER_LONG";
    public static final Object VALUE_VALIDTIME_FORMAT_PREFER_SHORT = "PREFER_SHORT";
    
    public static final ParsingHints METAR;
    public static final ParsingHints TAF;
    public static final ParsingHints SPECI;
    public static final ParsingHints SIGMET;
    public static final ParsingHints ARS;
    
    public static final ParsingHints STRICT_PARSING;
    public static final ParsingHints ALLOW_ERRORS;

    static {
        KEY_MESSAGE_TYPE = new KeyImpl(1, "Aviation message type hint", VALUE_MESSAGE_TYPE_METAR, VALUE_MESSAGE_TYPE_SPECI, VALUE_MESSAGE_TYPE_TAF,
                VALUE_MESSAGE_TYPE_SIGMET, VALUE_MESSAGE_TYPE_AIRMET, VALUE_MESSAGE_TYPE_ARS);
        KEY_REFERENCE_DATA = new KeyImpl(2, "Java object providing extra information for parsing / serializing");
        KEY_PARSING_MODE = new KeyImpl(3, "Parsing mode hint", VALUE_PARSING_MODE_STRICT, VALUE_PARSING_MODE_ALLOW_MISSING, VALUE_PARSING_MODE_ALLOW_SYNTAX_ERRORS, VALUE_PARSING_MODE_ALLOW_LOGICAL_ERRORS, VALUE_PARSING_MODE_ALLOW_ANY_ERRORS);
        KEY_VALIDTIME_FORMAT = new KeyImpl(4, "Valid time format preference", VALUE_VALIDTIME_FORMAT_PREFER_SHORT, VALUE_VALIDTIME_FORMAT_PREFER_LONG);
        
        METAR = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_METAR);
        TAF = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_TAF);
        SPECI = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_SPECI);
        SIGMET = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_SIGMET);
        ARS = new ParsingHints(KEY_MESSAGE_TYPE, VALUE_MESSAGE_TYPE_ARS);
        
        STRICT_PARSING = new ParsingHints(KEY_PARSING_MODE, VALUE_PARSING_MODE_STRICT);
        ALLOW_ERRORS = new ParsingHints(KEY_PARSING_MODE, VALUE_PARSING_MODE_ALLOW_ANY_ERRORS);
    }

    public abstract static class Key {
        private final int key;

        protected Key(int privateKey) {
            this.key = privateKey;
        }

        public abstract boolean isCompatibleValue(Object value);

        public int hashCode() {
            return System.identityHashCode(this);
        }

        public final boolean equals(Object other) {
            return this == other;
        }

        protected final int intKey() {
            return this.key;
        }
    }

    private static final class KeyImpl extends Key {
        final String description;
        final Object[] fixedOptions;

        KeyImpl(int privateKey, String description, Object... option) {
            super(privateKey);
            this.description = description;
            this.fixedOptions = option;
        }

        KeyImpl(int privateKey, String description) {
            this(privateKey, description, (Object[]) null);
        }
        public boolean isCompatibleValue(Object value) {
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

    public ParsingHints(Map<Key, ?> init) {
        if (init != null) {
            putAll(init);
        }
    }

    public ParsingHints(Key key, Object value) {
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

    public <T> T get(final Key key, Class<T> clz) {
        Object o = this.hintMap.get(key);
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
        Iterator<?> iterator = m.keySet().iterator();
        while (iterator.hasNext()) {
            Key key = (Key) iterator.next();
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
    public boolean equals(Object o) {
        return this.hintMap.equals(o);
    }

    @Override
    public int hashCode() {
        return this.hintMap.hashCode();
    }

    public Object clone() {
        try {
            ParsingHints copy = (ParsingHints) super.clone();
            copy.hintMap = new HashMap<Object, Object>(hintMap);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw (Error) new InternalError().initCause(e);
        }
    }

    public String toString() {
        return this.hintMap.toString();
    }

}
