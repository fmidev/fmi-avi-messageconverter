package fi.fmi.avi.model.metar;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.AerodromeWeatherMessage;
import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.Weather;

/**
 * Created by rinne on 06/04/2018.
 */

public interface MeteorologicalTerminalAirReport extends AerodromeWeatherMessage, AviationCodeListUser {

    boolean isAutomatedStation();

    MetarStatus getStatus();

    boolean isCeilingAndVisibilityOk();

    Optional<NumericMeasure> getAirTemperature();

    Optional<NumericMeasure> getDewpointTemperature();

    Optional<NumericMeasure> getAltimeterSettingQNH();

    Optional<ObservedSurfaceWind> getSurfaceWind();

    Optional<HorizontalVisibility> getVisibility();

    Optional<List<RunwayVisualRange>> getRunwayVisualRanges();

    Optional<List<Weather>> getPresentWeather();

    Optional<ObservedClouds> getClouds();

    Optional<List<Weather>> getRecentWeather();

    Optional<WindShear> getWindShear();

    Optional<SeaState> getSeaState();

    Optional<List<RunwayState>> getRunwayStates();

    boolean isSnowClosure();

    boolean isNoSignificantChanges();

    Optional<List<TrendForecast>> getTrends();

    Optional<ColorState> getColorState();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    interface Builder<T extends MeteorologicalTerminalAirReport, B extends Builder<T, B>> {

        /**
         * Returns a newly-created {@link MeteorologicalTerminalAirReport} based on the contents of the {@code Builder}.
         *
         * @throws IllegalStateException
         *         if any field has not been set
         */
        T build();

        /**
         * Sets all property values using the given {@code MeteorologicalTerminalAirReport} as a template.
         */
        B mergeFrom(T value);

        /**
         * Copies values from the given {@code Builder}. Does not affect any properties not set on the
         * input.
         */
        B mergeFrom(B template);

        /**
         * Resets the state of this builder.
         */
        B clear();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRemarks()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableRemarks(@Nullable final List<String> remarks) {
            if (remarks != null) {
                return setRemarks(remarks);
            } else {
                return clearRemarks();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getRemarks()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapRemarks(UnaryOperator<List<String>> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRemarks()} to {@link Optional#empty()
         * Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearRemarks();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getRemarks()}.
         */
        Optional<List<String>> getRemarks();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRemarks()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code remarks} is null
         */
        B setRemarks(List<String> remarks);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRemarks()}.
         *
         * @return this {@code Builder} object
         */
        B setRemarks(Optional<? extends List<String>> remarks);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsage()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullablePermissibleUsage(@Nullable final AviationCodeListUser.PermissibleUsage permissibleUsage) {
            if (permissibleUsage != null) {
                return setPermissibleUsage(permissibleUsage);
            } else {
                return clearPermissibleUsage();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsage()} is present, replaces
         * it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapPermissibleUsage(UnaryOperator<AviationCodeListUser.PermissibleUsage> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsage()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearPermissibleUsage();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsage()}.
         */
        Optional<AviationCodeListUser.PermissibleUsage> getPermissibleUsage();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsage()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code permissibleUsage} is null
         */
        B setPermissibleUsage(AviationCodeListUser.PermissibleUsage permissibleUsage);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsage()}.
         *
         * @return this {@code Builder} object
         */
        B setPermissibleUsage(Optional<? extends AviationCodeListUser.PermissibleUsage> permissibleUsage);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageReason()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullablePermissibleUsageReason(@Nullable final AviationCodeListUser.PermissibleUsageReason permissibleUsageReason) {
            if (permissibleUsageReason != null) {
                return setPermissibleUsageReason(permissibleUsageReason);
            } else {
                return clearPermissibleUsageReason();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageReason()} is present,
         * replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapPermissibleUsageReason(UnaryOperator<AviationCodeListUser.PermissibleUsageReason> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageReason()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearPermissibleUsageReason();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageReason()}.
         */
        Optional<AviationCodeListUser.PermissibleUsageReason> getPermissibleUsageReason();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageReason()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code permissibleUsageReason} is null
         */
        B setPermissibleUsageReason(AviationCodeListUser.PermissibleUsageReason permissibleUsageReason);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageReason()}.
         *
         * @return this {@code Builder} object
         */
        B setPermissibleUsageReason(Optional<? extends AviationCodeListUser.PermissibleUsageReason> permissibleUsageReason);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageSupplementary()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullablePermissibleUsageSupplementary(@Nullable final String permissibleUsageSupplementary) {
            if (permissibleUsageSupplementary != null) {
                return setPermissibleUsageSupplementary(permissibleUsageSupplementary);
            } else {
                return clearPermissibleUsageSupplementary();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageSupplementary()} is
         * present, replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapPermissibleUsageSupplementary(UnaryOperator<String> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageSupplementary()} to
         * {@link Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearPermissibleUsageSupplementary();

        /**
         * Returns the value that will be returned by {@link
         * MeteorologicalTerminalAirReport#getPermissibleUsageSupplementary()}.
         */
        Optional<String> getPermissibleUsageSupplementary();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageSupplementary()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code permissibleUsageSupplementary} is null
         */
        B setPermissibleUsageSupplementary(String permissibleUsageSupplementary);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPermissibleUsageSupplementary()}.
         *
         * @return this {@code Builder} object
         */
        B setPermissibleUsageSupplementary(Optional<? extends String> permissibleUsageSupplementary);

        /**
         * Replaces the value to be returned by {@link MeteorologicalTerminalAirReport#isTranslated()} by applying {@code
         * mapper} to it and using the result.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null or returns null
         * @throws IllegalStateException
         *         if the field has not been set
         */
        B mapTranslated(UnaryOperator<Boolean> mapper);

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#isTranslated()}.
         *
         * @throws IllegalStateException
         *         if the field has not been set
         */
        boolean isTranslated();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#isTranslated()}.
         *
         * @return this {@code Builder} object
         */
        B setTranslated(boolean translated);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinID()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableTranslatedBulletinID(@Nullable final String translatedBulletinID) {
            if (translatedBulletinID != null) {
                return setTranslatedBulletinID(translatedBulletinID);
            } else {
                return clearTranslatedBulletinID();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinID()} is present,
         * replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapTranslatedBulletinID(UnaryOperator<String> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinID()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearTranslatedBulletinID();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinID()}.
         */
        Optional<String> getTranslatedBulletinID();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinID()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code translatedBulletinID} is null
         */
        B setTranslatedBulletinID(String translatedBulletinID);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinID()}.
         *
         * @return this {@code Builder} object
         */
        B setTranslatedBulletinID(Optional<? extends String> translatedBulletinID);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinReceptionTime()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableTranslatedBulletinReceptionTime(@Nullable final ZonedDateTime translatedBulletinReceptionTime) {
            if (translatedBulletinReceptionTime != null) {
                return setTranslatedBulletinReceptionTime(translatedBulletinReceptionTime);
            } else {
                return clearTranslatedBulletinReceptionTime();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinReceptionTime()} is
         * present, replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapTranslatedBulletinReceptionTime(UnaryOperator<ZonedDateTime> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinReceptionTime()} to
         * {@link Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearTranslatedBulletinReceptionTime();

        /**
         * Returns the value that will be returned by {@link
         * MeteorologicalTerminalAirReport#getTranslatedBulletinReceptionTime()}.
         */
        Optional<ZonedDateTime> getTranslatedBulletinReceptionTime();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinReceptionTime()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code translatedBulletinReceptionTime} is null
         */
        B setTranslatedBulletinReceptionTime(ZonedDateTime translatedBulletinReceptionTime);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedBulletinReceptionTime()}.
         *
         * @return this {@code Builder} object
         */
        B setTranslatedBulletinReceptionTime(Optional<? extends ZonedDateTime> translatedBulletinReceptionTime);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreDesignator()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableTranslationCentreDesignator(@Nullable final String translationCentreDesignator) {
            if (translationCentreDesignator != null) {
                return setTranslationCentreDesignator(translationCentreDesignator);
            } else {
                return clearTranslationCentreDesignator();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreDesignator()} is
         * present, replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapTranslationCentreDesignator(UnaryOperator<String> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreDesignator()} to
         * {@link Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearTranslationCentreDesignator();

        /**
         * Returns the value that will be returned by {@link
         * MeteorologicalTerminalAirReport#getTranslationCentreDesignator()}.
         */
        Optional<String> getTranslationCentreDesignator();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreDesignator()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code translationCentreDesignator} is null
         */
        B setTranslationCentreDesignator(String translationCentreDesignator);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreDesignator()}.
         *
         * @return this {@code Builder} object
         */
        B setTranslationCentreDesignator(Optional<? extends String> translationCentreDesignator);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreName()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableTranslationCentreName(@Nullable final String translationCentreName) {
            if (translationCentreName != null) {
                return setTranslationCentreName(translationCentreName);
            } else {
                return clearTranslationCentreName();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreName()} is present,
         * replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapTranslationCentreName(UnaryOperator<String> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreName()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearTranslationCentreName();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreName()}.
         */
        Optional<String> getTranslationCentreName();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreName()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code translationCentreName} is null
         */
        B setTranslationCentreName(String translationCentreName);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationCentreName()}.
         *
         * @return this {@code Builder} object
         */
        B setTranslationCentreName(Optional<? extends String> translationCentreName);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationTime()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableTranslationTime(@Nullable final ZonedDateTime translationTime) {
            if (translationTime != null) {
                return setTranslationTime(translationTime);
            } else {
                return clearTranslationTime();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationTime()} is present, replaces
         * it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapTranslationTime(UnaryOperator<ZonedDateTime> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationTime()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearTranslationTime();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getTranslationTime()}.
         */
        Optional<ZonedDateTime> getTranslationTime();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationTime()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code translationTime} is null
         */
        B setTranslationTime(ZonedDateTime translationTime);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslationTime()}.
         *
         * @return this {@code Builder} object
         */
        B setTranslationTime(Optional<? extends ZonedDateTime> translationTime);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedTAC()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableTranslatedTAC(@Nullable final String translatedTAC) {
            if (translatedTAC != null) {
                return setTranslatedTAC(translatedTAC);
            } else {
                return clearTranslatedTAC();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedTAC()} is present, replaces it
         * by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapTranslatedTAC(UnaryOperator<String> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedTAC()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearTranslatedTAC();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getTranslatedTAC()}.
         */
        Optional<String> getTranslatedTAC();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedTAC()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code translatedTAC} is null
         */
        B setTranslatedTAC(String translatedTAC);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTranslatedTAC()}.
         *
         * @return this {@code Builder} object
         */
        B setTranslatedTAC(Optional<? extends String> translatedTAC);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getIssueTime()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code issueTime} is null
         */
        B setIssueTime(PartialOrCompleteTimeInstant issueTime);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getIssueTime()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code builder} is null
         */
        B setIssueTime(PartialOrCompleteTimeInstant.Builder builder);

        /**
         * Applies {@code mutator} to the builder for the value that will be returned by {@link
         * MeteorologicalTerminalAirReport#getIssueTime()}.
         *
         * <p>This method mutates the builder in-place. {@code mutator} is a void consumer, so any value
         * returned from a lambda will be ignored.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mutator} is null
         */
        B mutateIssueTime(Consumer<PartialOrCompleteTimeInstant.Builder> mutator);

        /**
         * Returns a builder for the value that will be returned by {@link MeteorologicalTerminalAirReport#getIssueTime()}.
         */
        PartialOrCompleteTimeInstant.Builder getIssueTimeBuilder();

        /**
         * Replaces the value to be returned by {@link MeteorologicalTerminalAirReport#getAerodrome()} by applying {@code
         * mapper} to it and using the result.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null or returns null
         * @throws IllegalStateException
         *         if the field has not been set
         */
        B mapAerodrome(UnaryOperator<Aerodrome> mapper);

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getAerodrome()}.
         *
         * @throws IllegalStateException
         *         if the field has not been set
         */
        Aerodrome getAerodrome();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAerodrome()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code aerodrome} is null
         */
        B setAerodrome(Aerodrome aerodrome);

        /**
         * Replaces the value to be returned by {@link MeteorologicalTerminalAirReport#isAutomatedStation()} by applying
         * {@code mapper} to it and using the result.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null or returns null
         * @throws IllegalStateException
         *         if the field has not been set
         */
        B mapAutomatedStation(UnaryOperator<Boolean> mapper);

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#isAutomatedStation()}.
         *
         * @throws IllegalStateException
         *         if the field has not been set
         */
        boolean isAutomatedStation();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#isAutomatedStation()}.
         *
         * @return this {@code Builder} object
         */
        B setAutomatedStation(boolean automatedStation);

        /**
         * Replaces the value to be returned by {@link MeteorologicalTerminalAirReport#getStatus()} by applying {@code
         * mapper} to it and using the result.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null or returns null
         * @throws IllegalStateException
         *         if the field has not been set
         */
        B mapStatus(UnaryOperator<MetarStatus> mapper);

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getStatus()}.
         *
         * @throws IllegalStateException
         *         if the field has not been set
         */
        AviationCodeListUser.MetarStatus getStatus();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getStatus()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code status} is null
         */
        B setStatus(AviationCodeListUser.MetarStatus status);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAirTemperature()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableAirTemperature(@Nullable final NumericMeasure airTemperature) {
            if (airTemperature != null) {
                return setAirTemperature(airTemperature);
            } else {
                return clearAirTemperature();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getAirTemperature()} is present, replaces it
         * by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapAirTemperature(UnaryOperator<NumericMeasure> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAirTemperature()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearAirTemperature();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getAirTemperature()}.
         */
        Optional<NumericMeasure> getAirTemperature();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAirTemperature()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code airTemperature} is null
         */
        B setAirTemperature(NumericMeasure airTemperature);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAirTemperature()}.
         *
         * @return this {@code Builder} object
         */
        B setAirTemperature(Optional<? extends NumericMeasure> airTemperature);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getDewpointTemperature()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableDewpointTemperature(@Nullable final NumericMeasure dewpointTemperature) {
            if (dewpointTemperature != null) {
                return setDewpointTemperature(dewpointTemperature);
            } else {
                return clearDewpointTemperature();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getDewpointTemperature()} is present,
         * replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapDewpointTemperature(UnaryOperator<NumericMeasure> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getDewpointTemperature()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearDewpointTemperature();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getDewpointTemperature()}.
         */
        Optional<NumericMeasure> getDewpointTemperature();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getDewpointTemperature()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code dewpointTemperature} is null
         */
        B setDewpointTemperature(NumericMeasure dewpointTemperature);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getDewpointTemperature()}.
         *
         * @return this {@code Builder} object
         */
        B setDewpointTemperature(Optional<? extends NumericMeasure> dewpointTemperature);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAltimeterSettingQNH()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableAltimeterSettingQNH(@Nullable final NumericMeasure altimeterSettingQNH) {
            if (altimeterSettingQNH != null) {
                return setAltimeterSettingQNH(altimeterSettingQNH);
            } else {
                return clearAltimeterSettingQNH();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getAltimeterSettingQNH()} is present,
         * replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapAltimeterSettingQNH(UnaryOperator<NumericMeasure> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAltimeterSettingQNH()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearAltimeterSettingQNH();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getAltimeterSettingQNH()}.
         */
        Optional<NumericMeasure> getAltimeterSettingQNH();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAltimeterSettingQNH()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code altimeteisCeilingAndVisibilityOkrSettingQNH} is null
         */
        B setAltimeterSettingQNH(NumericMeasure altimeterSettingQNH);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getAltimeterSettingQNH()}.
         *
         * @return this {@code Builder} object
         */
        B setAltimeterSettingQNH(Optional<? extends NumericMeasure> altimeterSettingQNH);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSurfaceWind()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableSurfaceWind(@Nullable final ObservedSurfaceWind surfaceWind) {
            if (surfaceWind != null) {
                return setSurfaceWind(surfaceWind);
            } else {
                return clearSurfaceWind();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getSurfaceWind()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapSurfaceWind(UnaryOperator<ObservedSurfaceWind> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSurfaceWind()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearSurfaceWind();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getSurfaceWind()}.
         */
        Optional<ObservedSurfaceWind> getSurfaceWind();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSurfaceWind()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code surfaceWind} is null
         */
        B setSurfaceWind(ObservedSurfaceWind surfaceWind);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSurfaceWind()}.
         *
         * @return this {@code Builder} object
         */
        B setSurfaceWind(Optional<? extends ObservedSurfaceWind> surfaceWind);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getVisibility()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableVisibility(@Nullable final HorizontalVisibility visibility) {
            if (visibility != null) {
                return setVisibility(visibility);
            } else {
                return clearVisibility();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getVisibility()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapVisibility(UnaryOperator<HorizontalVisibility> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getVisibility()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearVisibility();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getVisibility()}.
         */
        Optional<HorizontalVisibility> getVisibility();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getVisibility()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code visibility} is null
         */
        B setVisibility(HorizontalVisibility visibility);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getVisibility()}.
         *
         * @return this {@code Builder} object
         */
        B setVisibility(Optional<? extends HorizontalVisibility> visibility);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayVisualRanges()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableRunwayVisualRanges(@Nullable final List<RunwayVisualRange> runwayVisualRanges) {
            if (runwayVisualRanges != null) {
                return setRunwayVisualRanges(runwayVisualRanges);
            } else {
                return clearRunwayVisualRanges();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayVisualRanges()} is present,
         * replaces it by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapRunwayVisualRanges(UnaryOperator<List<RunwayVisualRange>> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayVisualRanges()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearRunwayVisualRanges();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getRunwayVisualRanges()}.
         */
        Optional<List<RunwayVisualRange>> getRunwayVisualRanges();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayVisualRanges()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code runwayVisualRanges} is null
         */
        B setRunwayVisualRanges(List<RunwayVisualRange> runwayVisualRanges);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayVisualRanges()}.
         *
         * @return this {@code Builder} object
         */
        B setRunwayVisualRanges(Optional<? extends List<RunwayVisualRange>> runwayVisualRanges);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPresentWeather()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullablePresentWeather(@Nullable final List<Weather> presentWeather) {
            if (presentWeather != null) {
                return setPresentWeather(presentWeather);
            } else {
                return clearPresentWeather();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getPresentWeather()} is present, replaces it
         * by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapPresentWeather(UnaryOperator<List<Weather>> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPresentWeather()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearPresentWeather();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getPresentWeather()}.
         */
        Optional<List<Weather>> getPresentWeather();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPresentWeather()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code presentWeather} is null
         */
        B setPresentWeather(List<Weather> presentWeather);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getPresentWeather()}.
         *
         * @return this {@code Builder} object
         */
        B setPresentWeather(Optional<? extends List<Weather>> presentWeather);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getClouds()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableClouds(@Nullable final ObservedClouds clouds) {
            if (clouds != null) {
                return setClouds(clouds);
            } else {
                return clearClouds();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getClouds()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapClouds(UnaryOperator<ObservedClouds> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getClouds()} to {@link Optional#empty()
         * Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearClouds();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getClouds()}.
         */
        Optional<ObservedClouds> getClouds();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getClouds()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code clouds} is null
         */
        B setClouds(ObservedClouds clouds);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getClouds()}.
         *
         * @return this {@code Builder} object
         */
        B setClouds(Optional<? extends ObservedClouds> clouds);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRecentWeather()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableRecentWeather(@Nullable final List<Weather> recentWeather) {
            if (recentWeather != null) {
                return setRecentWeather(recentWeather);
            } else {
                return clearRecentWeather();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getRecentWeather()} is present, replaces it
         * by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapRecentWeather(UnaryOperator<List<Weather>> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRecentWeather()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearRecentWeather();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getRecentWeather()}.
         */
        Optional<List<Weather>> getRecentWeather();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRecentWeather()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code recentWeather} is null
         */
        B setRecentWeather(List<Weather> recentWeather);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRecentWeather()}.
         *
         * @return this {@code Builder} object
         */
        B setRecentWeather(Optional<? extends List<Weather>> recentWeather);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getWindShear()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableWindShear(@Nullable final WindShear windShear) {
            if (windShear != null) {
                return setWindShear(windShear);
            } else {
                return clearWindShear();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getWindShear()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapWindShear(UnaryOperator<WindShear> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getWindShear()} to {@link Optional#empty()
         * Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearWindShear();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getWindShear()}.
         */
        Optional<WindShear> getWindShear();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getWindShear()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code windShear} is null
         */
        B setWindShear(WindShear windShear);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getWindShear()}.
         *
         * @return this {@code Builder} object
         */
        B setWindShear(Optional<? extends WindShear> windShear);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSeaState()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableSeaState(@Nullable final SeaState seaState) {
            if (seaState != null) {
                return setSeaState(seaState);
            } else {
                return clearSeaState();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getSeaState()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapSeaState(UnaryOperator<SeaState> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSeaState()} to {@link Optional#empty()
         * Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearSeaState();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getSeaState()}.
         */
        Optional<SeaState> getSeaState();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSeaState()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code seaState} is null
         */
        B setSeaState(SeaState seaState);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getSeaState()}.
         *
         * @return this {@code Builder} object
         */
        B setSeaState(Optional<? extends SeaState> seaState);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayStates()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableRunwayStates(@Nullable final List<RunwayState> runwayStates) {
            if (runwayStates != null) {
                return setRunwayStates(runwayStates);
            } else {
                return clearRunwayStates();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayStates()} is present, replaces it
         * by applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapRunwayStates(UnaryOperator<List<RunwayState>> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayStates()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearRunwayStates();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getRunwayStates()}.
         */
        Optional<List<RunwayState>> getRunwayStates();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayStates()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code runwayStates} is null
         */
        B setRunwayStates(List<RunwayState> runwayStates);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getRunwayStates()}.
         *
         * @return this {@code Builder} object
         */
        B setRunwayStates(Optional<? extends List<RunwayState>> runwayStates);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTrends()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableTrends(@Nullable final List<TrendForecast> trends) {
            if (trends != null) {
                return setTrends(trends);
            } else {
                return clearTrends();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getTrends()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapTrends(UnaryOperator<List<TrendForecast>> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTrends()} to {@link Optional#empty()
         * Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearTrends();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getTrends()}.
         */
        Optional<List<TrendForecast>> getTrends();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTrends()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code trends} is null
         */
        B setTrends(List<TrendForecast> trends);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getTrends()}.
         *
         * @return this {@code Builder} object
         */
        B setTrends(Optional<? extends List<TrendForecast>> trends);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getColorState()}.
         *
         * @return this {@code Builder} object
         */
        default B setNullableColorState(@Nullable final AviationCodeListUser.ColorState colorState) {
            if (colorState != null) {
                return setColorState(colorState);
            } else {
                return clearColorState();
            }
        }

        /**
         * If the value to be returned by {@link MeteorologicalTerminalAirReport#getColorState()} is present, replaces it by
         * applying {@code mapper} to it and using the result.
         *
         * <p>If the result is null, clears the value.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code mapper} is null
         */
        B mapColorState(UnaryOperator<AviationCodeListUser.ColorState> mapper);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getColorState()} to {@link
         * Optional#empty() Optional.empty()}.
         *
         * @return this {@code Builder} object
         */
        B clearColorState();

        /**
         * Returns the value that will be returned by {@link MeteorologicalTerminalAirReport#getColorState()}.
         */
        Optional<AviationCodeListUser.ColorState> getColorState();

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getColorState()}.
         *
         * @return this {@code Builder} object
         *
         * @throws NullPointerException
         *         if {@code colorState} is null
         */
        B setColorState(AviationCodeListUser.ColorState colorState);

        /**
         * Sets the value to be returned by {@link MeteorologicalTerminalAirReport#getColorState()}.
         *
         * @return this {@code Builder} object
         */
        B setColorState(Optional<? extends AviationCodeListUser.ColorState> colorState);

    }

}
