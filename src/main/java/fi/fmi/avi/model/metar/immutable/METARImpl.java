package fi.fmi.avi.model.metar.immutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fi.fmi.avi.model.metar.SPECI;
import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.PartialOrCompleteTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import fi.fmi.avi.model.immutable.WeatherImpl;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.MeteorologicalTerminalAirReport;
import fi.fmi.avi.model.metar.TrendForecast;

import javax.swing.plaf.ButtonUI;

@FreeBuilder
@JsonDeserialize(builder = METARImpl.Builder.class)
public abstract class METARImpl implements METAR, Serializable, MeteorologicalTerminalAirReport {

    public static METARImpl immutableCopyOf(final MeteorologicalTerminalAirReport msg) {
        checkNotNull(msg);
        if (msg instanceof METAR) {
            if (msg instanceof METARImpl) {
                return (METARImpl) msg;
            } else {
                return Builder.from((METAR)msg).build();
            }
        } else if (msg instanceof SPECI) {
            try {
                InvocationHandler handler = Proxy.getInvocationHandler(msg);
                if (handler instanceof SPECIInvocationHandler) {
                    //msg is a SPECI proxy, return the internal delegate:
                    return ((SPECIInvocationHandler) handler).getDelegate();
                }
            } catch (IllegalArgumentException iae) {}
            //msg is not a SPECI proxy instance, fallback to regular copy:
            return Builder.from((SPECI)msg).build();
        } else {
            throw new IllegalArgumentException("original is neither a METAR or a SPECI, cannot create a copy");
        }
    }

    public static Optional<METARImpl> immutableCopyOf(final Optional<MeteorologicalTerminalAirReport> metar) {
        checkNotNull(metar);
        return metar.map(METARImpl::immutableCopyOf);
    }


    protected static Optional<List<TrendForecast>> getTimeCompletedTrends(final List<TrendForecast> oldTrends, final YearMonth issueYearMonth, int issueDay,
            int issueHour, final ZoneId tz) throws IllegalArgumentException {
        Optional<List<TrendForecast>> retval = Optional.empty();
        if (oldTrends != null) {
            final ZonedDateTime approximateIssueTime = ZonedDateTime.of(
                    LocalDateTime.of(issueYearMonth.getYear(), issueYearMonth.getMonth(), issueDay, issueHour, 0), tz);
            retval = Optional.of(new ArrayList<>());
            List<PartialOrCompleteTime> list = new ArrayList<>();
            for (TrendForecast fct : oldTrends) {
                if (fct.getPeriodOfChange().isPresent()) {
                    list.add(fct.getPeriodOfChange().get());
                } else if (fct.getInstantOfChange().isPresent()) {
                    list.add(fct.getInstantOfChange().get());
                }
            }
            list = PartialOrCompleteTimePeriod.completePartialTimeReferenceList(list, approximateIssueTime);
            for (int i = 0; i < list.size(); i++) {
                PartialOrCompleteTime time = list.get(i);
                if (time instanceof PartialOrCompleteTimePeriod) {
                    retval.get().add(TrendForecastImpl.Builder.from(oldTrends.get(i)).setPeriodOfChange((PartialOrCompleteTimePeriod) time).build());
                } else if (time instanceof PartialOrCompleteTimeInstant) {
                    retval.get().add(TrendForecastImpl.Builder.from(oldTrends.get(i)).setInstantOfChange((PartialOrCompleteTimeInstant) time).build());
                }
            }
        }
        return retval;
    }

    public abstract Builder toBuilder();

    public static class Builder extends METARImpl_Builder {
        public Builder() {
            setRoutineDelayed(false);
        }

        public SPECI buildAsSPECI() {
            checkState(!isRoutineDelayed(),"Routine delayed (RTD) is true, cannot build as SPECI");
            return (SPECI) Proxy.newProxyInstance(SPECI.class.getClassLoader(), new Class[]{SPECI.class}, new SPECIInvocationHandler(this.build()));
        }

        public SPECI buildPartialAsSPECI() {
            checkState(!isRoutineDelayed(),"Routine delayed (RTD) is true, cannot build as SPECI");
            return (SPECI) Proxy.newProxyInstance(SPECI.class.getClassLoader(), new Class[]{SPECI.class}, new SPECIInvocationHandler(this.buildPartial()));
        }

        private static Builder from(final MeteorologicalTerminalAirReport value) {
            //From AviationWeatherMessage:
            METARImpl.Builder retval = new METARImpl.Builder().setIssueTime(value.getIssueTime())
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
                    .setStatus(value.getStatus())
                    .setCeilingAndVisibilityOk(value.isCeilingAndVisibilityOk())
                    .setAirTemperature(NumericMeasureImpl.immutableCopyOf(value.getAirTemperature()))
                    .setDewpointTemperature(NumericMeasureImpl.immutableCopyOf(value.getDewpointTemperature()))
                    .setAltimeterSettingQNH(NumericMeasureImpl.immutableCopyOf(value.getAltimeterSettingQNH()))
                    .setSurfaceWind(ObservedSurfaceWindImpl.immutableCopyOf(value.getSurfaceWind()))
                    .setVisibility(HorizontalVisibilityImpl.immutableCopyOf(value.getVisibility()))
                    .setClouds(ObservedCloudsImpl.immutableCopyOf(value.getClouds()))
                    .setWindShear(WindShearImpl.immutableCopyOf(value.getWindShear()))
                    .setSeaState(SeaStateImpl.immutableCopyOf(value.getSeaState()))
                    .setColorState(value.getColorState());

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
            Builder retval = from(value);
            retval.setRoutineDelayed(value.isRoutineDelayed());
            return retval;
        }

        public static Builder from(final SPECI value) {
            Builder retval = from(value);
            retval.setRoutineDelayed(false);
            return retval;
        }

        public Builder withCompleteForecastTimes(final YearMonth issueYearMonth, int issueDay, int issueHour, final ZoneId tz) throws IllegalArgumentException {
            if (getTrends().isPresent()) {
                return setTrends(getTimeCompletedTrends(getTrends().get(), issueYearMonth, issueDay, issueHour, tz));
            } else {
                return this;
            }
        }

        public Builder withCompleteIssueTime(final YearMonth yearMonth) throws IllegalArgumentException {
            return mutateIssueTime((input) -> input.completedWithIssueYearMonth(yearMonth));
        }
    }

    static class SPECIInvocationHandler implements InvocationHandler {
        private METARImpl delegate;

        SPECIInvocationHandler(final METARImpl delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method delegateMethod = METARImpl.class.getMethod(method.getName(), method.getParameterTypes());
            return delegateMethod.invoke(delegate, args);
        }

        METARImpl getDelegate() {
            return delegate;
        }

    }

}