package fi.fmi.avi.model.metar.immutable;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.RunwayDirectionImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.metar.HorizontalVisibility;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.ObservedSurfaceWind;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.SPECI;
import fi.fmi.avi.model.metar.SeaState;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.model.metar.WindShear;

@FreeBuilder
@JsonDeserialize(builder = METARImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "status", "aerodrome", "issueTime", "automatedStation", "surfaceWind", "visibility", "runwayVisualRanges", "presentWeather", "cloud",
        "airTemperature", "dewpointTemperature", "altimeterSettingQNH", "recentWeather", "windShear", "seaState", "runwayStates", "snowClosure",
        "noSignificantChanges", "trend", "remarks", "permissibleUsage", "permissibleUsageReason", "permissibleUsageSupplementary", "translated",
        "translatedBulletinID", "translatedBulletinReceptionTime", "translationCentreDesignator", "translationCentreName", "translationTime", "translatedTAC" })
public abstract class METARImpl implements METAR, Serializable {

    public static METARImpl immutableCopyOf(final MeteorologicalTerminalAirReport msg) {
        requireNonNull(msg);
        if (msg instanceof METAR) {
            if (msg instanceof METARImpl) {
                return (METARImpl) msg;
            } else {
                return Builder.from((METAR) msg).build();
            }
        } else if (msg instanceof SPECI) {
            try {
                InvocationHandler handler = Proxy.getInvocationHandler(msg);
                if (handler instanceof SPECIInvocationHandler) {
                    //msg is a SPECI proxy, return the internal delegate:
                    return ((SPECIInvocationHandler) handler).getDelegate();
                }
            } catch (IllegalArgumentException iae) {
                //NOOP: msg is not a SPECI proxy instance, fallback to regular copy
            }
            return Builder.from((SPECI) msg).build();
        } else {
            throw new IllegalArgumentException("original is neither a METAR or a SPECI, cannot create a copy");
        }
    }

    public static Optional<METARImpl> immutableCopyOf(final Optional<MeteorologicalTerminalAirReport> metar) {
        requireNonNull(metar);
        return metar.map(METARImpl::immutableCopyOf);
    }

    protected static List<TrendForecast> completeTrendTimes(final List<TrendForecast> trendForecasts, final ZonedDateTime reference) {
        requireNonNull(trendForecasts, "trendForecasts");
        requireNonNull(reference, "reference");
        if (trendForecasts.isEmpty()) {
            return Collections.emptyList();
        }
        final List<TrendForecast> builder = new ArrayList<>(trendForecasts.size());
        List<PartialOrCompleteTime> times = new ArrayList<>(trendForecasts.size());
        for (final TrendForecast forecast : trendForecasts) {
            if (forecast.getPeriodOfChange().isPresent()) {
                times.add(forecast.getPeriodOfChange().get());
            } else if (forecast.getInstantOfChange().isPresent()) {
                times.add(forecast.getInstantOfChange().get());
            } else {
                times.add(null);
            }
        }
        times = PartialOrCompleteTimePeriod.completeAscendingPartialTimes(times, reference);
        for (int i = 0; i < times.size(); i++) {
            final PartialOrCompleteTime time = times.get(i);
            if (time instanceof PartialOrCompleteTimePeriod) {
                builder.add(TrendForecastImpl.Builder.from(trendForecasts.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
            } else if (time instanceof PartialOrCompleteTimeInstant) {
                builder.add(TrendForecastImpl.Builder.from(trendForecasts.get(i)).setInstantOfChange((PartialOrCompleteTimeInstant) time).build());
            }
        }
        return Collections.unmodifiableList(builder);
    }

    public abstract Builder toBuilder();

    /**
     * Returns true if issue time, valid time and all other time references contained in this
     * message are full ZonedDateTime instances.
     *
     * @return true if all time references are complete, false otherwise
     */
    @Override
    @JsonIgnore
    public boolean areAllTimeReferencesComplete() {
        if (!this.getIssueTime().getCompleteTime().isPresent()) {
            return false;
        }
        if (this.getTrends().isPresent()) {
            for (TrendForecast trend : this.getTrends().get()) {
                if (trend.getPeriodOfChange().isPresent()) {
                    if (!trend.getPeriodOfChange().get().isComplete()) {
                        return false;
                    }
                } else if (trend.getInstantOfChange().isPresent()) {
                    if (!trend.getInstantOfChange().get().getCompleteTime().isPresent()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    @JsonIgnore
    public boolean allAerodromeReferencesContainPosition() {
        Aerodrome ad = this.getAerodrome();
        if (!ad.getFieldElevationValue().isPresent()) {
            return false;
        }
        if (this.getRunwayStates().isPresent()) {
            for (RunwayState state : this.getRunwayStates().get()) {
                if (state.getRunwayDirection().isPresent()) {
                    if (state.getRunwayDirection().get().getAssociatedAirportHeliport().isPresent()) {
                        ad = state.getRunwayDirection().get().getAssociatedAirportHeliport().get();
                        if (!ad.getReferencePoint().isPresent()) {
                            return false;
                        }
                    }
                }
            }
        }

        if (this.getRunwayVisualRanges().isPresent()) {
            for (RunwayVisualRange range : this.getRunwayVisualRanges().get()) {
                if (range.getRunwayDirection().getAssociatedAirportHeliport().isPresent()) {
                    ad = range.getRunwayDirection().getAssociatedAirportHeliport().get();
                    if (!ad.getReferencePoint().isPresent()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static class Builder extends METARImpl_Builder {
        public Builder() {
            setTranslated(false);
            setAutomatedStation(false);
            setCeilingAndVisibilityOk(false);
            setRoutineDelayed(false);
            setSnowClosure(false);
            setNoSignificantChanges(false);
        }

        private static Builder from(final MeteorologicalTerminalAirReport value) {
            if (value instanceof METARImpl) {
                return ((METARImpl) value).toBuilder();
            } else if (value instanceof SPECI) {
                try {
                    InvocationHandler handler = Proxy.getInvocationHandler(value);
                    if (handler instanceof SPECIInvocationHandler) {
                        //value is a SPECI proxy, return a Builder of the internal delegate:
                        return ((SPECIInvocationHandler) handler).getDelegate().toBuilder();
                    }
                } catch (IllegalArgumentException iae) {
                    //NOOP, non-proxy SPECIs fallback to using the full METARImpl.Builder
                }
            }
            //From AviationWeatherMessage:
            METARImpl.Builder retval = new METARImpl.Builder()//
                    .setIssueTime(value.getIssueTime())
                    .setPermissibleUsage(value.getPermissibleUsage())
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

            //From AerodromeWeatherMessage:
            retval.setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()));

            //From MeteorologicalTerminalAirReport:
            retval.setAutomatedStation(value.isAutomatedStation())
                    .setStatus(value.getStatus())//
                    .setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())//
                    .setSnowClosure(value.isSnowClosure())///
                    .setAirTemperature(NumericMeasureImpl.immutableCopyOf(value.getAirTemperature()))
                    .setDewpointTemperature(NumericMeasureImpl.immutableCopyOf(value.getDewpointTemperature()))
                    .setAltimeterSettingQNH(NumericMeasureImpl.immutableCopyOf(value.getAltimeterSettingQNH()))
                    .setSurfaceWind(ObservedSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))
                    .setVisibility(HorizontalVisibilityImpl.immutableCopyOf(value.getVisibility()))
                    .setClouds(ObservedCloudsImpl.immutableCopyOf(value.getClouds()))
                    .setWindShear(WindShearImpl.immutableCopyOf(value.getWindShear()))
                    .setSeaState(SeaStateImpl.immutableCopyOf(value.getSeaState()))
                    .setColorState(value.getColorState())
                    .setNoSignificantChanges(value.isNoSignificantChanges());

            value.getRunwayVisualRanges()
                    .map(ranges -> retval.setRunwayVisualRanges(
                            Collections.unmodifiableList(ranges.stream().map(RunwayVisualRangeImpl::immutableCopyOf).collect(Collectors.toList()))));

            value.getPresentWeather()
                    .map(weather -> retval.setPresentWeather(
                            Collections.unmodifiableList(weather.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))));

            value.getRecentWeather()
                    .map(weather -> retval.setRecentWeather(
                            Collections.unmodifiableList(weather.stream().map(WeatherImpl::immutableCopyOf).collect(Collectors.toList()))

                    ));

            value.getRunwayStates()
                    .map(states -> retval.setRunwayStates(
                            Collections.unmodifiableList(states.stream().map(RunwayStateImpl::immutableCopyOf).collect(Collectors.toList()))));

            value.getTrends()
                    .map(trends -> retval.setTrends(
                            Collections.unmodifiableList(trends.stream().map(TrendForecastImpl::immutableCopyOf).collect(Collectors.toList()))));
            return retval;
        }

        public static Builder from(final METAR value) {
            Builder retval = from((MeteorologicalTerminalAirReport) value);
            retval.setRoutineDelayed(value.isRoutineDelayed());
            return retval;
        }

        public static Builder from(final SPECI value) {
            Builder retval = from((MeteorologicalTerminalAirReport) value);
            retval.setRoutineDelayed(false);
            return retval;
        }

        public SPECI buildAsSPECI() {
            if (isRoutineDelayed()) {
                throw new IllegalStateException("Routine delayed (RTD) is true, cannot build as SPECI");
            }
            return (SPECI) Proxy.newProxyInstance(SPECI.class.getClassLoader(), new Class[] { SPECI.class }, new SPECIInvocationHandler(this.build()));
        }

        public SPECI buildPartialAsSPECI() {
            if (isRoutineDelayed()) {
                throw new IllegalStateException("Routine delayed (RTD) is true, cannot build as SPECI");
            }
            return (SPECI) Proxy.newProxyInstance(SPECI.class.getClassLoader(), new Class[] { SPECI.class }, new SPECIInvocationHandler(this.buildPartial()));
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) {
            return mutateIssueTime((input) -> input.completePartialAt(yearMonth));
        }

        public Builder withCompleteIssueTimeNear(final ZonedDateTime reference) {
            return mutateIssueTime((input) -> input.completePartialNear(reference));
        }

        public Builder withCompleteForecastTimes(final YearMonth issueYearMonth, final int issueDay, final int issueHour, final ZoneId tz)
                throws IllegalArgumentException {
            requireNonNull(issueYearMonth, "issueYearMonth");
            requireNonNull(tz, "tz");
            return withCompleteForecastTimes(
                    ZonedDateTime.of(LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz));
        }

        public Builder withCompleteForecastTimes(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return mapTrends(trends -> completeTrendTimes(trends, reference));
        }

        public Builder withAllTimesComplete(final ZonedDateTime reference) {
            requireNonNull(reference, "reference");
            return withCompleteIssueTimeNear(reference)//
                    .withCompleteForecastTimes(reference);
        }

        @Override
        @JsonDeserialize(as = AerodromeImpl.class)
        public Builder setAerodrome(final Aerodrome aerodrome) {
            Builder retval = super.setAerodrome(aerodrome);
            if (getRunwayStates().isPresent()) {
                List<RunwayState> oldStates = getRunwayStates().get();
                List<RunwayState> newStates = new ArrayList<>(oldStates.size());
                for (RunwayState state : oldStates) {
                    if (state.getRunwayDirection().isPresent()) {
                        if (state.getRunwayDirection().get().getAssociatedAirportHeliport().isPresent()) {
                            RunwayStateImpl.Builder builder = RunwayStateImpl.immutableCopyOf(state).toBuilder();
                            builder.setRunwayDirection(RunwayDirectionImpl.immutableCopyOf(builder.getRunwayDirection().get())
                                    .toBuilder()
                                    .setAssociatedAirportHeliport(aerodrome)
                                    .build());

                            newStates.add(builder.build());
                        }
                    }
                }
                setRunwayStates(newStates);
            }
            if (getRunwayVisualRanges().isPresent()) {
                List<RunwayVisualRange> oldRanges = getRunwayVisualRanges().get();
                List<RunwayVisualRange> newRanges = new ArrayList<>(oldRanges.size());
                for (RunwayVisualRange range : oldRanges) {
                    if (range.getRunwayDirection().getAssociatedAirportHeliport().isPresent()) {
                        RunwayVisualRangeImpl.Builder builder = RunwayVisualRangeImpl.immutableCopyOf(range).toBuilder();
                        builder.setRunwayDirection(
                                RunwayDirectionImpl.immutableCopyOf(builder.getRunwayDirection()).toBuilder().setAssociatedAirportHeliport(aerodrome).build());
                        newRanges.add(builder.build());
                    }
                }
                setRunwayVisualRanges(newRanges);
            }
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

    static class SPECIInvocationHandler implements InvocationHandler {
        private final METARImpl delegate;

        SPECIInvocationHandler(final METARImpl delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                Method delegateMethod = METARImpl.class.getMethod(method.getName(), method.getParameterTypes());
                return delegateMethod.invoke(delegate, args);
            } catch (NoSuchMethodException nsme) {
                throw new RuntimeException("SPECI method " + method.getName() + "(" + Arrays.toString(method.getParameterTypes()) + ") not implemented by "
                        + METARImpl.class.getSimpleName() + ", cannot delegate. Make sure that " + METARImpl.class.getCanonicalName()
                        + " implements all SPECI methods");
            }
        }

        METARImpl getDelegate() {
            return delegate;
        }

    }

}
