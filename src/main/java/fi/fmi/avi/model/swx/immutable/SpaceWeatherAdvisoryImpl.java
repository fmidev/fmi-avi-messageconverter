package fi.fmi.avi.model.swx.immutable;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import fi.fmi.avi.model.swx.*;
import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAdvisoryImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "issuingCenter", "advisoryNumber", "replacementAdvisoryNumber", "phenomena", "analyses", "nextAdvisory", "remarks",
        "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated", "translatedBulletinID", "translatedBulletinReceptionTime",
        "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class SpaceWeatherAdvisoryImpl implements SpaceWeatherAdvisory, Serializable {

    private static final long serialVersionUID = 2643733022733469004L;

    public static Builder builder() {
        return new Builder();
    }

    public static SpaceWeatherAdvisoryImpl immutableCopyOf(final SpaceWeatherAdvisory advisory) {
        Objects.requireNonNull(advisory);
        if (advisory instanceof SpaceWeatherAdvisoryImpl) {
            return (SpaceWeatherAdvisoryImpl) advisory;
        } else {
            return Builder.from(advisory).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherAdvisoryImpl> immutableCopyOf(final Optional<SpaceWeatherAdvisory> advisory) {
        Objects.requireNonNull(advisory);
        return advisory.map(SpaceWeatherAdvisoryImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    @Override
    public boolean areAllTimeReferencesComplete() {
        if (this.getIssueTime().isPresent() && !this.getIssueTime().get().getCompleteTime().isPresent()) {
            return false;
        }
        for (final SpaceWeatherAdvisoryAnalysis analysis : this.getAnalyses()) {
            if (!analysis.getTime().getCompleteTime().isPresent()) {
                return false;
            }
        }
        return true;
    }

    public static class Builder extends SpaceWeatherAdvisoryImpl_Builder {
        @Deprecated
        Builder() {
            this.setTranslated(false);
        }

        public static Builder from(final SpaceWeatherAdvisory value) {
            if (value instanceof SpaceWeatherAdvisoryImpl) {
                return ((SpaceWeatherAdvisoryImpl) value).toBuilder();
            } else {
                final Builder retval = builder();

                //From AviationWeatherMessage:
                retval.setPermissibleUsage(value.getPermissibleUsage());
                retval.setPermissibleUsageReason(value.getPermissibleUsageReason());
                retval.setPermissibleUsageSupplementary(value.getPermissibleUsageSupplementary());
                retval.setTranslated(value.isTranslated());
                retval.setTranslatedBulletinID(value.getTranslatedBulletinID());
                retval.setTranslatedBulletinReceptionTime(value.getTranslatedBulletinReceptionTime());
                retval.setTranslationCentreDesignator(value.getTranslationCentreDesignator());
                retval.setTranslationCentreName(value.getTranslationCentreName());
                retval.setTranslationTime(value.getTranslationTime());
                retval.setTranslatedTAC(value.getTranslatedTAC());
                retval.setRemarks(value.getRemarks());
                retval.setIssueTime(value.getIssueTime());

                //From SpaceWeatherAdvisory:
                retval.setIssuingCenter(IssuingCenterImpl.immutableCopyOf(value.getIssuingCenter()))
                        .setAdvisoryNumber(AdvisoryNumberImpl.immutableCopyOf(value.getAdvisoryNumber()))
                        .setReplaceAdvisoryNumber(AdvisoryNumberImpl.immutableCopyOf(value.getReplaceAdvisoryNumber()))
                        .setNextAdvisory(NextAdvisoryImpl.immutableCopyOf(value.getNextAdvisory()));

                retval.addAllPhenomena(value.getPhenomena().stream()//
                        .map(p -> SpaceWeatherPhenomenonImpl.builder().setType(p.getType()).setSeverity(p.getSeverity()).build()));
                retval.addAllAnalyses(value.getAnalyses().stream().map(SpaceWeatherAdvisoryAnalysisImpl::immutableCopyOf));
                return retval;
            }
        }

        @JsonDeserialize(contentAs = SpaceWeatherPhenomenonImpl.class)
        public Builder addAllPhenomena(final List<SpaceWeatherPhenomenon> elements) {
            return super.addAllPhenomena(elements);
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
