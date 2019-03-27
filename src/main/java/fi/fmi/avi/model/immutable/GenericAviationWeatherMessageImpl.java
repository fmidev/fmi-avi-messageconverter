package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.GenericAviationWeatherMessage;

@FreeBuilder
@JsonDeserialize(builder = GenericAviationWeatherMessageImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "messageType", "messageFormat", "originalMessage",
        "issueTime", "validityTime", "targetAerodrome"})
public abstract class GenericAviationWeatherMessageImpl implements GenericAviationWeatherMessage, Serializable {

    private static final long serialVersionUID = 2232603779482075673L;

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
        if (this.getValidityTime().isPresent()) {
            if (!this.getValidityTime().get().isComplete()) {
                return false;
            }
        }
        return true;
    }


    public static class Builder extends GenericAviationWeatherMessageImpl_Builder {

        public static Builder from(final GenericAviationWeatherMessage value) {
            if (value instanceof GenericAviationWeatherMessageImpl) {
                return ((GenericAviationWeatherMessageImpl) value).toBuilder();
            } else {
                //From AviationWeatherMessage:
                final GenericAviationWeatherMessageImpl.Builder retval = new GenericAviationWeatherMessageImpl.Builder()//
                        .setPermissibleUsage(value.getPermissibleUsage())//
                        .setPermissibleUsageReason(value.getPermissibleUsageReason())//
                        .setPermissibleUsageSupplementary(value.getPermissibleUsageSupplementary())//
                        .setTranslated(value.isTranslated())//
                        .setTranslatedBulletinID(value.getTranslatedBulletinID())//
                        .setTranslatedBulletinReceptionTime(value.getTranslatedBulletinReceptionTime())//
                        .setTranslationCentreDesignator(value.getTranslationCentreDesignator())//
                        .setTranslationCentreName(value.getTranslationCentreName())//
                        .setTranslationTime(value.getTranslationTime())//
                        .setTranslatedTAC(value.getTranslatedTAC());

                value.getRemarks().map(remarks -> retval.setRemarks(Collections.unmodifiableList(remarks)));

                retval.setOriginalMessage(value.getOriginalMessage())
                        .setMessageType(value.getMessageType())//
                        .setMessageFormat(value.getMessageFormat())//
                        .setIssueTime(value.getIssueTime())//
                        .setValidityTime(value.getValidityTime())//
                        .setTargetAerodrome(value.getTargetAerodrome());

                return retval;
            }
        }

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setTargetAerodrome(final Aerodrome aerodrome) {
            return super.setTargetAerodrome(aerodrome);
        }
    }
}
