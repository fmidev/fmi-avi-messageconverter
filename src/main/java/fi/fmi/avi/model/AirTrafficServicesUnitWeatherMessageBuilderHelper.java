package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.util.function.BiConsumer;

import fi.fmi.avi.model.immutable.UnitPropertyGroupImpl;

public final class AirTrafficServicesUnitWeatherMessageBuilderHelper {
    private AirTrafficServicesUnitWeatherMessageBuilderHelper() {
        throw new AssertionError();
    }

    /**
     * Copy properties declared in {@link AirTrafficServicesUnitWeatherMessage} from provided {@code value} to {@code builder} using provided setters.
     *
     * <p>
     * This method exists for completeness safety. Whenever the {@link AirTrafficServicesUnitWeatherMessage} interface changes, applying changes here will enforce to
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
     * @param setIssuingAirTrafficServicesUnit
     *         setter for issuingAirTrafficServicesUnit
     * @param setMeteorologicalWatchOffice
     *         setter for meteorologicalWatchOffice
     */
    public static <T extends AirTrafficServicesUnitWeatherMessage, B> void copyFrom(final B builder, final T value,  //
            final BiConsumer<B, UnitPropertyGroup> setIssuingAirTrafficServicesUnit, //
            final BiConsumer<B, UnitPropertyGroup> setMeteorologicalWatchOffice) {
        requireNonNull(value, "value");
        requireNonNull(builder, "builder");
        setIssuingAirTrafficServicesUnit.accept(builder, UnitPropertyGroupImpl.immutableCopyOf(value.getIssuingAirTrafficServicesUnit()));
        setMeteorologicalWatchOffice.accept(builder, UnitPropertyGroupImpl.immutableCopyOf(value.getMeteorologicalWatchOffice()));
    }
}
