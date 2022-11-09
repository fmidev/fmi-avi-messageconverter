package fi.fmi.avi.model.swx.immutable;

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
import fi.fmi.avi.model.bulletin.MeteorologicalBulletinBuilderHelper;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.swx.SpaceWeatherAdvisory;
import fi.fmi.avi.model.swx.SpaceWeatherBulletin;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "timeStamp", "timeStampFields", "heading", "messages" })
public abstract class SpaceWeatherBulletinImpl implements SpaceWeatherBulletin, Serializable {

    private static final long serialVersionUID = -7494296545788396274L;

    public static Builder builder() {
        return new Builder();
    }

    public static SpaceWeatherBulletinImpl immutableCopyOf(final SpaceWeatherBulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof SpaceWeatherBulletinImpl) {
            return (SpaceWeatherBulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherBulletinImpl> immutableCopyOf(final Optional<SpaceWeatherBulletin> bulletin) {
        return bulletin.map(SpaceWeatherBulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SpaceWeatherBulletinImpl_Builder {
        Builder() {
        }

        public static Builder from(final SpaceWeatherBulletin value) {
            if (value instanceof SpaceWeatherBulletinImpl) {
                return ((SpaceWeatherBulletinImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                MeteorologicalBulletinBuilderHelper.copyFrom(builder, value, //
                        Builder::setHeading, //
                        Builder::addAllMessages, //
                        SpaceWeatherAdvisoryImpl::immutableCopyOf, //
                        Builder::setTimeStamp, //
                        Builder::addAllTimeStampFields);
                return builder;
            }
        }

        @Override
        @JsonDeserialize(as = BulletinHeadingImpl.class)
        public Builder setHeading(final BulletinHeading heading) {
            if (DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML.equals(heading.getDataTypeDesignatorT1ForTAC())) {
                if (!DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_SPACE_WEATHER_ADVISORY.equals(heading.getDataTypeDesignatorT2())) {
                    throw new IllegalArgumentException(
                            "Data type designator T2 of the bulletin heading must " + DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_SPACE_WEATHER_ADVISORY
                                    + " for SpaceWeatherAdvisory");
                }
            } else if (DataTypeDesignatorT1.FORECASTS.equals(heading.getDataTypeDesignatorT1ForTAC())) {
                if (!DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_SPACE_WEATHER.equals(heading.getDataTypeDesignatorT2())) {
                    throw new IllegalArgumentException(
                            "Data type designator T2 of the bulletin heading must " + DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_SPACE_WEATHER
                                    + " for SpaceWeatherAdvisory");
                }
            } else {
                throw new IllegalArgumentException(
                        "Data type designator T1 for TAC of the bulletin heading must be either " + DataTypeDesignatorT1.FORECASTS + " or "
                                + DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML + " for SpaceWeatherAdvisory");
            }
            return super.setHeading(heading);
        }

        @Override
        @JsonDeserialize(contentAs = SpaceWeatherAdvisoryImpl.class)
        @JsonProperty("messages")
        public Builder addMessages(final SpaceWeatherAdvisory... messages) {
            return super.addMessages(messages);
        }

        @Override
        public Builder addMessages(final SpaceWeatherAdvisory message) {
            return super.addMessages(SpaceWeatherAdvisoryImpl.immutableCopyOf(message));
        }
    }
}
