package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.*;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAdvisoryAmd79;
import fi.fmi.avi.model.swx.amd82.*;
import org.immutables.value.Value;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static java.util.Objects.requireNonNull;

/*
 * NOTE: this Builder is a "detached builder" (see docs/07-modernization-plan.md): it does not extend
 * ImmutableSpaceWeatherAdvisoryAmd82Impl.Builder, but is a standalone class with plain fields, implementing
 * its own get/set/map/mutate methods, and only calls into ImmutableSpaceWeatherAdvisoryAmd82Impl.internalBuilder()
 * inside build() to assemble the final value. This is needed because withCompleteIssueTimeNear(...),
 * withCompleteNextAdvisory(...) and withCompleteAnalysisTimes(...) all need to read the builder's *current*
 * property values mid-construction, and Immutables generates no builder-side getters at all.
 */
@Value.Immutable
@Value.Style(init = "set*", get = {"is*", "get*"},
        passAnnotations = {com.fasterxml.jackson.databind.annotation.JsonDeserialize.class, com.fasterxml.jackson.annotation.JsonProperty.class},
        typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
@JsonDeserialize(builder = SpaceWeatherAdvisoryAmd82Impl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({
        "issueTime",
        "issuingCenter",
        "effect",
        "advisoryNumber",
        "replaceAdvisoryNumbers",
        "analyses",
        "remarks",
        "nextAdvisory",
        "permissibleUsage",
        "permissibleUsageReason",
        "permissibleUsageSupplementary",
        "translated",
        "translatedBulletinID",
        "translatedBulletinReceptionTime",
        "translationCentreDesignator",
        "translationCentreName",
        "translationTime",
        "translatedTAC"})
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
            return Builder.copyOf(advisory).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherAdvisoryAmd82Impl> immutableCopyOf(final Optional<SpaceWeatherAdvisoryAmd82> advisory) {
        requireNonNull(advisory);
        return advisory.map(SpaceWeatherAdvisoryAmd82Impl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().mergeFrom(this);
    }

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

    @Override
    @JsonDeserialize(as = AdvisoryNumberImpl.class)
    public abstract AdvisoryNumber getAdvisoryNumber();

    @Override
    @JsonDeserialize(as = NextAdvisoryImpl.class)
    public abstract NextAdvisory getNextAdvisory();

    @Override
    @JsonDeserialize(as = IssuingCenterImpl.class)
    public abstract IssuingCenter getIssuingCenter();

    public static class Builder {

        protected AviationWeatherMessage.ReportStatus reportStatus = ReportStatus.NORMAL;
        protected boolean translated;
        protected Optional<PartialOrCompleteTimeInstant> issueTime = Optional.empty();
        protected Optional<List<String>> remarks = Optional.empty();
        protected Optional<AviationCodeListUser.PermissibleUsage> permissibleUsage = Optional.empty();
        protected Optional<AviationCodeListUser.PermissibleUsageReason> permissibleUsageReason = Optional.empty();
        protected Optional<String> permissibleUsageSupplementary = Optional.empty();
        protected Optional<String> translatedBulletinID = Optional.empty();
        protected Optional<ZonedDateTime> translatedBulletinReceptionTime = Optional.empty();
        protected Optional<String> translationCentreDesignator = Optional.empty();
        protected Optional<String> translationCentreName = Optional.empty();
        protected Optional<ZonedDateTime> translationTime = Optional.empty();
        protected Optional<String> translatedTAC = Optional.empty();

        @JsonIgnore
        protected IssuingCenter issuingCenter;
        protected Effect effect;
        @JsonIgnore
        protected AdvisoryNumber advisoryNumber;
        @JsonIgnore
        protected List<AdvisoryNumber> replaceAdvisoryNumbers = new ArrayList<>();
        @JsonIgnore
        protected List<SpaceWeatherAdvisoryAnalysis> analyses = new ArrayList<>();
        @JsonIgnore
        protected NextAdvisory nextAdvisory;

        @Deprecated
        Builder() {
        }

        private static Builder builderFromAviationWeatherMessage(final AviationWeatherMessage aviationWeatherMessage) {
            final Builder builder = builder();
            AviationWeatherMessageBuilderHelper.copyFrom(builder, aviationWeatherMessage, //
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
            return builder;
        }

        public static Builder copyOf(final SpaceWeatherAdvisoryAmd82 value) {
            if (value instanceof SpaceWeatherAdvisoryAmd82Impl) {
                return ((SpaceWeatherAdvisoryAmd82Impl) value).toBuilder();
            } else {
                return builderFromAviationWeatherMessage(value)//
                        .setIssuingCenter(IssuingCenterImpl.immutableCopyOf(value.getIssuingCenter()))
                        .setAdvisoryNumber(AdvisoryNumberImpl.immutableCopyOf(value.getAdvisoryNumber()))
                        .addAllReplaceAdvisoryNumbers(value.getReplaceAdvisoryNumbers().stream().map(AdvisoryNumberImpl::immutableCopyOf)
                                .collect(java.util.stream.Collectors.toList()))
                        .setEffect(value.getEffect())
                        .addAllAnalyses(value.getAnalyses().stream().map(SpaceWeatherAdvisoryAnalysisImpl::immutableCopyOf)
                                .collect(java.util.stream.Collectors.toList()))//
                        .setNextAdvisory(NextAdvisoryImpl.immutableCopyOf(value.getNextAdvisory()));
            }
        }

        public static Builder fromAmd79(final SpaceWeatherAdvisoryAmd79 value) {
            final Intensity intensity = Intensity.fromString(
                    value.getPhenomena().stream()
                            .map(fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon::getSeverity)
                            .max(fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon.Severity.comparator())
                            .orElseThrow(() -> new IllegalArgumentException("Missing phenomena: " + value))
                            .getCode());
            return builderFromAviationWeatherMessage(value)
                    .setIssuingCenter(IssuingCenterImpl.Builder.fromAmd79(value.getIssuingCenter()).build())
                    .setEffect(effectFromAmd79(value))
                    .setAdvisoryNumber(AdvisoryNumberImpl.Builder.fromAmd79(value.getAdvisoryNumber()).build())
                    .addAllReplaceAdvisoryNumbers(value.getReplaceAdvisoryNumber()
                            .map(advisoryNumber -> Collections.singletonList(
                                    AdvisoryNumberImpl.Builder.fromAmd79(advisoryNumber).build()))
                            .orElse(Collections.emptyList()))
                    .addAllAnalyses(value.getAnalyses().stream().map(analysis ->
                                    (SpaceWeatherAdvisoryAnalysis) SpaceWeatherAdvisoryAnalysisImpl.Builder.fromAmd79(intensity, analysis).build())
                            .collect(java.util.stream.Collectors.toList()))
                    .setNextAdvisory(NextAdvisoryImpl.Builder.fromAmd79(value.getNextAdvisory()).build());
        }

        private static Effect effectFromAmd79(final SpaceWeatherAdvisoryAmd79 value) {
            // Same effect with different intensities is forbidden in Annex 3, but this is not enforced by the model.
            final Iterator<fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon.Type> phenomenonTypes = value.getPhenomena().stream()
                    .map(fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon::getType)
                    .distinct()
                    .iterator();
            if (!phenomenonTypes.hasNext()) {
                throw new IllegalArgumentException("Missing phenomena: " + value);
            }
            final fi.fmi.avi.model.swx.amd79.SpaceWeatherPhenomenon.Type phenomenonType = phenomenonTypes.next();
            if (phenomenonTypes.hasNext()) {
                throw new IllegalArgumentException("Only one effect is allowed: " + value);
            }
            return Effect.fromString(phenomenonType.getCode().replaceAll("\\s+", "_"));
        }

        public AviationWeatherMessage.ReportStatus getReportStatus() {
            return reportStatus;
        }

        public Builder setReportStatus(final AviationWeatherMessage.ReportStatus reportStatus) {
            this.reportStatus = requireNonNull(reportStatus, "reportStatus");
            return this;
        }

        public boolean isTranslated() {
            return translated;
        }

        public Builder setTranslated(final boolean translated) {
            this.translated = translated;
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

        public Optional<List<String>> getRemarks() {
            return remarks;
        }

        public Builder setRemarks(final List<String> remarks) {
            this.remarks = Optional.of(requireNonNull(remarks, "remarks"));
            return this;
        }

        public Builder setRemarks(final Optional<? extends List<String>> remarks) {
            requireNonNull(remarks, "remarks");
            this.remarks = remarks.isPresent() ? Optional.of(remarks.get()) : Optional.empty();
            return this;
        }

        public Builder clearRemarks() {
            this.remarks = Optional.empty();
            return this;
        }

        public Optional<AviationCodeListUser.PermissibleUsage> getPermissibleUsage() {
            return permissibleUsage;
        }

        public Builder setPermissibleUsage(final AviationCodeListUser.PermissibleUsage permissibleUsage) {
            this.permissibleUsage = Optional.of(requireNonNull(permissibleUsage, "permissibleUsage"));
            return this;
        }

        public Builder setPermissibleUsage(final Optional<? extends AviationCodeListUser.PermissibleUsage> permissibleUsage) {
            requireNonNull(permissibleUsage, "permissibleUsage");
            this.permissibleUsage = permissibleUsage.isPresent() ? Optional.of(permissibleUsage.get()) : Optional.empty();
            return this;
        }

        public Builder clearPermissibleUsage() {
            this.permissibleUsage = Optional.empty();
            return this;
        }

        public Optional<AviationCodeListUser.PermissibleUsageReason> getPermissibleUsageReason() {
            return permissibleUsageReason;
        }

        public Builder setPermissibleUsageReason(final AviationCodeListUser.PermissibleUsageReason permissibleUsageReason) {
            this.permissibleUsageReason = Optional.of(requireNonNull(permissibleUsageReason, "permissibleUsageReason"));
            return this;
        }

        public Builder setPermissibleUsageReason(final Optional<? extends AviationCodeListUser.PermissibleUsageReason> permissibleUsageReason) {
            requireNonNull(permissibleUsageReason, "permissibleUsageReason");
            this.permissibleUsageReason = permissibleUsageReason.isPresent() ? Optional.of(permissibleUsageReason.get()) : Optional.empty();
            return this;
        }

        public Builder clearPermissibleUsageReason() {
            this.permissibleUsageReason = Optional.empty();
            return this;
        }

        public Optional<String> getPermissibleUsageSupplementary() {
            return permissibleUsageSupplementary;
        }

        public Builder setPermissibleUsageSupplementary(final String permissibleUsageSupplementary) {
            this.permissibleUsageSupplementary = Optional.of(requireNonNull(permissibleUsageSupplementary, "permissibleUsageSupplementary"));
            return this;
        }

        public Builder setPermissibleUsageSupplementary(final Optional<? extends String> permissibleUsageSupplementary) {
            requireNonNull(permissibleUsageSupplementary, "permissibleUsageSupplementary");
            this.permissibleUsageSupplementary = permissibleUsageSupplementary.isPresent() ? Optional.of(permissibleUsageSupplementary.get())
                    : Optional.empty();
            return this;
        }

        public Builder clearPermissibleUsageSupplementary() {
            this.permissibleUsageSupplementary = Optional.empty();
            return this;
        }

        public Optional<String> getTranslatedBulletinID() {
            return translatedBulletinID;
        }

        public Builder setTranslatedBulletinID(final String translatedBulletinID) {
            this.translatedBulletinID = Optional.of(requireNonNull(translatedBulletinID, "translatedBulletinID"));
            return this;
        }

        public Builder setTranslatedBulletinID(final Optional<? extends String> translatedBulletinID) {
            requireNonNull(translatedBulletinID, "translatedBulletinID");
            this.translatedBulletinID = translatedBulletinID.isPresent() ? Optional.of(translatedBulletinID.get()) : Optional.empty();
            return this;
        }

        public Builder clearTranslatedBulletinID() {
            this.translatedBulletinID = Optional.empty();
            return this;
        }

        public Optional<ZonedDateTime> getTranslatedBulletinReceptionTime() {
            return translatedBulletinReceptionTime;
        }

        public Builder setTranslatedBulletinReceptionTime(final ZonedDateTime translatedBulletinReceptionTime) {
            this.translatedBulletinReceptionTime = Optional.of(requireNonNull(translatedBulletinReceptionTime, "translatedBulletinReceptionTime"));
            return this;
        }

        public Builder setTranslatedBulletinReceptionTime(final Optional<? extends ZonedDateTime> translatedBulletinReceptionTime) {
            requireNonNull(translatedBulletinReceptionTime, "translatedBulletinReceptionTime");
            this.translatedBulletinReceptionTime = translatedBulletinReceptionTime.isPresent() ? Optional.of(translatedBulletinReceptionTime.get())
                    : Optional.empty();
            return this;
        }

        public Builder clearTranslatedBulletinReceptionTime() {
            this.translatedBulletinReceptionTime = Optional.empty();
            return this;
        }

        public Optional<String> getTranslationCentreDesignator() {
            return translationCentreDesignator;
        }

        public Builder setTranslationCentreDesignator(final String translationCentreDesignator) {
            this.translationCentreDesignator = Optional.of(requireNonNull(translationCentreDesignator, "translationCentreDesignator"));
            return this;
        }

        public Builder setTranslationCentreDesignator(final Optional<? extends String> translationCentreDesignator) {
            requireNonNull(translationCentreDesignator, "translationCentreDesignator");
            this.translationCentreDesignator = translationCentreDesignator.isPresent() ? Optional.of(translationCentreDesignator.get()) : Optional.empty();
            return this;
        }

        public Builder clearTranslationCentreDesignator() {
            this.translationCentreDesignator = Optional.empty();
            return this;
        }

        public Optional<String> getTranslationCentreName() {
            return translationCentreName;
        }

        public Builder setTranslationCentreName(final String translationCentreName) {
            this.translationCentreName = Optional.of(requireNonNull(translationCentreName, "translationCentreName"));
            return this;
        }

        public Builder setTranslationCentreName(final Optional<? extends String> translationCentreName) {
            requireNonNull(translationCentreName, "translationCentreName");
            this.translationCentreName = translationCentreName.isPresent() ? Optional.of(translationCentreName.get()) : Optional.empty();
            return this;
        }

        public Builder clearTranslationCentreName() {
            this.translationCentreName = Optional.empty();
            return this;
        }

        public Optional<ZonedDateTime> getTranslationTime() {
            return translationTime;
        }

        public Builder setTranslationTime(final ZonedDateTime translationTime) {
            this.translationTime = Optional.of(requireNonNull(translationTime, "translationTime"));
            return this;
        }

        public Builder setTranslationTime(final Optional<? extends ZonedDateTime> translationTime) {
            requireNonNull(translationTime, "translationTime");
            this.translationTime = translationTime.isPresent() ? Optional.of(translationTime.get()) : Optional.empty();
            return this;
        }

        public Builder clearTranslationTime() {
            this.translationTime = Optional.empty();
            return this;
        }

        public Optional<String> getTranslatedTAC() {
            return translatedTAC;
        }

        public Builder setTranslatedTAC(final String translatedTAC) {
            this.translatedTAC = Optional.of(requireNonNull(translatedTAC, "translatedTAC"));
            return this;
        }

        public Builder setTranslatedTAC(final Optional<? extends String> translatedTAC) {
            requireNonNull(translatedTAC, "translatedTAC");
            this.translatedTAC = translatedTAC.isPresent() ? Optional.of(translatedTAC.get()) : Optional.empty();
            return this;
        }

        public Builder clearTranslatedTAC() {
            this.translatedTAC = Optional.empty();
            return this;
        }

        public IssuingCenter getIssuingCenter() {
            return issuingCenter;
        }

        @JsonProperty("issuingCenter")
        @JsonDeserialize(as = IssuingCenterImpl.class)
        public Builder setIssuingCenter(final IssuingCenter issuingCenter) {
            this.issuingCenter = requireNonNull(issuingCenter, "issuingCenter");
            return this;
        }

        public Effect getEffect() {
            return effect;
        }

        public Builder setEffect(final Effect effect) {
            this.effect = requireNonNull(effect, "effect");
            return this;
        }

        public AdvisoryNumber getAdvisoryNumber() {
            return advisoryNumber;
        }

        @JsonProperty("advisoryNumber")
        @JsonDeserialize(as = AdvisoryNumberImpl.class)
        public Builder setAdvisoryNumber(final AdvisoryNumber advisoryNumber) {
            this.advisoryNumber = requireNonNull(advisoryNumber, "advisoryNumber");
            return this;
        }

        public List<AdvisoryNumber> getReplaceAdvisoryNumbers() {
            return replaceAdvisoryNumbers;
        }

        @JsonProperty("replaceAdvisoryNumbers")
        @JsonDeserialize(contentAs = AdvisoryNumberImpl.class)
        public Builder setReplaceAdvisoryNumbers(final List<? extends AdvisoryNumber> replaceAdvisoryNumbers) {
            requireNonNull(replaceAdvisoryNumbers, "replaceAdvisoryNumbers");
            this.replaceAdvisoryNumbers = new ArrayList<>(replaceAdvisoryNumbers);
            return this;
        }

        public Builder addReplaceAdvisoryNumbers(final AdvisoryNumber replaceAdvisoryNumber) {
            this.replaceAdvisoryNumbers.add(AdvisoryNumberImpl.immutableCopyOf(requireNonNull(replaceAdvisoryNumber, "replaceAdvisoryNumber")));
            return this;
        }

        public Builder addAllReplaceAdvisoryNumbers(final List<? extends AdvisoryNumber> replaceAdvisoryNumbers) {
            requireNonNull(replaceAdvisoryNumbers, "replaceAdvisoryNumbers");
            replaceAdvisoryNumbers.forEach(this::addReplaceAdvisoryNumbers);
            return this;
        }

        public Builder clearReplaceAdvisoryNumbers() {
            this.replaceAdvisoryNumbers = new ArrayList<>();
            return this;
        }

        public List<SpaceWeatherAdvisoryAnalysis> getAnalyses() {
            return analyses;
        }

        @JsonProperty("analyses")
        @JsonDeserialize(contentAs = SpaceWeatherAdvisoryAnalysisImpl.class)
        public Builder setAnalyses(final List<SpaceWeatherAdvisoryAnalysis> analyses) {
            requireNonNull(analyses, "analyses");
            this.analyses = new ArrayList<>(analyses);
            return this;
        }

        public Builder addAnalyses(final SpaceWeatherAdvisoryAnalysis analysis) {
            this.analyses.add(SpaceWeatherAdvisoryAnalysisImpl.immutableCopyOf(requireNonNull(analysis, "analysis")));
            return this;
        }

        public Builder addAllAnalyses(final List<SpaceWeatherAdvisoryAnalysis> elements) {
            requireNonNull(elements, "elements");
            elements.forEach(this::addAnalyses);
            return this;
        }

        public Builder clearAnalyses() {
            this.analyses = new ArrayList<>();
            return this;
        }

        public Builder mutateAnalyses(final Consumer<List<SpaceWeatherAdvisoryAnalysis>> mutator) {
            requireNonNull(mutator, "mutator");
            mutator.accept(this.analyses);
            return this;
        }

        public NextAdvisory getNextAdvisory() {
            return nextAdvisory;
        }

        @JsonProperty("nextAdvisory")
        @JsonDeserialize(as = NextAdvisoryImpl.class)
        public Builder setNextAdvisory(final NextAdvisory nextAdvisory) {
            this.nextAdvisory = requireNonNull(nextAdvisory, "nextAdvisory");
            return this;
        }

        public Builder mapNextAdvisory(final UnaryOperator<NextAdvisory> mapper) {
            requireNonNull(mapper, "mapper");
            this.nextAdvisory = mapper.apply(this.nextAdvisory);
            return this;
        }

        public Builder mergeFrom(final SpaceWeatherAdvisoryAmd82Impl template) {
            requireNonNull(template, "template");
            setReportStatus(template.getReportStatus());
            setTranslated(template.isTranslated());
            this.issueTime = template.getIssueTime();
            this.remarks = template.getRemarks();
            this.permissibleUsage = template.getPermissibleUsage();
            this.permissibleUsageReason = template.getPermissibleUsageReason();
            this.permissibleUsageSupplementary = template.getPermissibleUsageSupplementary();
            this.translatedBulletinID = template.getTranslatedBulletinID();
            this.translatedBulletinReceptionTime = template.getTranslatedBulletinReceptionTime();
            this.translationCentreDesignator = template.getTranslationCentreDesignator();
            this.translationCentreName = template.getTranslationCentreName();
            this.translationTime = template.getTranslationTime();
            this.translatedTAC = template.getTranslatedTAC();
            setIssuingCenter(template.getIssuingCenter());
            setEffect(template.getEffect());
            setAdvisoryNumber(template.getAdvisoryNumber());
            this.replaceAdvisoryNumbers = new ArrayList<>(template.getReplaceAdvisoryNumbers());
            this.analyses = new ArrayList<>(template.getAnalyses());
            setNextAdvisory(template.getNextAdvisory());
            return this;
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
                final NextAdvisoryImpl.Builder builder = NextAdvisoryImpl.Builder.copyOf(nextAdvisory);
                // NOTE: rewritten from builder.mapTime(...) (FreeBuilder-only convenience, see
                // docs/07-modernization-plan.md): read the time from nextAdvisory itself instead of the builder.
                nextAdvisory.getTime()
                        .map(time -> time.toBuilder().completePartial(partial -> partial.toZonedDateTimeAfter(issueTime)).build())
                        .ifPresent(builder::setTime);
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
                analyses.set(index, SpaceWeatherAdvisoryAnalysisImpl.Builder.copyOf(analyses.get(index)).setTime(time).build());
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

        public ImmutableSpaceWeatherAdvisoryAmd82Impl build() {
            final ImmutableSpaceWeatherAdvisoryAmd82Impl.Builder delegate = ImmutableSpaceWeatherAdvisoryAmd82Impl.internalBuilder()//
                    .setReportStatus(getReportStatus())//
                    .setTranslated(isTranslated())//
                    .setIssuingCenter(getIssuingCenter())//
                    .setEffect(getEffect())//
                    .setAdvisoryNumber(getAdvisoryNumber())//
                    .setReplaceAdvisoryNumbers(getReplaceAdvisoryNumbers())//
                    .setAnalyses(getAnalyses())//
                    .setNextAdvisory(getNextAdvisory());
            getIssueTime().ifPresent(delegate::setIssueTime);
            getRemarks().ifPresent(delegate::setRemarks);
            getPermissibleUsage().ifPresent(delegate::setPermissibleUsage);
            getPermissibleUsageReason().ifPresent(delegate::setPermissibleUsageReason);
            getPermissibleUsageSupplementary().ifPresent(delegate::setPermissibleUsageSupplementary);
            getTranslatedBulletinID().ifPresent(delegate::setTranslatedBulletinID);
            getTranslatedBulletinReceptionTime().ifPresent(delegate::setTranslatedBulletinReceptionTime);
            getTranslationCentreDesignator().ifPresent(delegate::setTranslationCentreDesignator);
            getTranslationCentreName().ifPresent(delegate::setTranslationCentreName);
            getTranslationTime().ifPresent(delegate::setTranslationTime);
            getTranslatedTAC().ifPresent(delegate::setTranslatedTAC);
            return delegate.build();
        }
    }
}
