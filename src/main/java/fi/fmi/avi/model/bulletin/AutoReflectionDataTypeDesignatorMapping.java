package fi.fmi.avi.model.bulletin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class AutoReflectionDataTypeDesignatorMapping<T extends DataTypeDesignator> {

    private final Map<Character, T> codeToDesignator = new HashMap<>();
    private final Map<String, T> nameToDesignator = new HashMap<>();
    private final Map<Character, String> codeToName = new HashMap<>();

    AutoReflectionDataTypeDesignatorMapping(final Class<T> clz) {
        final Field[] fields = clz.getFields();
        for (final Field field : fields) {
            if (clz.isAssignableFrom(field.getType())) {
                try {
                    @SuppressWarnings("unchecked")
                    final T designator = (T) field.get(null);
                    if (designator != null) {
                        final String name = field.getName();
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

    Optional<T> getOptionalDesignatorByCode(final char code) {
        return Optional.ofNullable(codeToDesignator.get(code));
    }

    Optional<T> getOptionalDesignatorByName(final String name) {
        return Optional.ofNullable(nameToDesignator.get(name));
    }

    Optional<String> getOptionalDesignatorName(final char code) {
        return Optional.ofNullable(getNullableDesignatorName(code));
    }

    private String getNullableDesignatorName(final char code) {
        return codeToName.get(code);
    }

    String getDesignatorName(final char code) {
        final String name = getNullableDesignatorName(code);
        if (name == null) {
            throw new IllegalStateException("No name exists for code " + code);
        }
        return name;
    }
}
