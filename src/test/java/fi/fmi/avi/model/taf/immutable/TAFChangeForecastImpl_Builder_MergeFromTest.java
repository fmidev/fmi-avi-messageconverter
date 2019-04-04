package fi.fmi.avi.model.taf.immutable;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

public class TAFChangeForecastImpl_Builder_MergeFromTest
        extends AbstractTAFForecast_Builder_MergeFromTest<TAFChangeForecastImpl, TAFChangeForecastImpl.Builder> {
    @Override
    protected TAFChangeForecastImpl.Builder newBuilder() {
        return TAFChangeForecastImpl.builder()//
                .setChangeIndicator(AviationCodeListUser.TAFChangeIndicator.TEMPORARY_FLUCTUATIONS)//
                .setPeriodOfChange(PartialOrCompleteTimePeriod.createValidityTime("0100/0200"));

    }

    @Override
    protected TAFChangeForecastImpl.Builder clearOptionalSpecializedValues(final TAFChangeForecastImpl.Builder builder) {
        return builder;
    }

    @Override
    protected TAFChangeForecastImpl.Builder populateOptionalEmptySpecializedValues(final TAFChangeForecastImpl.Builder builder) {
        return builder;
    }

    @Override
    protected TAFChangeForecastImpl.Builder populateOptionalSpecializedValues(final TAFChangeForecastImpl.Builder builder) {
        return builder;
    }

    @Override
    protected TAFChangeForecastImpl.Builder copyOptionalSpecializedValues(final TAFChangeForecastImpl.Builder from, final TAFChangeForecastImpl.Builder to) {
        return to//
                .setChangeIndicator(from.getChangeIndicator())//
                .setPeriodOfChange(from.getPeriodOfChangeBuilder());
    }
}
