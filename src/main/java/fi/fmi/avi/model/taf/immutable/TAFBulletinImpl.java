package fi.fmi.avi.model.taf.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.MeteorologicalBulletinBuilderHelper;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
@JsonDeserialize(builder = TAFBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"timeStamp", "timeStampFields", "heading", "collectIdentifier", "messages"})
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
            return Builder.copyOf(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<TAFBulletinImpl> immutableCopyOf(final Optional<TAFBulletin> bulletin) {
        return bulletin.map(TAFBulletinImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = BulletinHeadingImpl.class)
    public abstract BulletinHeading getHeading();

    @Override
    @JsonProperty("messages")
    public abstract List<TAF> getMessages();

    public static class Builder extends ImmutableTAFBulletinImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final TAFBulletin value) {
            if (value instanceof TAFBulletinImpl) {
                return ((TAFBulletinImpl) value).toBuilder();
            } else {
                final Builder builder = TAFBulletinImpl.builder();
                MeteorologicalBulletinBuilderHelper.copyFrom(builder, value, //
                        Builder::setHeading, //
                        Builder::addAllMessages, //
                        TAFImpl::immutableCopyOf, //
                        Builder::setTimeStamp, //
                        Builder::addAllTimeStampFields, //
                        Builder::setCollectIdentifier);
                return builder;
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

        /*
         * The FreeBuilder-era Builder overrode setHeading(...) to validate the heading's data type
         * designators as soon as it was set. Immutables' generated setters are final and cannot be
         * overridden (see docs/07-modernization-plan.md), so this validation moved here, into
         * build(), and now runs against the finished value instead of eagerly on set(). The
         * @JsonDeserialize(as=...) hint this setter override used to carry moved onto the abstract
         * getHeading() getter above (see the class body), which is the Immutables-compatible way to
         * give Jackson the same concrete-type hint for builder-based deserialization.
         */
        @Override
        public ImmutableTAFBulletinImpl build() {
            final ImmutableTAFBulletinImpl result = super.build();
            final BulletinHeading heading = result.getHeading();
            if (DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML.equals(heading.getDataTypeDesignatorT1ForTAC())) {
                if (!DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_AERODROME_VT_LONG.equals(heading.getDataTypeDesignatorT2())
                        && !DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_AERODROME_VT_SHORT.equals(heading.getDataTypeDesignatorT2())) {
                    throw new IllegalArgumentException(
                            "Data type designator T2 of the bulletin heading must " + DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_AERODROME_VT_LONG
                                    + " or " + DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_AERODROME_VT_SHORT + " for TAF");
                }
            } else if (DataTypeDesignatorT1.FORECASTS.equals(heading.getDataTypeDesignatorT1ForTAC())) {
                if (!DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG.equals(heading.getDataTypeDesignatorT2())
                        && !DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_SHORT.equals(heading.getDataTypeDesignatorT2())) {
                    throw new IllegalArgumentException("Data type designator T2 of the bulletin heading must be either "
                            + DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_LONG + " or "
                            + DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_AERODROME_VT_SHORT + " for TAF");
                }
            } else {
                throw new IllegalArgumentException(
                        "Data type designator T1 for TAC of the bulletin heading must be either " + DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML + " or "
                                + DataTypeDesignatorT1.FORECASTS + " for TAF");
            }
            return result;
        }
    }
}
