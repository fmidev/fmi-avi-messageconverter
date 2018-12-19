package fi.fmi.avi.model.sigmet;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.SIGMETAIRMET;

public interface AIRMET extends SIGMETAIRMET {
    AeronauticalAirmetWeatherPhenomenon getAirmetPhenomenon();
    Optional<AirmetCloudLevels>getCloudLevels();
    Optional<AirmetWind>getWind();
    Optional<WeatherCausingVisibilityReduction>getObscuration();

    Optional<SigmetReference> getCancelledReference();

    public SigmetAnalysisType getAnalysisType();
    public Optional<List<PhenomenonGeometryWithHeight>> getAnalysisGeometries();

    public Optional<NumericMeasure> getMovingSpeed();
    public Optional<NumericMeasure> getMovingDirection();

    public Optional<SigmetIntensityChange> getIntensityChange();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    interface Builder<T extends AIRMET, B extends Builder<T, B>> {
        /**
         * Returns a newly-created {@link AIRMET} based on the contents of the {@code Builder}.
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
        B copyFrom(AIRMET value);

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
        B mergeFromTAFForecast(AIRMET value);

        /**
         * Resets the state of this builder.
         */
        B clear();
    }

}
