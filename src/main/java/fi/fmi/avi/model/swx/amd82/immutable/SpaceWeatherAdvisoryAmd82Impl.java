package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAmd79;
import fi.fmi.avi.model.swx.amd82.*;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAdvisoryAmd82Impl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"issueTime", "issuingCenter", "advisoryNumber", "replacementAdvisoryNumber", "phenomena", "analyses", "nextAdvisory", "remarks",
        "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated", "translatedBulletinID", "translatedBulletinReceptionTime",
        "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC"})
public abstract class SpaceWeatherAdvisoryAmd82Impl implements SpaceWeatherAdvisoryAmd82, Serializable {

    private static final long serialVersionUID = 2643733022733469004L;

    public static Builder builder() {
        return new Builder();
    }

    public static SpaceWeatherAdvisoryAmd82Impl immutableCopyOf(final SpaceWeatherAdvisoryAmd82 advisory) {
        requireNonNull(advisory);
        if (advisory instanceof SpaceWeatherAdvisoryAmd82Impl) {
            return (SpaceWeatherAdvisoryAmd82Impl) advisory;
        } else {
            return Builder.from(advisory).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherAdvisoryAmd82Impl> immutableCopyOf(final Optional<SpaceWeatherAdvisoryAmd82> advisory) {
        requireNonNull(advisory);
        return advisory.map(SpaceWeatherAdvisoryAmd82Impl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    @Override
    public boolean areAllTimeReferencesComplete() {
        if (this.getIssueTime().isPresent() && !this.getIssueTime().get().getCompleteTime().isPresent()) {
            return false;
        }
        if (this.getNextAdvisory().getTime().isPresent() && !this.getNextAdvisory().getTime().get().getCompleteTime().isPresent()) {
            return false;
        }
        for (final SpaceWeatherAdvisoryAnalysis analysis : this.getAnalyses()) {
            if (!analysis.getTime().getCompleteTime().isPresent()) {
                return false;
            }
        }
        return true;
    }

    public static class Builder extends SpaceWeatherAdvisoryAmd82Impl_Builder {
        @Deprecated
        Builder() {
            this.setTranslated(false);
            this.setReportStatus(ReportStatus.NORMAL);
        }

        public static Builder from(final SpaceWeatherAdvisoryAmd82 value) {
            if (value instanceof SpaceWeatherAdvisoryAmd82Impl) {
                return ((SpaceWeatherAdvisoryAmd82Impl) value).toBuilder();
            } else {
                final Builder builder = builder();
                AviationWeatherMessageBuilderHelper.copyFrom(builder, value, //
                        Builder::setReportStatus, //
                        Builder::setIssueTime, //
                        Builder::setRemarks, //
                        Builder::setPermissibleUsage, //
                        Builder::setPermissibleUsageReason, //
                        Builder::setPermissibleUsageSupplementary, //
                        Builder::setTranslated, //
                        Builder::setTranslatedBulletinID, //
                        Builder::setTranslatedBulletinReceptionTime, //
                        Builder::setTranslationCentreDesignator, //
                        Builder::setTranslationCentreName, //
                        Builder::setTranslationTime, //
                        Builder::setTranslatedTAC);
                return builder//
                        .setIssuingCenter(IssuingCenterImpl.immutableCopyOf(value.getIssuingCenter()))
                        .setAdvisoryNumber(AdvisoryNumberImpl.immutableCopyOf(value.getAdvisoryNumber()))
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.immutableCopyOf(value.getReplaceAdvisoryNumber()))
                        .addAllPhenomena(value.getPhenomena().stream()//
                                .map(p -> SpaceWeatherPhenomenon.from(p.getType(), p.getSeverity())))//
                        .addAllAnalyses(value.getAnalyses().stream().map(SpaceWeatherAdvisoryAnalysisImpl::immutableCopyOf))//
                        .setNextAdvisory(NextAdvisoryImpl.immutableCopyOf(value.getNextAdvisory()));
            }
        }

        public static Builder fromAmd79(final SpaceWeatherAdvisoryAmd79 value) {
            final Builder builder = builder();
            AviationWeatherMessageBuilderHelper.copyFrom(builder, value, //
                    Builder::setReportStatus, //
                    Builder::setIssueTime, //
                    Builder::setRemarks, //
                    Builder::setPermissibleUsage, //
                    Builder::setPermissibleUsageReason, //
                    Builder::setPermissibleUsageSupplementary, //
                    Builder::setTranslated, //
                    Builder::setTranslatedBulletinID, //
                    Builder::setTranslatedBulletinReceptionTime, //
                    Builder::setTranslationCentreDesignator, //
                    Builder::setTranslationCentreName, //
                    Builder::setTranslationTime, //
                    Builder::setTranslatedTAC);
            builder//
                    .setIssuingCenter(IssuingCenterImpl.Builder.fromAmd79(value.getIssuingCenter()).build())
                    .setAdvisoryNumber(AdvisoryNumberImpl.Builder.fromAmd79(value.getAdvisoryNumber()).build())
                    .addAllPhenomena(value.getPhenomena().stream()
                            .map(phenomenon -> SpaceWeatherPhenomenon.valueOf(phenomenon.name())))
                    .addAllAnalyses(value.getAnalyses().stream().map(analysis ->
                            SpaceWeatherAdvisoryAnalysisImpl.Builder.fromAmd79(analysis).build()))
                    .setNextAdvisory(NextAdvisoryImpl.Builder.fromAmd79(value.getNextAdvisory()).build());

            value.getReplaceAdvisoryNumber().ifPresent(replaceAdvisoryNumber ->
                    builder.setReplaceAdvisoryNumber(AdvisoryNumberImpl.Builder.fromAmd79(replaceAdvisoryNumber).build()));
            return builder;
        }

        public Builder addAllPhenomena(final List<SpaceWeatherPhenomenon> elements) {
            return super.addAllPhenomena(elements);
        }

        public Builder withCompleteIssueTimeNear(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            if (getIssueTime().isPresent() && getIssueTime().get().getCompleteTime().isPresent()) {
                return this;
            }
            return mapIssueTime((input) -> input.toBuilder().completePartialNear(reference).build());
        }

        public Builder withCompleteNextAdvisory(final ZonedDateTime issueTime) {
            requireNonNull(issueTime, "issueTime");
            if (getNextAdvisory().getTime().isPresent() && getNextAdvisory().getTime().get().getCompleteTime().isPresent()) {
                return this;
            }
            return mapNextAdvisory(nextAdvisory -> {
                final NextAdvisoryImpl.Builder builder = NextAdvisoryImpl.Builder.from(nextAdvisory);
                builder.mapTime(time -> time.toBuilder().completePartial(partial -> partial.toZonedDateTimeAfter(issueTime)).build());
                return builder.build();
            });
        }

        public Builder withCompleteAnalysisTimes(final ZonedDateTime issueTime) {
            requireNonNull(issueTime, "issueTime");
            if (!getAnalyses().isEmpty()) {
                mutateAnalyses(analyses -> {
                    final PartialOrCompleteTimeInstant completeObservationTime = analyses.get(0).getTime().toBuilder().completePartialNear(issueTime).build();
                    final Iterable<PartialOrCompleteTimeInstant> timesToComplete = () -> analyses.stream()//
                            .skip(1)// skip observation completed above
                            .map(SpaceWeatherAdvisoryAnalysis::getTime)//
                            .iterator();
                    final List<PartialOrCompleteTime> completedForecastTimes = PartialOrCompleteTimes.completeAscendingPartialTimes(timesToComplete,
                            completeObservationTime.getCompleteTime().orElse(issueTime), toZonedDateTimeNotBeforeOrNear());
                    updateAnalysisTime(analyses, 0, completeObservationTime);
                    for (int i = 1; i < analyses.size(); i++) {
                        updateAnalysisTime(analyses, i, (PartialOrCompleteTimeInstant) completedForecastTimes.get(i - 1));
                    }
                });
            }
            return this;
        }

        private void updateAnalysisTime(final List<SpaceWeatherAdvisoryAnalysis> analyses, final int index, final PartialOrCompleteTimeInstant time) {
            if (!time.equals(analyses.get(index).getTime())) {
                analyses.set(index, SpaceWeatherAdvisoryAnalysisImpl.Builder.from(analyses.get(index)).setTime(time).build());
            }
        }

        private BiFunction<PartialDateTime, ZonedDateTime, ZonedDateTime> toZonedDateTimeNotBeforeOrNear() {
            return (partial, reference) -> {
                try {
                    return partial.toZonedDateTimeNotBefore(reference);
                } catch (final DateTimeException exception) {
                    try {
                        return partial.toZonedDateTimeNear(reference);
                    } catch (final DateTimeException ignored) {
                        throw exception;
                    }
                }
            };
        }

        public Builder withAllTimesComplete(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            withCompleteIssueTimeNear(reference);
            final ZonedDateTime issueTime = getIssueTime()//
                    .flatMap(PartialOrCompleteTimeInstant::getCompleteTime)//
                    .orElse(reference);
            withCompleteNextAdvisory(issueTime);
            return withCompleteAnalysisTimes(issueTime);
        }

        @Override
        @JsonDeserialize(as = AdvisoryNumberImpl.class)
        public Builder setAdvisoryNumber(final AdvisoryNumber advisoryNumber) {
            return super.setAdvisoryNumber(AdvisoryNumberImpl.immutableCopyOf(advisoryNumber));
        }

        @Override
        @JsonDeserialize(as = AdvisoryNumberImpl.class)
        public Builder setReplaceAdvisoryNumber(final AdvisoryNumber replaceAdvisoryNumber) {
            return super.setReplaceAdvisoryNumber(AdvisoryNumberImpl.immutableCopyOf(replaceAdvisoryNumber));
        }

        @Override
        @JsonDeserialize(as = NextAdvisoryImpl.class)
        public Builder setNextAdvisory(final NextAdvisory nextAdvisory) {
            return super.setNextAdvisory(NextAdvisoryImpl.immutableCopyOf(nextAdvisory));
        }

        @Override
        @JsonDeserialize(as = IssuingCenterImpl.class)
        public Builder setIssuingCenter(final IssuingCenter issuingCenter) {
            return super.setIssuingCenter(IssuingCenterImpl.immutableCopyOf(issuingCenter));
        }

        @JsonDeserialize(contentAs = SpaceWeatherAdvisoryAnalysisImpl.class)
        public Builder addAllAnalyses(final List<SpaceWeatherAdvisoryAnalysis> elements) {
            return super.addAllAnalyses(elements);
        }

        @Override
        // Added here to cover the various cases for the generated builder to addAllAnalyses: they all call this one internally:
        public Builder addAnalyses(final SpaceWeatherAdvisoryAnalysis analysis) {
            return super.addAnalyses(SpaceWeatherAdvisoryAnalysisImpl.immutableCopyOf(analysis));
        }
    }
}
