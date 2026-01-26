package fi.fmi.avi.model.immutable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.AviationWeatherMessageBuilderHelper;
import fi.fmi.avi.model.GenericAviationWeatherMessage;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = GenericAviationWeatherMessageImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "messageType", "messageFormat", "originalMessage", "issueTime", "validityTime", "targetAerodrome" })
public abstract class GenericAviationWeatherMessageImpl implements GenericAviationWeatherMessage, Serializable {

    private static final long serialVersionUID = 2232603779482075673L;

    public static Builder builder() {
        return new Builder();
    }

    public static GenericAviationWeatherMessageImpl immutableCopyOf(final GenericAviationWeatherMessage message) {
        Objects.requireNonNull(message);
        if (message instanceof GenericAviationWeatherMessageImpl) {
            return (GenericAviationWeatherMessageImpl) message;
        } else {
            return Builder.from(message).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<GenericAviationWeatherMessageImpl> immutableCopyOf(final Optional<GenericAviationWeatherMessage> geoPosition) {
        Objects.requireNonNull(geoPosition);
        return geoPosition.map(GenericAviationWeatherMessageImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (this.getIssueTime().isPresent()) {
            if (!this.getIssueTime().get().getCompleteTime().isPresent()) {
                return false;
            }
        }
        if (this.getObservationTime().isPresent()) {
            if (!this.getObservationTime().get().getCompleteTime().isPresent()) {
                return false;
            }
        }
        if (this.getValidityTime().isPresent()) {
            return this.getValidityTime().get().isComplete();
        }
        return true;
    }

    public static class Builder extends GenericAviationWeatherMessageImpl_Builder {
        Builder() {
            setReportStatus(ReportStatus.NORMAL);
            setTranslated(false);
            setNil(false);
        }

        public static Builder from(final GenericAviationWeatherMessage value) {
            if (value instanceof GenericAviationWeatherMessageImpl) {
                return ((GenericAviationWeatherMessageImpl) value).toBuilder();
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
                        .setOriginalMessage(value.getOriginalMessage())//
                        .setMessageType(value.getMessageType())//
                        .setMessageFormat(value.getMessageFormat())//
                        .setValidityTime(value.getValidityTime())//
                        .setObservationTime(value.getObservationTime())//
                        .setNil(value.isNil())//
                        .putAllLocationIndicators(value.getLocationIndicators());
            }
        }
    }
}
