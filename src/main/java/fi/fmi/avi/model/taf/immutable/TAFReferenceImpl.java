package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFReference;

/**
 * Created by rinne on 18/04/2018.
 */
@Value.Immutable
@Value.Style(init = "set*", typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
@JsonDeserialize(builder = TAFReferenceImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "status", "aerodrome", "issueTime", "validityTime" })
public abstract class TAFReferenceImpl implements TAFReference, Serializable {

    private static final long serialVersionUID = 8909829850430522942L;

    public static Builder builder() {
        return new Builder();
    }

    public static TAFReferenceImpl immutableCopyOf(final TAFReference tafReference) {
        requireNonNull(tafReference);
        if (tafReference instanceof TAFReferenceImpl) {
            return (TAFReferenceImpl) tafReference;
        } else {
            return Builder.copyOf(tafReference).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<TAFReferenceImpl> immutableCopyOf(final Optional<TAFReference> tafReference) {
        requireNonNull(tafReference);
        return tafReference.map(TAFReferenceImpl::immutableCopyOf);
    }

    public static TAFReferenceImpl from(final TAF taf) {
        return Builder.copyOf(taf).build();
    }

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        return (!getIssueTime().isPresent() || getIssueTime().get().getCompleteTime().isPresent()) //
                && (!getValidityTime().isPresent() || getValidityTime().get().isComplete());
    }

    public Builder toBuilder() {
        return new Builder().mergeFrom(this);
    }

    @Override
    @JsonDeserialize(as = AerodromeImpl.class)
    public abstract Aerodrome getAerodrome();

    /*
     * The FreeBuilder-era Builder extended the generated builder directly and used its builder-side
     * getters/mapX(...) convenience methods (mapIssueTime, mapValidityTime, getIssueTime, getValidityTime)
     * to implement withCompleteIssueTime/withCompleteValidityTime/withAllTimesComplete etc. Immutables
     * generates neither builder-side getters nor mapX methods (see docs/07-modernization-plan.md), so this
     * Builder is a "detached builder": a standalone class with plain fields, its own get/set/map methods,
     * which only calls into ImmutableTAFReferenceImpl.internalBuilder() inside build() to assemble the
     * final value.
     */
    public static class Builder {

        @JsonIgnore
        protected Aerodrome aerodrome;
        protected Optional<TAF.TAFStatus> status = Optional.empty();
        protected Optional<PartialOrCompleteTimeInstant> issueTime = Optional.empty();
        protected Optional<PartialOrCompleteTimePeriod> validityTime = Optional.empty();

        Builder() {
        }

        public static Builder copyOf(final TAFReference value) {
            if (value instanceof TAFReferenceImpl) {
                return ((TAFReferenceImpl) value).toBuilder();
            } else {
                return builder()//
                        .setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()))//
                        .setStatus(value.getStatus())//
                        .setIssueTime(value.getIssueTime())//
                        .setValidityTime(value.getValidityTime());
            }
        }

        public static Builder copyOf(final TAF taf) {
            requireNonNull(taf, "taf");
            return builder()//
                    .setAerodrome(AerodromeImpl.immutableCopyOf(taf.getAerodrome()))//
                    .setStatus(Optional.of(taf.getStatus()))//
                    .setIssueTime(taf.getIssueTime())//
                    .setValidityTime(taf.getValidityTime());
        }

        public static Builder copyOf(final TAFImpl.Builder taf) {
            requireNonNull(taf, "taf");
            return builder()//
                    .setAerodrome(AerodromeImpl.immutableCopyOf(taf.getAerodrome()))//
                    .setStatus(Optional.of(taf.getStatus()))//
                    .setIssueTime(taf.getIssueTime())//
                    .setValidityTime(taf.getValidityTime());
        }

        public Aerodrome getAerodrome() {
            return aerodrome;
        }

        @JsonProperty("aerodrome")
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            this.aerodrome = requireNonNull(aerodrome, "aerodrome");
            return this;
        }

        public Optional<TAF.TAFStatus> getStatus() {
            return status;
        }

        public Builder setStatus(final TAF.TAFStatus status) {
            this.status = Optional.of(requireNonNull(status, "status"));
            return this;
        }

        public Builder setStatus(final Optional<? extends TAF.TAFStatus> status) {
            requireNonNull(status, "status");
            this.status = status.isPresent() ? Optional.of(status.get()) : Optional.empty();
            return this;
        }

        public Builder clearStatus() {
            this.status = Optional.empty();
            return this;
        }

        public Optional<PartialOrCompleteTimeInstant> getIssueTime() {
            return issueTime;
        }

        public Builder setIssueTime(final PartialOrCompleteTimeInstant issueTime) {
            this.issueTime = Optional.of(requireNonNull(issueTime, "issueTime"));
            return this;
        }

        public Builder setIssueTime(final Optional<? extends PartialOrCompleteTimeInstant> issueTime) {
            requireNonNull(issueTime, "issueTime");
            this.issueTime = issueTime.isPresent() ? Optional.of(issueTime.get()) : Optional.empty();
            return this;
        }

        public Builder clearIssueTime() {
            this.issueTime = Optional.empty();
            return this;
        }

        public Builder mapIssueTime(final UnaryOperator<PartialOrCompleteTimeInstant> mapper) {
            requireNonNull(mapper, "mapper");
            this.issueTime = this.issueTime.map(mapper);
            return this;
        }

        public Optional<PartialOrCompleteTimePeriod> getValidityTime() {
            return validityTime;
        }

        public Builder setValidityTime(final PartialOrCompleteTimePeriod validityTime) {
            this.validityTime = Optional.of(requireNonNull(validityTime, "validityTime"));
            return this;
        }

        public Builder setValidityTime(final Optional<? extends PartialOrCompleteTimePeriod> validityTime) {
            requireNonNull(validityTime, "validityTime");
            this.validityTime = validityTime.isPresent() ? Optional.of(validityTime.get()) : Optional.empty();
            return this;
        }

        public Builder clearValidityTime() {
            this.validityTime = Optional.empty();
            return this;
        }

        public Builder mapValidityTime(final UnaryOperator<PartialOrCompleteTimePeriod> mapper) {
            requireNonNull(mapper, "mapper");
            this.validityTime = this.validityTime.map(mapper);
            return this;
        }

        public Builder mergeFrom(final TAFReferenceImpl template) {
            requireNonNull(template, "template");
            setAerodrome(template.getAerodrome());
            this.status = template.getStatus();
            this.issueTime = template.getIssueTime();
            this.validityTime = template.getValidityTime();
            return this;
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) {
            requireNonNull(yearMonth, "yearMonth");
            return mapIssueTime((input) -> input.toBuilder().completePartialAt(yearMonth).build());
        }

        public Builder withCompleteIssueTimeNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return mapIssueTime((input) -> input.toBuilder().completePartialNear(reference).build());
        }

        public Builder withCompleteValidityTime(final ZonedDateTime issueTime) {
            requireNonNull(issueTime, "issueTime");
            return mapValidityTime(validityTime -> validityTime.toBuilder().completePartialStartingNear(issueTime).build());
        }

        public Builder withAllTimesComplete(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            withCompleteIssueTimeNear(reference);
            return withCompleteValidityTime(getIssueTime()//
                    .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                    .orElse(reference));
        }

        public Builder withAllTimesCompleteFromValidityEnd(final ZonedDateTime validityEnd) {
            requireNonNull(validityEnd, "validityEnd");
            mapValidityTime(validityTime -> validityTime.toBuilder().completePartialEndingNear(validityEnd).build());
            return withCompleteIssueTimeNear(getValidityTime()//
                    .flatMap(PartialOrCompleteTimePeriod::getStartTime)//
                    .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                    .orElse(validityEnd));
        }

        public ImmutableTAFReferenceImpl build() {
            final ImmutableTAFReferenceImpl.Builder delegate = ImmutableTAFReferenceImpl.internalBuilder()//
                    .setAerodrome(getAerodrome());
            getStatus().ifPresent(delegate::setStatus);
            getIssueTime().ifPresent(delegate::setIssueTime);
            getValidityTime().ifPresent(delegate::setValidityTime);
            return delegate.build();
        }
    }
}
