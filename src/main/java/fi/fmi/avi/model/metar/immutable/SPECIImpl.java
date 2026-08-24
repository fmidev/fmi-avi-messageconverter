package fi.fmi.avi.model.metar.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReportBuilderHelper;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.SPECI;
import fi.fmi.avi.model.metar.SeaState;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.WindShear;

/**
 * See {@link METARImpl}'s javadoc for the "detached builder" pattern this class (and
 * {@code Builder}) uses instead of extending an Immutables-generated builder directly.
 */
@Value.Immutable
@Value.Style(init = "set*", get = { "is*", "get*" },
        passAnnotations = { com.fasterxml.jackson.databind.annotation.JsonDeserialize.class, com.fasterxml.jackson.annotation.JsonProperty.class },
        typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")
@JsonDeserialize(builder = SPECIImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "reportStatus", "missingMessage", "aerodrome", "issueTime", "automatedStation", "surfaceWind", "visibility", "runwayVisualRanges",
        "presentWeather", "cloud", "airTemperature", "dewpointTemperature", "altimeterSettingQNH", "recentWeather", "windShear", "seaState", "runwayStates",
        "snowClosure", "noSignificantChanges", "trend", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class SPECIImpl extends AbstractMeteorologicalTerminalAirReportImpl<SPECIImpl, SPECIImpl.Builder> implements SPECI, Serializable {
    private static final long serialVersionUID = 1918131429312289735L;

    public static Builder builder() {
        return new Builder();
    }

    public static SPECIImpl immutableCopyOf(final SPECI speci) {
        requireNonNull(speci);
        if (speci instanceof SPECIImpl) {
            return (SPECIImpl) speci;
        } else {
            return Builder.copyOf(speci).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SPECIImpl> immutableCopyOf(final Optional<SPECI> speci) {
        requireNonNull(speci);
        return speci.map(SPECIImpl::immutableCopyOf);
    }

    @Override
    public Builder toBuilder() {
        return new Builder().mergeFrom(this);
    }

    public static class Builder extends AbstractMeteorologicalTerminalAirReportBuilderImpl<SPECIImpl, Builder> {

        Builder() {
            setTranslated(false);
            setReportStatus(ReportStatus.NORMAL);
            setMissingMessage(false);
            setAutomatedStation(false);
            setCeilingAndVisibilityOk(false);
            setSnowClosure(false);
            setNoSignificantChanges(false);
        }

        public static Builder copyOf(final SPECI value) {
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

        public Builder withCompleteForecastTimes(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return mapTrends(trends -> MeteorologicalTerminalAirReportBuilderHelper.completeTrendTimes(trends, reference));
        }

        @Override
        public ImmutableSPECIImpl build() {
            final ImmutableSPECIImpl.Builder delegate = ImmutableSPECIImpl.internalBuilder()//
                    .setAerodrome(getAerodrome())//
                    .setReportStatus(getReportStatus())//
                    .setMissingMessage(isMissingMessage())//
                    .setAutomatedStation(isAutomatedStation())//
                    .setCeilingAndVisibilityOk(isCeilingAndVisibilityOk())//
                    .setSnowClosure(isSnowClosure())//
                    .setNoSignificantChanges(isNoSignificantChanges())//
                    .setTranslated(isTranslated());
            getIssueTime().ifPresent(delegate::setIssueTime);
            getRemarks().ifPresent(delegate::setRemarks);
            getPermissibleUsage().ifPresent(delegate::setPermissibleUsage);
            getPermissibleUsageReason().ifPresent(delegate::setPermissibleUsageReason);
            getPermissibleUsageSupplementary().ifPresent(delegate::setPermissibleUsageSupplementary);
            getTranslatedBulletinID().ifPresent(delegate::setTranslatedBulletinID);
            getTranslatedBulletinReceptionTime().ifPresent(delegate::setTranslatedBulletinReceptionTime);
            getTranslationCentreDesignator().ifPresent(delegate::setTranslationCentreDesignator);
            getTranslationCentreName().ifPresent(delegate::setTranslationCentreName);
            getTranslationTime().ifPresent(delegate::setTranslationTime);
            getTranslatedTAC().ifPresent(delegate::setTranslatedTAC);
            getAirTemperature().ifPresent(delegate::setAirTemperature);
            getDewpointTemperature().ifPresent(delegate::setDewpointTemperature);
            getAltimeterSettingQNH().ifPresent(delegate::setAltimeterSettingQNH);
            getSurfaceWind().ifPresent(delegate::setSurfaceWind);
            getVisibility().ifPresent(delegate::setVisibility);
            getRunwayVisualRanges().ifPresent(delegate::setRunwayVisualRanges);
            getPresentWeather().ifPresent(delegate::setPresentWeather);
            getClouds().ifPresent(delegate::setClouds);
            getRecentWeather().ifPresent(delegate::setRecentWeather);
            getWindShear().ifPresent(delegate::setWindShear);
            getSeaState().ifPresent(delegate::setSeaState);
            getRunwayStates().ifPresent(delegate::setRunwayStates);
            getTrends().ifPresent(delegate::setTrends);
            getColorState().ifPresent(delegate::setColorState);
            return delegate.build();
        }

        @Override
        @JsonProperty("aerodrome")
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            super.setAerodrome(aerodrome);
            MeteorologicalTerminalAirReportBuilderHelper.afterSetAerodrome(this, aerodrome);
            return this;
        }

        @Override
        @JsonProperty("airTemperature")
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setAirTemperature(final NumericMeasure airTemperature) {
            return super.setAirTemperature(airTemperature);
        }

        @Override
        @JsonProperty("dewpointTemperature")
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setDewpointTemperature(final NumericMeasure dewpointTemperature) {
            return super.setDewpointTemperature(dewpointTemperature);
        }

        @Override
        @JsonProperty("altimeterSettingQNH")
        @JsonDeserialize(as = NumericMeasureImpl.class)
        public Builder setAltimeterSettingQNH(final NumericMeasure altimeterSettingQNH) {
            return super.setAltimeterSettingQNH(altimeterSettingQNH);
        }

        @Override
        @JsonProperty("surfaceWind")
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
        @JsonProperty("runwayVisualRanges")
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
        @JsonProperty("clouds")
        @JsonDeserialize(as = ObservedCloudsImpl.class)
        public Builder setClouds(final ObservedClouds clouds) {
            return super.setClouds(clouds);
        }

        @Override
        @JsonProperty("recentWeather")
        @JsonDeserialize(contentAs = WeatherImpl.class)
        public Builder setRecentWeather(final List<Weather> weather) {
            return super.setRecentWeather(weather);
        }

        @Override
        @JsonProperty("windShear")
        @JsonDeserialize(as = WindShearImpl.class)
        public Builder setWindShear(final WindShear windShear) {
            return super.setWindShear(windShear);
        }

        @Override
        @JsonProperty("seaState")
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
