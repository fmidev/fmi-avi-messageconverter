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
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReportBuilder;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReportBuilderHelper;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.SeaState;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.WindShear;

@FreeBuilder
@JsonDeserialize(builder = METARImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "reportStatus", "missingMessage", "aerodrome", "issueTime", "automatedStation", "surfaceWind", "visibility", "runwayVisualRanges",
        "presentWeather", "cloud", "airTemperature", "dewpointTemperature", "altimeterSettingQNH", "recentWeather", "windShear", "seaState", "runwayStates",
        "snowClosure", "noSignificantChanges", "trend", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class METARImpl extends AbstractMeteorologicalTerminalAirReportImpl<METARImpl, METARImpl.Builder> implements METAR, Serializable {

    private static final long serialVersionUID = 5959988117998705772L;

    public static Builder builder() {
        return new Builder();
    }

    public static METARImpl immutableCopyOf(final METAR metar) {
        requireNonNull(metar);
        if (metar instanceof METARImpl) {
            return (METARImpl) metar;
        } else {
            return Builder.from(metar).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<METARImpl> immutableCopyOf(final Optional<METAR> metar) {
        requireNonNull(metar);
        return metar.map(METARImpl::immutableCopyOf);
    }

    @Override
    public abstract Builder toBuilder();

    public static class Builder extends METARImpl_Builder implements MeteorologicalTerminalAirReportBuilder<METARImpl, Builder> {

        @Deprecated
        public Builder() {
            setTranslated(false);
            setReportStatus(ReportStatus.NORMAL);
            setMissingMessage(false);
            setAutomatedStation(false);
            setCeilingAndVisibilityOk(false);
            setRoutineDelayed(false);
            setSnowClosure(false);
            setNoSignificantChanges(false);
        }

        public static Builder from(final METAR value) {
            if (value instanceof METARImpl) {
                return ((METARImpl) value).toBuilder();
            }
            return METARImpl.builder().copyFrom(value);
        }

        @Override
        public Builder copyFrom(final MeteorologicalTerminalAirReport value) {
            if (value instanceof METARImpl) {
                return clear().mergeFrom((METARImpl) value);
            }
            MeteorologicalTerminalAirReportBuilderHelper.copyFrom(this, value);
            if (value instanceof METAR) {
                final METAR fromMetar = (METAR) value;
                setRoutineDelayed(fromMetar.isRoutineDelayed());
            }
            return this;
        }

        @Override
        public Builder withCompleteForecastTimes(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return mapTrends(trends -> MeteorologicalTerminalAirReportBuilderHelper.completeTrendTimes(trends, reference));
        }

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            final Builder retval = super.setAerodrome(aerodrome);
            MeteorologicalTerminalAirReportBuilderHelper.afterSetAerodrome(this, aerodrome);
            return retval;
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setAirTemperature(final NumericMeasure airTemperature) {
            return super.setAirTemperature(airTemperature);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setDewpointTemperature(final NumericMeasure dewpointTemperature) {
            return super.setDewpointTemperature(dewpointTemperature);
        }

        @Override
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setAltimeterSettingQNH(final NumericMeasure altimeterSettingQNH) {
            return super.setAltimeterSettingQNH(altimeterSettingQNH);
        }

        @Override
        @JsonDeserialize(as = ObservedSurfaceWindImpl.class)
        public Builder setSurfaceWind(final ObservedSurfaceWind surfaceWind) {
            return super.setSurfaceWind(surfaceWind);
        }

        @Override
        @JsonDeserialize(as = HorizontalVisibilityImpl.class)
        public Builder setVisibility(final HorizontalVisibility visibility) {
            return super.setVisibility(visibility);
        }

        @Override
        @JsonDeserialize(contentAs = RunwayVisualRangeImpl.class)
        public Builder setRunwayVisualRanges(final List<RunwayVisualRange> runwayVisualRanges) {
            return super.setRunwayVisualRanges(runwayVisualRanges);
        }

        @Override
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public Builder setPresentWeather(final List<Weather> weather) {
            return super.setPresentWeather(weather);
        }

        @Override
        @JsonDeserialize(as = ObservedCloudsImpl.class)
        public Builder setClouds(final ObservedClouds clouds) {
            return super.setClouds(clouds);
        }

        @Override
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public Builder setRecentWeather(final List<Weather> weather) {
            return super.setRecentWeather(weather);
        }

        @Override
        @JsonDeserialize(as = WindShearImpl.class)
        public Builder setWindShear(final WindShear windShear) {
            return super.setWindShear(windShear);
        }

        @Override
        @JsonDeserialize(as = SeaStateImpl.class)
        public Builder setSeaState(final SeaState seaState) {
            return super.setSeaState(seaState);
        }

        @Override
        @JsonDeserialize(contentAs = RunwayStateImpl.class)
        public Builder setRunwayStates(final List<RunwayState> runwayStates) {
            return super.setRunwayStates(runwayStates);
        }

        @Override
        @JsonDeserialize(contentAs = TrendForecastImpl.class)
        public Builder setTrends(final List<TrendForecast> trends) {
            return super.setTrends(trends);
        }
    }

}
