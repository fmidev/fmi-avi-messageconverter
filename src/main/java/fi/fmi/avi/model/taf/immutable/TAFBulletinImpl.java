package fi.fmi.avi.model.taf.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;

@FreeBuilder
@JsonDeserialize(builder = TAFBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "timeStamp", "timeStampFields", "heading", "messages" })
public abstract class TAFBulletinImpl implements TAFBulletin, Serializable {

    private static final long serialVersionUID = 8584221872062469463L;

    public static Builder builder() {
        return new Builder();
    }

    public static TAFBulletinImpl immutableCopyOf(final TAFBulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof TAFBulletinImpl) {
            return (TAFBulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<TAFBulletinImpl> immutableCopyOf(final Optional<TAFBulletin> bulletin) {
        return bulletin.map(TAFBulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFBulletinImpl_Builder {

        @Deprecated
        public Builder() {
        }

        public static Builder from(final TAFBulletin value) {
            if (value instanceof TAFBulletinImpl) {
                return ((TAFBulletinImpl) value).toBuilder();
            } else {
                return TAFBulletinImpl.builder()//
                        .setHeading(BulletinHeadingImpl.immutableCopyOf(value.getHeading()))//
                        .setTimeStamp(value.getTimeStamp())//
                        .addAllTimeStampFields(value.getTimeStampFields())//
                        .addAllMessages(value.getMessages());
            }
        }

        /*
         //NOTE: check disabled, it seems that the length of TAF is no longer relevant in bulletins / Ilkka Rinne 27th Nov 2018
            //check the all the TAFs are short or long consistently with the heading info
        @Override
        public TAFBulletinImpl build() {

            Optional<PartialOrCompleteTimePeriod> validity;
            Optional<Duration> span;
            Duration twelweHours = Duration.ofHours(12);
            for (TAF taf : this.getMessages()) {
                validity = taf.getValidityTime();
                if (validity.isPresent()) {
                    span = validity.get().getValidityTimeSpan();
                    if (span.isPresent()) {
                        if (getHeading().isValidLessThan12Hours() == (span.get().compareTo(twelweHours) >= 0)) {
                            throw new IllegalStateException("TAF contained in bulletin has time span of " + span.get().toHours() + " hours which is "
                                    + "inconsistent with the heading isValidLessThan12Hours: " + getHeading().isValidLessThan12Hours());
                        }
                    }
                }
            }

            return super.build();
        }
        */


        @Override
        @JsonDeserialize(as = BulletinHeadingImpl.class)
        public Builder setHeading(final BulletinHeading heading) {
            if (!DataTypeDesignatorT1.FORECASTS.equals(heading.getDataTypeDesignatorT1ForTAC())) {
                throw new IllegalArgumentException(
                        "Data type designator T1 for TAC of the bulletin heading must be " + DataTypeDesignatorT1.FORECASTS + " " + "for TAF");
            }
            if (!DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG.equals(heading.getDataTypeDesignatorT2())
                    && !DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_SHORT.equals(heading.getDataTypeDesignatorT2())) {
                throw new IllegalArgumentException(
                        "Data type designator T2 of the bulletin heading must be either " + DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG
                                + " or " + DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_SHORT + " for TAF");
            }
            return super.setHeading(heading);
        }

        @Override
        @JsonDeserialize(contentAs = TAFImpl.class)
        @JsonProperty("messages")
        public Builder addMessages(final TAF... messages) {
            return super.addMessages(messages);
        }
    }
}
