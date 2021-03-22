package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.util.function.BiConsumer;

import fi.fmi.avi.model.immutable.AerodromeImpl;

public final class AerodromeWeatherMessageBuilderHelper {
    private AerodromeWeatherMessageBuilderHelper() {
        throw new AssertionError();
    }

    /**
     * Copy properties declared in {@link AerodromeWeatherMessage} from provided {@code value} to {@code builder} using provided setters.
     *
     * <p>
     * This method exists for completeness safety. Whenever the {@link AerodromeWeatherMessage} interface changes, applying changes here will enforce to
     * conform to changes in all builders using this method. This ensures that changes will not get unnoticed in builder classes.
     * </p>
     *
     * @param <T>
     *         type of {@code value}
     * @param <B>
     *         type of {@code builder}
     * @param builder
     *         builder to copy properties to
     * @param value
     *         value object to copy properties from
     * @param setAerodrome
     *         setter for aerodrome
     */
    public static <T extends AerodromeWeatherMessage, B> void copyFrom(final B builder, final T value,  //
            final BiConsumer<B, Aerodrome> setAerodrome) {
        requireNonNull(value, "value");
        requireNonNull(builder, "builder");
        setAerodrome.accept(builder, AerodromeImpl.immutableCopyOf(value.getAerodrome()));
    }

}
