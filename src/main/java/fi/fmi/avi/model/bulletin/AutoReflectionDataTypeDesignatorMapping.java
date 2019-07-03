package fi.fmi.avi.model.bulletin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AutoReflectionDataTypeDesignatorMapping<T extends DataTypeDesignator> {

    private final Map<Character, T> codeToDesignator = new HashMap<>();
    private final Map<String, T> nameToDesignator = new HashMap<>();
    private final Map<Character, String> codeToName = new HashMap<>();

    protected AutoReflectionDataTypeDesignatorMapping(final Class<T> clz) {
        final Field[] fields = clz.getFields();
        String name;
        T designator;
        for (final Field field:fields) {
            if (clz.isAssignableFrom(field.getType())) {
                try {
                    designator = (T) field.get(null);
                    if (designator != null) {
                        name = field.getName();
                        codeToDesignator.put(designator.code(), designator);
                        nameToDesignator.put(name, designator);
                        codeToName.put(designator.code(), name);
                    }
                } catch (final IllegalAccessException e) {
                    //NO-OP
                }
            }
        }
    }

    protected T getDesignatorByCode(final char code) {
        return codeToDesignator.get(code);
    }

    protected T getDesignatorByName(final String name) {
        return nameToDesignator.get(name);
    }

    protected String getDesignatorName(final char code) {
        return codeToName.get(code);
    }
}
