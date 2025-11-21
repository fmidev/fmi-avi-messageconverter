package fi.fmi.avi.model.swx.amd82;

import java.util.Comparator;

public enum Intensity {
    MODERATE("MOD", 1), SEVERE("SEV", 2);

    private static final Comparator<Intensity> COMPARATOR = Comparator.comparingInt(Intensity::getInternalIntensityWeight);

    private final String code;
    private final int internalIntensityWeight;

    Intensity(final String code, final int internalIntensityWeight) {
        this.code = code;
        this.internalIntensityWeight = internalIntensityWeight;
    }

    public static Comparator<Intensity> comparator() {
        return COMPARATOR;
    }

    public static Intensity fromString(final String code) {
        for (final Intensity intensity : Intensity.values()) {
            if (intensity.getCode().equals(code)) {
                return intensity;
            }
        }
        throw new IllegalArgumentException("Intensity code \" " + code + "\" could not be resolved");
    }

    public String getCode() {
        return this.code;
    }

    private int getInternalIntensityWeight() {
        return internalIntensityWeight;
    }
}
