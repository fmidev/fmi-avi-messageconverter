package fi.fmi.avi.model.metar.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.metar.HorizontalVisibility;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReportBuilder;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReportBuilderHelper;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.SPECI;
import fi.fmi.avi.model.metar.SeaState;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.WindShear;

@FreeBuilder
@JsonDeserialize(builder = SPECIImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "status", "aerodrome", "issueTime", "automatedStation", "surfaceWind", "visibility", "runwayVisualRanges", "presentWeather", "cloud",
        "airTemperature", "dewpointTemperature", "altimeterSettingQNH", "recentWeather", "windShear", "seaState", "runwayStates", "snowClosure",
        "noSignificantChanges", "trend", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class SPECIImpl extends AbstractMeteorologicalTerminalAirReportImpl implements SPECI, Serializable {
    private static final long serialVersionUID = 1918131429312289735L;

    public static Builder builder() {
        return new Builder();
    }

    public static SPECIImpl immutableCopyOf(final SPECI speci) {
        requireNonNull(speci);
        if (speci instanceof SPECIImpl) {
            return (SPECIImpl) speci;
        } else {
            return Builder.from(speci).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SPECIImpl> immutableCopyOf(final Optional<SPECI> speci) {
        requireNonNull(speci);
        return speci.map(SPECIImpl::immutableCopyOf);
    }

    @Override
    public abstract Builder toBuilder();

    public static class Builder extends SPECIImpl_Builder implements MeteorologicalTerminalAirReportBuilder<SPECIImpl, Builder> {

        @Deprecated
        public Builder() {
            setTranslated(false);
            setAutomatedStation(false);
            setCeilingAndVisibilityOk(false);
            setSnowClosure(false);
            setNoSignificantChanges(false);
        }

        public static Builder from(final SPECI value) {
            if (value instanceof SPECIImpl) {
                return ((SPECIImpl) value).toBuilder();
            }
            return SPECIImpl.builder().copyFrom(value);
        }

        @Override
        public Builder copyFrom(final MeteorologicalTerminalAirReport value) {
            if (value instanceof SPECIImpl) {
                return clear().mergeFrom((SPECIImpl) value);
            }
            MeteorologicalTerminalAirReportBuilderHelper.copyFrom(this, value);
            return this;
        }

        @Override
        public Builder withCompleteForecastTimes(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return mapTrends(trends -> MeteorologicalTerminalAirReportBuilderHelper.completeTrendTimes(trends, reference));
        }

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public SPECIImpl.Builder setAerodrome(final Aerodrome aerodrome) {
            final SPECIImpl.Builder retval = super.setAerodrome(aerodrome);
            MeteorologicalTerminalAirReportBuilderHelper.afterSetAerodrome(this, aerodrome);
            return retval;
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public SPECIImpl.Builder setAirTemperature(final NumericMeasure airTemperature) {
            return super.setAirTemperature(airTemperature);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public SPECIImpl.Builder setDewpointTemperature(final NumericMeasure dewpointTemperature) {
            return super.setDewpointTemperature(dewpointTemperature);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public SPECIImpl.Builder setAltimeterSettingQNH(final NumericMeasure altimeterSettingQNH) {
            return super.setAltimeterSettingQNH(altimeterSettingQNH);
        }

        @Override
        @JsonDeserialize(as = ObservedSurfaceWindImpl.class)
        public SPECIImpl.Builder setSurfaceWind(final ObservedSurfaceWind surfaceWind) {
            return super.setSurfaceWind(surfaceWind);
        }

        @Override
        @JsonDeserialize(as = HorizontalVisibilityImpl.class)
        public SPECIImpl.Builder setVisibility(final HorizontalVisibility visibility) {
            return super.setVisibility(visibility);
        }

        @Override
        @JsonDeserialize(contentAs = RunwayVisualRangeImpl.class)
        public SPECIImpl.Builder setRunwayVisualRanges(final List<RunwayVisualRange> runwayVisualRanges) {
            return super.setRunwayVisualRanges(runwayVisualRanges);
        }

        @Override
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public SPECIImpl.Builder setPresentWeather(final List<Weather> weather) {
            return super.setPresentWeather(weather);
        }

        @Override
        @JsonDeserialize(as = ObservedCloudsImpl.class)
        public SPECIImpl.Builder setClouds(final ObservedClouds clouds) {
            return super.setClouds(clouds);
        }

        @Override
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public SPECIImpl.Builder setRecentWeather(final List<Weather> weather) {
            return super.setRecentWeather(weather);
        }

        @Override
        @JsonDeserialize(as = WindShearImpl.class)
        public SPECIImpl.Builder setWindShear(final WindShear windShear) {
            return super.setWindShear(windShear);
        }

        @Override
        @JsonDeserialize(as = SeaStateImpl.class)
        public SPECIImpl.Builder setSeaState(final SeaState seaState) {
            return super.setSeaState(seaState);
        }

        @Override
        @JsonDeserialize(contentAs = RunwayStateImpl.class)
        public SPECIImpl.Builder setRunwayStates(final List<RunwayState> runwayStates) {
            return super.setRunwayStates(runwayStates);
        }

        @Override
        @JsonDeserialize(contentAs = TrendForecastImpl.class)
        public SPECIImpl.Builder setTrends(final List<TrendForecast> trends) {
            return super.setTrends(trends);
        }
    }

}
