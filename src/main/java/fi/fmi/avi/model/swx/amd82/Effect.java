package fi.fmi.avi.model.swx.amd82;

public enum Effect {
    GNSS_BASED_NAVIGATION_AND_SURVEILLANCE("GNSS"), //
    HF_COMMUNICATIONS("HF_COM"), //
    RADIATION_AT_FLIGHT_LEVELS("RADIATION"), //
    COMMUNICATIONS_VIA_SATELLITE("SATCOM");

    private final String code;

    Effect(final String code) {
        this.code = code;
    }

    public static Effect fromString(final String code) {
        for (final Effect effect : Effect.values()) {
            if (effect.getCode().equals(code)) {
                return effect;
            }
        }
        throw new IllegalArgumentException("Effect code \" " + code + "\" could not be resolved");
    }

    public String getCode() {
        return this.code;
    }
}
