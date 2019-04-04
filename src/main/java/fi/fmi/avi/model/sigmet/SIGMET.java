package fi.fmi.avi.model.sigmet;

import fi.fmi.avi.model.AirTrafficServicesUnitWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;
import fi.fmi.avi.model.SIGMETAIRMET;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using=SIGMETDeserializer.class)
public interface SIGMET extends SIGMETAIRMET {
    AeronauticalSignificantWeatherPhenomenon getSigmetPhenomenon();
    Optional<SigmetReference> getCancelledReference();

    public SigmetAnalysisType getAnalysisType();
    public Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    public Optional<NumericMeasure> getMovingSpeed();
    public Optional<NumericMeasure> getMovingDirection();

    public Optional<SigmetIntensityChange> getIntensityChange();

    public Optional<List<PhenomenonGeometry>> getForecastGeometries();

    public Optional<Boolean> getNoVaExpected(); //Only applicable to ForecastPositionAnalysis

    Optional<VAInfo> getVAInfo(); //If this is present this is a VASigmet

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    interface Builder<T extends SIGMET, B extends Builder<T, B>> {
        /**
         * Returns a newly-created {@link SIGMET} based on the contents of the {@code Builder}.
         *
         * @throws IllegalStateException
         *         if any field has not been set
         */
        T build();

        /**
         * Sets all property values using the given {@code SIGMET} as a template.
         */
        B mergeFrom(T value);

        /**
         * Copies values from the given {@code Builder}. Does not affect any properties not set on the
         * input.
         */
        B mergeFrom(B template);

        /**
         * Copies all property values from the given {@code SIGMET}.
         * Properties specific to {@link TAFBaseForecast} are copied when {@code value} is an instance of {@code TAFBaseForecast}.
         *
         * @param value
         *         copy source
         *
         * @return this builder
         */
        B copyFrom(SIGMET value);

        /**
         * Merges the given {@code SIGMET} into this builder.
         * All existing properties of {@code value} are copied into this builder and properties that are empty in {@code value} are left as is in this builder
         * with some exceptions depending on property values of {@code value}:
         *
         * <ul>
         * <li>
         * {@code prevailingVisibility}, {@code forecastWeather} and {@code cloudForecast} are always copied when {@code ceilingAndVisibilityOk == true}.
         * </li>
         * <li>{@code prevailingVisibilityOperator} is copied always whenever {@code prevailingVisibility} is copied.</li>
         * <li>{@code forecastWeather} is always copied when {@code noSignificantWeather == true}.</li>
         * <li>{@code noSignificantWeather} is always set to {@code false}</li>
         * </ul>
         *
         * @param value
         *         merge source
         *
         * @return this builder
         */
        B mergeFromTAFForecast(SIGMET value);

        /**
         * Resets the state of this builder.
         */
        B clear();
    }

}
