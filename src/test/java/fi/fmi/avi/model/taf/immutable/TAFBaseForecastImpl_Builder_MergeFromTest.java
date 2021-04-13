package fi.fmi.avi.model.taf.immutable;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.runner.RunWith;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.NumericMeasureImpl;
import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
public class TAFBaseForecastImpl_Builder_MergeFromTest extends AbstractTAFForecast_Builder_MergeFromTest<TAFBaseForecastImpl, TAFBaseForecastImpl.Builder> {
    @Override
    protected TAFBaseForecastImpl.Builder newBuilder() {
        return TAFBaseForecastImpl.builder();
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
                .setTemperatures(Collections.singletonList(TAFAirTemperatureForecastImpl.builder()//
                        .setMinTemperature(NumericMeasureImpl.of(0.0, DEGREES_CELSIUS))//
                        .setMinTemperatureTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.now()).build())//
                        .setMaxTemperature(NumericMeasureImpl.of(0.0, DEGREES_CELSIUS))//
                        .setMaxTemperatureTime(PartialOrCompleteTimeInstant.builder().setCompleteTime(ZonedDateTime.now()).build())//
                        .build()));
    }

    @Override
    protected TAFBaseForecastImpl.Builder copyOptionalSpecializedValues(final TAFBaseForecastImpl.Builder source, final TAFBaseForecastImpl.Builder builder) {
        return builder.setTemperatures(source.getTemperatures());
    }
}
