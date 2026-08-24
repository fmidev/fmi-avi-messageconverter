package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialDateTime.PartialField;

/**
 * Created by rinne on 04/04/2018.
 */
@Value.Immutable
@Value.Style(init = "set*", get = { "is*", "get*" },
        passAnnotations = { com.fasterxml.jackson.databind.annotation.JsonDeserialize.class, com.fasterxml.jackson.annotation.JsonProperty.class },
        typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
@JsonDeserialize(builder = PartialOrCompleteTimePeriod.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "startTime", "endTime" })
public abstract class PartialOrCompleteTimePeriod extends PartialOrCompleteTime {

    private static final Pattern DAY_HOUR_HOUR_PATTERN = Pattern.compile("^(?<day>[0-9]{2})(?<startHour>[0-9]{2})(?<endHour>[0-9]{2})$");
    private static final Pattern DAY_HOUR_DAY_HOUR_PATTERN = Pattern.compile(
            "^(?<startDay>[0-9]{2})(?<startHour>[0-9]{2})[/-](?<endDay>[0-9]{2})(?<endHour>[0-9]{2})$");
    private static final long serialVersionUID = 875078230227696812L;

    public static Builder builder() {
        return new Builder();
    }

    public static PartialOrCompleteTimePeriod createValidityTimeDHDH(final String partialTimePeriod) throws IllegalArgumentException {
        final Matcher matcher = DAY_HOUR_DAY_HOUR_PATTERN.matcher(partialTimePeriod);
        if (matcher.matches()) {
            return PartialOrCompleteTimePeriod.builder()//
                    .setStartTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.startDay.intValue(matcher), TimePatternGroup.startHour.intValue(matcher))))//
                    .setEndTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.endDay.intValue(matcher), TimePatternGroup.endHour.intValue(matcher))))//
                    .build();
        } else {
            throw new IllegalArgumentException("time period does not match pattern " + DAY_HOUR_DAY_HOUR_PATTERN);
        }
    }

    public static PartialOrCompleteTimePeriod createValidityTimeDHH(final String partialTimePeriod) throws IllegalArgumentException {
        final Matcher matcher = DAY_HOUR_HOUR_PATTERN.matcher(partialTimePeriod);
        if (matcher.matches()) {
            return PartialOrCompleteTimePeriod.builder()//
                    .setStartTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.day.intValue(matcher), TimePatternGroup.startHour.intValue(matcher))))//
                    .setEndTime(PartialOrCompleteTimeInstant.of(
                            PartialDateTime.ofDayHour(TimePatternGroup.day.intValue(matcher), TimePatternGroup.endHour.intValue(matcher))))//
                    .build();
        } else {
            throw new IllegalArgumentException("time period does not match pattern " + DAY_HOUR_HOUR_PATTERN);
        }
    }

    public static PartialOrCompleteTimePeriod createValidityTime(final String partialTimePeriod) throws IllegalArgumentException {
        try {
            return createValidityTimeDHDH(partialTimePeriod);
        } catch (final IllegalArgumentException iae) {
            return createValidityTimeDHH(partialTimePeriod);
        }
    }

    public abstract Optional<PartialOrCompleteTimeInstant> getStartTime();

    public abstract Optional<PartialOrCompleteTimeInstant> getEndTime();

    public Builder toBuilder() {
        return new Builder().mergeFrom(this);
    }

    /**
     * Indicates whether present startTime and/or endTime are complete. Empty startTime or endTime is considered as complete.
     *
     * @return {@code true} if present times are complete, {@code false} otherwise
     */
    @JsonIgnore
    public boolean isComplete() {
        final Optional<PartialOrCompleteTimeInstant> start = getStartTime();
        final Optional<PartialOrCompleteTimeInstant> end = getEndTime();
        if (start.isPresent() && !start.get().getCompleteTime().isPresent()) {
            return false;
        }
        return end.map(partialOrCompleteTimeInstant -> partialOrCompleteTimeInstant.getCompleteTime().isPresent()).orElse(true);
    }

    /**
     * Indicates whether both startTime and endTime are present and contain completeTime.
     *
     * @return {@code true} if both startTime and endTime are present and contain completeTime, {@code false} otherwise
     */
    @JsonIgnore
    public boolean isCompleteStrict() {
        return getStartTime().flatMap(PartialOrCompleteTimeInstant::getCompleteTime).isPresent() //
                && getEndTime().flatMap(PartialOrCompleteTimeInstant::getCompleteTime).isPresent();
    }

    /**
     * Returns a non-empty Duration between the start and end times, is both are given and complete.
     *
     * @return a time duration, or Optional.empty() if either or both of the validity start and end times are incomplete or missing.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @JsonIgnore
    public Optional<Duration> getValidityTimeSpan() {
        if (isCompleteStrict()) {
            return Optional.of(Duration.between(this.getStartTime().get().getCompleteTime().get(), this.getEndTime().get().getCompleteTime().get()));
        } else {
            return Optional.empty();
        }
    }

    private enum TimePatternGroup {
        day, startDay, endDay, hour, startHour, endHour, minute;

        public int intValue(final Matcher matcher) {
            return Integer.parseInt(stringValue(matcher));
        }

        public String stringValue(final Matcher matcher) {
            return matcher.group(name());
        }
    }

    /*
     * NOTE: this Builder is a "detached builder" (see doc/immutables-migration.md): it does not extend
     * ImmutablePartialOrCompleteTimePeriod.Builder, but is a standalone class with plain fields. This is
     * needed because completePartial(...)/completePartialBackwards(...) need to read the builder's
     * *current* startTime/endTime mid-construction, and Immutables generates no builder-side getters at all.
     */
    public static class Builder {

        private Optional<PartialOrCompleteTimeInstant> startTime = Optional.empty();
        private Optional<PartialOrCompleteTimeInstant> endTime = Optional.empty();

        Builder() {
        }

        public Optional<PartialOrCompleteTimeInstant> getStartTime() {
            return startTime;
        }

        public Builder setStartTime(final PartialOrCompleteTimeInstant startTime) {
            this.startTime = Optional.of(requireNonNull(startTime, "startTime"));
            return this;
        }

        public Builder setStartTime(final Optional<? extends PartialOrCompleteTimeInstant> startTime) {
            requireNonNull(startTime, "startTime");
            this.startTime = startTime.isPresent() ? Optional.of(startTime.get()) : Optional.empty();
            return this;
        }

        public Builder clearStartTime() {
            this.startTime = Optional.empty();
            return this;
        }

        public Builder mapStartTime(final UnaryOperator<PartialOrCompleteTimeInstant> mapper) {
            requireNonNull(mapper, "mapper");
            this.startTime = this.startTime.map(mapper);
            return this;
        }

        public Optional<PartialOrCompleteTimeInstant> getEndTime() {
            return endTime;
        }

        public Builder setEndTime(final PartialOrCompleteTimeInstant time) {
            this.endTime = Optional.of(tryToMidnight24h(requireNonNull(time, "time")));
            return this;
        }

        public Builder setEndTime(final Optional<? extends PartialOrCompleteTimeInstant> endTime) {
            requireNonNull(endTime, "endTime");
            if (endTime.isPresent()) {
                return setEndTime(endTime.get());
            } else {
                this.endTime = Optional.empty();
                return this;
            }
        }

        public Builder clearEndTime() {
            this.endTime = Optional.empty();
            return this;
        }

        public Builder mapEndTime(final UnaryOperator<PartialOrCompleteTimeInstant> mapper) {
            requireNonNull(mapper, "mapper");
            return setEndTime(this.endTime.map(mapper));
        }

        public Builder mergeFrom(final PartialOrCompleteTimePeriod template) {
            requireNonNull(template, "template");
            this.startTime = template.getStartTime();
            this.endTime = template.getEndTime();
            return this;
        }

        public PartialOrCompleteTimePeriod build() {
            final ImmutablePartialOrCompleteTimePeriod.Builder delegate = ImmutablePartialOrCompleteTimePeriod.internalBuilder();
            this.startTime.ifPresent(delegate::setStartTime);
            this.endTime.ifPresent(delegate::setEndTime);
            return delegate.build();
        }

        public Builder setTrendTimeGroupToken(final String token) {
            requireNonNull(token, "token");
            final String kind = token.substring(0, Math.min(2, token.length()));
            final String time = token.substring(Math.min(2, token.length()));
            final PartialOrCompleteTimeInstant partial = PartialOrCompleteTimeInstant.of(
                    PartialDateTime.parseTACStringStrict(time, EnumSet.of(PartialField.HOUR, PartialField.MINUTE), false));
            if ("FM".equals(kind)) {
                return setStartTime(partial);
            } else if ("TL".equals(kind)) {
                return setEndTime(partial);
            } else {
                throw new IllegalArgumentException("token does not begin with FM or TL: " + token);
            }
        }

        private PartialOrCompleteTimeInstant tryToMidnight24h(final PartialOrCompleteTimeInstant time) {
            if (time.getPartialTime().map(PartialDateTime::isMidnight24h).orElse(true) || !time.getCompleteTime().isPresent()) {
                return time;
            }
            final ZonedDateTime completeTime = time.getCompleteTime().get();
            return time.toBuilder()//
                    .mapPartialTime(partialTime -> partialTime.withMidnight24h(YearMonth.from(completeTime)))//
                    .build();
        }

        /**
         * Equivalent to {@code completePartial(partial -> partial.toZonedDateTimeNear(reference))}.
         *
         * @param reference
         *         reference time
         *
         * @return this builder
         */
        public Builder completePartialStartingNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return completePartial(partial -> partial.toZonedDateTimeNear(reference));
        }

        /**
         * Set the complete times of start and end time.
         * Start time is completed by applying the provided {@code startCompletion} function. End time is completed to an instant after start time. If start
         * time is empty, the {@code startCompletion} function is applied to end time.
         *
         * @param startCompletion
         *         function to complete start time from given {@code PartialDateTime} to a complete {@code ZonedDateTime}
         *
         * @return this builder
         */
        public Builder completePartial(final Function<PartialDateTime, ZonedDateTime> startCompletion) {
            requireNonNull(startCompletion, "startCompletion");
            mapStartTime(partialOrComplete -> partialOrComplete.toBuilder().completePartial(startCompletion).build());
            return mapEndTime(partialOrComplete -> partialOrComplete.toBuilder()//
                    .completePartial(getStartTime()//
                            .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)
                            .map(completeTime -> (Function<PartialDateTime, ZonedDateTime>) partial -> partial.toZonedDateTimeAfter(completeTime))
                            .orElse(startCompletion))//
                    .build());
        }

        /**
         * Equivalent to {@code completePartialBackwards(partial -> partial.toZonedDateTimeNear(reference))}.
         *
         * @param reference
         *         reference time
         *
         * @return this builder
         */
        public Builder completePartialEndingNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return completePartialBackwards(partial -> partial.toZonedDateTimeNear(reference));
        }

        /**
         * Set the complete times of start and end time backwards.
         * End time is completed by applying the provided {@code startCompletion} function. Start time is completed to an instant before end time. If end
         * time is empty, the {@code startCompletion} function is applied to start time.
         *
         * @param endCompletion
         *         function to complete end time from given {@code PartialDateTime} to a complete {@code ZonedDateTime}
         *
         * @return this builder
         */
        public Builder completePartialBackwards(final Function<PartialDateTime, ZonedDateTime> endCompletion) {
            requireNonNull(endCompletion, "endCompletion");
            mapEndTime(partialOrComplete -> partialOrComplete.toBuilder()//
                    .completePartial(endCompletion)//
                    .build());
            return mapStartTime(partialOrComplete -> partialOrComplete.toBuilder()//
                    .completePartial(getStartTime()//
                            .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                            .map(zonedDateTime -> (Function<PartialDateTime, ZonedDateTime>) partial -> partial.toZonedDateTimeBefore(zonedDateTime))//
                            .orElse(endCompletion))//
                    .build());
        }
    }
}
