package fi.fmi.avi.model.taf.immutable;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.runner.RunWith;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import junitparams.JUnitParamsRunner;

@SuppressWarnings("UnnecessaryLocalVariable")
@RunWith(JUnitParamsRunner.class)
public class TAFBaseForecastImpl_Builder_MergeFromTest extends AbstractTAFForecast_Builder_MergeFromTest<TAFBaseForecastImpl, TAFBaseForecastImpl.Builder> {
    @Override
    protected TAFBaseForecastImpl.Builder newBuilder() {
        return new TAFBaseForecastImpl.Builder();
    }

    @Override
    protected TAFBaseForecastImpl.Builder clearOptionalSpecializedValues(final TAFBaseForecastImpl.Builder builder) {
        return builder.clearTemperatures();
    }

    @Override
    protected TAFBaseForecastImpl.Builder populateOptionalEmptySpecializedValues(final TAFBaseForecastImpl.Builder builder) {
        return builder.setTemperatures(Collections.emptyList());
    }

    @Override
    protected TAFBaseForecastImpl.Builder populateOptionalSpecializedValues(final TAFBaseForecastImpl.Builder builder) {
        return builder//
                .setTemperatures(Collections.singletonList(new TAFAirTemperatureForecastImpl.Builder()//
                        .setMinTemperature(NumericMeasureImpl.of(0.0, DEGREES_CELSIUS))//
                        .setMinTemperatureTime(new PartialOrCompleteTimeInstant.Builder().setCompleteTime(ZonedDateTime.now()).build())//
                        .setMaxTemperature(NumericMeasureImpl.of(0.0, DEGREES_CELSIUS))//
                        .setMaxTemperatureTime(new PartialOrCompleteTimeInstant.Builder().setCompleteTime(ZonedDateTime.now()).build())//
                        .build()));
    }

    @Override
    protected TAFBaseForecastImpl.Builder copyOptionalSpecializedValues(final TAFBaseForecastImpl.Builder source, final TAFBaseForecastImpl.Builder builder) {
        return builder.setTemperatures(source.getTemperatures());
    }
}
