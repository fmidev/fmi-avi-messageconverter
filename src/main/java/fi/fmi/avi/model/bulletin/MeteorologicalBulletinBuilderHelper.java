package fi.fmi.avi.model.bulletin;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public final class MeteorologicalBulletinBuilderHelper {
    private MeteorologicalBulletinBuilderHelper() {
        throw new AssertionError();
    }

    /**
     * Copy properties declared in {@link MeteorologicalBulletin} from provided {@code value} to {@code builder} using provided setters.
     *
     * <p>
     * This method exists for completeness safety. Whenever the {@link MeteorologicalBulletin} interface changes, applying changes here will enforce to
     * conform to changes in all builders using this method. This ensures that changes will not get unnoticed in builder classes.
     * </p>
     *
     * @param <T>                   type of {@code value}
     * @param <B>                   type of {@code builder}
     * @param builder               builder to copy properties to
     * @param value                 value object to copy properties from
     * @param setHeading            setter for heading
     * @param addAllMessages        setter for messages
     * @param toImmutableMessage    function transforming message to immutable
     * @param setTimeStamp          setter for timeStamp
     * @param addAllTimeStampFields setter for timeStampFields
     * @param setCollectIdentifier  setter for collectIdentifier
     */
    public static <T extends MeteorologicalBulletin<M>, M extends AviationWeatherMessage, B> void copyFrom(
            final B builder,
            final T value,
            final BiConsumer<B, BulletinHeading> setHeading,
            final BiConsumer<B, Stream<M>> addAllMessages,
            final Function<M, ? extends M> toImmutableMessage,
            final BiConsumer<B, Optional<ZonedDateTime>> setTimeStamp,
            final BiConsumer<B, Set<ChronoField>> addAllTimeStampFields,
            final BiConsumer<B, Optional<String>> setCollectIdentifier) {
        copyAndTransform(builder, value, setHeading, addAllMessages, toImmutableMessage, setTimeStamp, addAllTimeStampFields, setCollectIdentifier);
    }

    /**
     * Copy properties declared in {@link MeteorologicalBulletin} from provided {@code value} to {@code builder}
     * using provided setters and transformation function for messages.
     *
     * <p>
     * This method allows transforming bulletin messages to another type while copying all other bulletin properties.
     * See {@link #copyFrom(Object, MeteorologicalBulletin, BiConsumer, BiConsumer, Function, BiConsumer, BiConsumer, BiConsumer)}
     * for non-transforming version.
     * </p>
     *
     * @param <T>                   type of {@code value}
     * @param <M>                   type of messages contained in {@code value}
     * @param <I>                   type of transformed messages accepted by {@code addAllMessages}
     * @param <B>                   type of {@code builder}
     * @param builder               builder to copy properties to
     * @param value                 value object to copy properties from
     * @param setHeading            setter for heading
     * @param addAllMessages        setter for messages
     * @param toImmutableMessage    function transforming message to immutable
     * @param setTimeStamp          setter for timeStamp
     * @param addAllTimeStampFields setter for timeStampFields
     * @param setCollectIdentifier  setter for collectIdentifier
     */
    public static <T extends MeteorologicalBulletin<M>, M extends AviationWeatherMessage, I extends AviationWeatherMessage, B> void copyAndTransform(
            final B builder,
            final T value,
            final BiConsumer<B, BulletinHeading> setHeading,
            final BiConsumer<B, Stream<I>> addAllMessages,
            final Function<M, ? extends I> toImmutableMessage,
            final BiConsumer<B, Optional<ZonedDateTime>> setTimeStamp,
            final BiConsumer<B, Set<ChronoField>> addAllTimeStampFields,
            final BiConsumer<B, Optional<String>> setCollectIdentifier) {
        requireNonNull(value, "value");
        requireNonNull(builder, "builder");
        setHeading.accept(builder, BulletinHeadingImpl.immutableCopyOf(value.getHeading()));
        addAllMessages.accept(builder, value.getMessages().stream().map(toImmutableMessage));
        setTimeStamp.accept(builder, value.getTimeStamp());
        addAllTimeStampFields.accept(builder, value.getTimeStampFields());
        setCollectIdentifier.accept(builder, value.getCollectIdentifier());
    }

}
