package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT1;
import fi.fmi.avi.model.bulletin.DataTypeDesignatorT2;
import fi.fmi.avi.model.bulletin.MeteorologicalBulletinBuilderHelper;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.swx.amd79.SpaceWeatherAmd79Bulletin;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAdvisoryAmd82;
import fi.fmi.avi.model.swx.amd82.SpaceWeatherAmd82Bulletin;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = SpaceWeatherAmd82BulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"timeStamp", "timeStampFields", "heading", "messages"})
public abstract class SpaceWeatherAmd82BulletinImpl implements SpaceWeatherAmd82Bulletin, Serializable {

    private static final long serialVersionUID = -7494296545788396274L;

    public static Builder builder() {
        return new Builder();
    }

    public static SpaceWeatherAmd82BulletinImpl immutableCopyOf(final SpaceWeatherAmd82Bulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof SpaceWeatherAmd82BulletinImpl) {
            return (SpaceWeatherAmd82BulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SpaceWeatherAmd82BulletinImpl> immutableCopyOf(final Optional<SpaceWeatherAmd82Bulletin> bulletin) {
        return bulletin.map(SpaceWeatherAmd82BulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SpaceWeatherAmd82BulletinImpl_Builder {
        Builder() {
        }

        public static Builder from(final SpaceWeatherAmd82Bulletin value) {
            if (value instanceof SpaceWeatherAmd82BulletinImpl) {
                return ((SpaceWeatherAmd82BulletinImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                MeteorologicalBulletinBuilderHelper.copyFrom(builder, value, //
                        Builder::setHeading, //
                        Builder::addAllMessages, //
                        SpaceWeatherAdvisoryAmd82Impl::immutableCopyOf, //
                        Builder::setTimeStamp, //
                        Builder::addAllTimeStampFields);
                return builder;
            }
        }

        public static Builder fromAmd79(final SpaceWeatherAmd79Bulletin value) {
            final Builder builder = builder();
            MeteorologicalBulletinBuilderHelper.copyAndTransform(builder, value,
                    Builder::setHeading,
                    Builder::addAllMessages,
                    message -> SpaceWeatherAdvisoryAmd82Impl.Builder.fromAmd79(message).build(),
                    Builder::setTimeStamp,
                    Builder::addAllTimeStampFields);
            return builder;
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
        @JsonDeserialize(contentAs = SpaceWeatherAdvisoryAmd82Impl.class)
        @JsonProperty("messages")
        public Builder addMessages(final SpaceWeatherAdvisoryAmd82... messages) {
            return super.addMessages(messages);
        }

        @Override
        public Builder addMessages(final SpaceWeatherAdvisoryAmd82 message) {
            return super.addMessages(SpaceWeatherAdvisoryAmd82Impl.immutableCopyOf(message));
        }
    }
}
