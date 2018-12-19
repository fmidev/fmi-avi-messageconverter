package fi.fmi.avi.model.sigmet;

import static java.util.Objects.requireNonNull;


public final class SIGMETBuilderHelper {
    private SIGMETBuilderHelper() {
        throw new UnsupportedOperationException();
    }

    public static void copyFrom(final SIGMET.Builder<?, ?> builder, final SIGMET value) {
        requireNonNull(builder, "builder");
        requireNonNull(value, "value");
    }

    public static void mergeFromTAFForecast(final SIGMET.Builder<?, ?> builder, final SIGMET value) {
        requireNonNull(builder, "builder");
        requireNonNull(value, "value");
    }

}