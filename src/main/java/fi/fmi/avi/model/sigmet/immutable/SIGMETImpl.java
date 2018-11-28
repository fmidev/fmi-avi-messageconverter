package fi.fmi.avi.model.sigmet.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.sigmet.SIGMET;

@FreeBuilder
@JsonDeserialize(builder = SIGMETImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated", "translatedBulletinID",
        "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class SIGMETImpl implements SIGMET, Serializable {

    private static final long serialVersionUID = 7783515832011053005L;

    public static SIGMETImpl immutableCopyOf(final SIGMET sigmet) {
        requireNonNull(sigmet);
        if (sigmet instanceof SIGMETImpl) {
            return (SIGMETImpl) sigmet;
        } else {
            return Builder.from(sigmet).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SIGMETImpl> immutableCopyOf(final Optional<SIGMET> sigmet) {
        requireNonNull(sigmet);
        return sigmet.map(SIGMETImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    /**
     * Returns true if issue time, valid time and all other time references contained in this
     * message are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    @JsonIgnore
    @Override
    public boolean areAllTimeReferencesComplete() {
        return true;
    }

    public static class Builder extends SIGMETImpl_Builder {

        public Builder() {
            setTranslated(false);
        }

        public static Builder from(final SIGMET value) {
            if (value instanceof SIGMETImpl) {
                return ((SIGMETImpl) value).toBuilder();
            } else {
                final Builder retval = new Builder().setPermissibleUsage(value.getPermissibleUsage())
                        .setPermissibleUsageReason(value.getPermissibleUsageReason())
                        .setPermissibleUsageSupplementary(value.getPermissibleUsageSupplementary())
                        .setTranslated(value.isTranslated())
                        .setTranslatedBulletinID(value.getTranslatedBulletinID())
                        .setTranslatedBulletinReceptionTime(value.getTranslatedBulletinReceptionTime())
                        .setTranslationCentreDesignator(value.getTranslationCentreDesignator())
                        .setTranslationCentreName(value.getTranslationCentreName())
                        .setTranslationTime(value.getTranslationTime())
                        .setTranslatedTAC(value.getTranslatedTAC());

                value.getRemarks().map(remarks -> retval.setRemarks(Collections.unmodifiableList(remarks)));
                return retval;
            }
        }
    }
}
