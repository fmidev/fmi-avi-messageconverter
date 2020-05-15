package fi.fmi.avi.model.SpaceWeatherAdvisory.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherAdvisory;
import fi.fmi.avi.model.SpaceWeatherAdvisory.SpaceWeatherBulletin;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;

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
        @Deprecated
        public Builder() {
        }

        public static Builder from(final SpaceWeatherBulletin value) {
            if (value instanceof SpaceWeatherBulletinImpl) {
                return ((SpaceWeatherBulletinImpl) value).toBuilder();
            } else {
                return builder()//
                        .setHeading(BulletinHeadingImpl.immutableCopyOf(value.getHeading()))//
                        .setTimeStamp(value.getTimeStamp())//
                        .addAllTimeStampFields(value.getTimeStampFields())//
                        .addAllMessages(value.getMessages());
            }
        }

        @Override
        @JsonDeserialize(as = BulletinHeadingImpl.class)
        public Builder setHeading(final BulletinHeading heading) {
            if (!DataTypeDesignatorT1.FORECASTS.equals(heading.getDataTypeDesignatorT1ForTAC())) {
                throw new IllegalArgumentException(
                        "Data type designator T1 for TAC of the bulletin heading must be " + DataTypeDesignatorT1.FORECASTS + " " + "for SpaceWeatherAdvisory");
            }
            if (!DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_SPACE_WEATHER.equals(heading.getDataTypeDesignatorT2())
                    && !DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_SPACE_WEATHER_ADVISORY.equals(heading.getDataTypeDesignatorT2())) {
                throw new IllegalArgumentException(
                        "Data type designator T2 of the bulletin heading must be either " + DataTypeDesignatorT2.ForecastsDataTypeDesignatorT2.FCT_SPACE_WEATHER
                                + " or " + DataTypeDesignatorT2.XMLDataTypeDesignatorT2.XML_SPACE_WEATHER_ADVISORY + " for SpaceWeatherAdvisory");
            }
            return super.setHeading(heading);
        }

        @Override
        @JsonDeserialize(contentAs = SpaceWeatherAdvisoryImpl.class)
        @JsonProperty("messages")
        public Builder addMessages(final SpaceWeatherAdvisory... messages) {
            return super.addMessages(messages);
        }
    }
}
