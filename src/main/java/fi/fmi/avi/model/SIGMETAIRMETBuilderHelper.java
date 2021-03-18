package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.util.function.BiConsumer;

import fi.fmi.avi.model.immutable.AirspaceImpl;

public final class SIGMETAIRMETBuilderHelper {
    private SIGMETAIRMETBuilderHelper() {
        throw new AssertionError();
    }

    /**
     * Copy properties declared in {@link SIGMETAIRMET} from provided {@code value} to {@code builder} using provided setters.
     *
     * <p>
     * This method exists for completeness safety. Whenever the {@link SIGMETAIRMET} interface changes, applying changes here will enforce to
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
     * @param setSequenceNumber
     *         setter for sequenceNumber
     * @param setValidityPeriod
     *         setter for validityPeriod
     * @param setAirspace
     *         setter for airspace
     * @param setStatus
     *         setter for status
     */
    public static <T extends SIGMETAIRMET, B> void copyFrom(final B builder, final T value,  //
            final BiConsumer<B, String> setSequenceNumber, //
            final BiConsumer<B, PartialOrCompleteTimePeriod> setValidityPeriod, //
            final BiConsumer<B, Airspace> setAirspace, //
            final BiConsumer<B, AviationCodeListUser.SigmetAirmetReportStatus> setStatus) {
        requireNonNull(value, "value");
        requireNonNull(builder, "builder");
        setSequenceNumber.accept(builder, value.getSequenceNumber());
        setValidityPeriod.accept(builder, value.getValidityPeriod());
        setAirspace.accept(builder, AirspaceImpl.immutableCopyOf(value.getAirspace()));
        setStatus.accept(builder, value.getStatus());
    }
}
