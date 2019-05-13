package fi.fmi.avi.model.taf.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFReference;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
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
            return Builder.from(tafReference).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<TAFReferenceImpl> immutableCopyOf(final Optional<TAFReference> tafReference) {
        requireNonNull(tafReference);
        return tafReference.map(TAFReferenceImpl::immutableCopyOf);
    }

    public static TAFReferenceImpl from(final TAF taf) {
        return Builder.from(taf).build();
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFReferenceImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final TAFReference value) {
            if (value instanceof TAFReferenceImpl) {
                return ((TAFReferenceImpl) value).toBuilder();
            } else {
                return builder()//
                        .setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()))
                        .setStatus(value.getStatus())
                        .setIssueTime(value.getIssueTime())
                        .setValidityTime(value.getValidityTime());
            }
        }

        public static Builder from(final TAF taf) {
            requireNonNull(taf, "taf");
            return builder()//
                    .setAerodrome(AerodromeImpl.immutableCopyOf(taf.getAerodrome()))//
                    .setStatus(taf.getStatus())//
                    .setIssueTime(taf.getIssueTime())//
                    .setValidityTime(taf.getValidityTime());
        }

        public static Builder from(final TAFImpl.Builder taf) {
            requireNonNull(taf, "taf");
            return builder()//
                    .setAerodrome(AerodromeImpl.immutableCopyOf(taf.getAerodrome()))//
                    .setStatus(taf.getStatus())//
                    .setIssueTime(taf.getIssueTime())//
                    .setValidityTime(taf.getValidityTime());
        }

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            return super.setAerodrome(aerodrome);
        }
    }
}
