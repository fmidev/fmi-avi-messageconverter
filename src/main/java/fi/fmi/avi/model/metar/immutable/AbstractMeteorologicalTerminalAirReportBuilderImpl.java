package fi.fmi.avi.model.metar.immutable;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.NumericMeasure;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.metar.HorizontalVisibility;
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
import fi.fmi.avi.model.Weather;

/**
 * Shared, hand-written base for {@link METARImpl.Builder} and {@link SPECIImpl.Builder}.
 *
 * <p>{@link MeteorologicalTerminalAirReportBuilder} (~30 shared properties, ~90 abstract methods)
 * used to be satisfied automatically because FreeBuilder's generated {@code METARImpl_Builder}/
 * {@code SPECIImpl_Builder} happened to expose a matching set of builder-side getters and setters.
 * Immutables' generated builders expose no getters at all (see docs/07-modernization-plan.md), so
 * this class implements the shared interface directly against plain fields instead of extending an
 * Immutables-generated builder - the "detached builder" pattern. {@code build()} itself stays
 * abstract: each subclass assembles its own {@code ImmutableMETARImpl}/{@code ImmutableSPECIImpl}
 * from these fields.
 *
 * @param <T> the concrete value type ({@link METARImpl} or {@link SPECIImpl})
 * @param <B> the concrete builder type ({@code METARImpl.Builder} or {@code SPECIImpl.Builder})
 */
public abstract class AbstractMeteorologicalTerminalAirReportBuilderImpl<T extends MeteorologicalTerminalAirReport,
        B extends AbstractMeteorologicalTerminalAirReportBuilderImpl<T, B>> implements MeteorologicalTerminalAirReportBuilder<T, B> {

    // NOTE: these fields carry @JsonIgnore because Jackson's builder-style deserialization otherwise binds JSON
    // properties directly to same-named fields (bypassing any @JsonDeserialize/@JsonProperty hint placed only on
    // the corresponding setter method) whenever a field of that name exists here, regardless of visibility. Fields
    // whose declared type is a plain concrete/enum/primitive type don't need this (Jackson can construct those
    // without a hint), but every field below whose type is an interface needing polymorphic resolution does; see
    // docs/07-modernization-plan.md.
    @JsonIgnore
    protected Aerodrome aerodrome;
    protected AviationWeatherMessage.ReportStatus reportStatus;

    protected boolean translated;
    protected boolean automatedStation;
    protected boolean missingMessage;
    protected boolean ceilingAndVisibilityOk;
    protected boolean snowClosure;
    protected boolean noSignificantChanges;
    protected Optional<List<String>> remarks = Optional.empty();
    protected Optional<AviationCodeListUser.PermissibleUsage> permissibleUsage = Optional.empty();
    protected Optional<AviationCodeListUser.PermissibleUsageReason> permissibleUsageReason = Optional.empty();
    protected Optional<String> permissibleUsageSupplementary = Optional.empty();
    protected Optional<String> translatedBulletinID = Optional.empty();
    protected Optional<ZonedDateTime> translatedBulletinReceptionTime = Optional.empty();
    protected Optional<String> translationCentreDesignator = Optional.empty();
    protected Optional<String> translationCentreName = Optional.empty();
    protected Optional<ZonedDateTime> translationTime = Optional.empty();
    protected Optional<String> translatedTAC = Optional.empty();
    protected Optional<PartialOrCompleteTimeInstant> issueTime = Optional.empty();
    @JsonIgnore
    protected Optional<NumericMeasure> airTemperature = Optional.empty();
    @JsonIgnore
    protected Optional<NumericMeasure> dewpointTemperature = Optional.empty();
    @JsonIgnore
    protected Optional<NumericMeasure> altimeterSettingQNH = Optional.empty();
    @JsonIgnore
    protected Optional<ObservedSurfaceWind> surfaceWind = Optional.empty();
    @JsonIgnore
    protected Optional<HorizontalVisibility> visibility = Optional.empty();
    @JsonIgnore
    protected Optional<List<RunwayVisualRange>> runwayVisualRanges = Optional.empty();
    @JsonIgnore
    protected Optional<List<Weather>> presentWeather = Optional.empty();
    @JsonIgnore
    protected Optional<ObservedClouds> clouds = Optional.empty();
    @JsonIgnore
    protected Optional<List<Weather>> recentWeather = Optional.empty();
    @JsonIgnore
    protected Optional<WindShear> windShear = Optional.empty();
    @JsonIgnore
    protected Optional<SeaState> seaState = Optional.empty();
    @JsonIgnore
    protected Optional<List<RunwayState>> runwayStates = Optional.empty();
    @JsonIgnore
    protected Optional<List<TrendForecast>> trends = Optional.empty();
    protected Optional<AviationCodeListUser.ColorState> colorState = Optional.empty();

    @SuppressWarnings("unchecked")
    protected final B self() {
        return (B) this;
    }

    @Override
    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    @Override
    public B setAerodrome(final Aerodrome aerodrome) {
        this.aerodrome = Objects.requireNonNull(aerodrome, "aerodrome");
        return self();
    }

    @Override
    public AviationWeatherMessage.ReportStatus getReportStatus() {
        return reportStatus;
    }

    @Override
    public B setReportStatus(final AviationWeatherMessage.ReportStatus reportStatus) {
        this.reportStatus = Objects.requireNonNull(reportStatus, "reportStatus");
        return self();
    }

    @Override
    public boolean isTranslated() {
        return translated;
    }

    @Override
    public B setTranslated(final boolean translated) {
        this.translated = translated;
        return self();
    }

    @Override
    public boolean isAutomatedStation() {
        return automatedStation;
    }

    @Override
    public B setAutomatedStation(final boolean automatedStation) {
        this.automatedStation = automatedStation;
        return self();
    }

    @Override
    public boolean isMissingMessage() {
        return missingMessage;
    }

    @Override
    public B setMissingMessage(final boolean missingMessage) {
        this.missingMessage = missingMessage;
        return self();
    }

    @Override
    public boolean isCeilingAndVisibilityOk() {
        return ceilingAndVisibilityOk;
    }

    @Override
    public B setCeilingAndVisibilityOk(final boolean ceilingAndVisibilityOk) {
        this.ceilingAndVisibilityOk = ceilingAndVisibilityOk;
        return self();
    }

    @Override
    public boolean isSnowClosure() {
        return snowClosure;
    }

    @Override
    public B setSnowClosure(final boolean snowClosure) {
        this.snowClosure = snowClosure;
        return self();
    }

    @Override
    public boolean isNoSignificantChanges() {
        return noSignificantChanges;
    }

    @Override
    public B setNoSignificantChanges(final boolean noSignificantChanges) {
        this.noSignificantChanges = noSignificantChanges;
        return self();
    }

    @Override
    public Optional<List<String>> getRemarks() {
        return remarks;
    }

    @Override
    public B setRemarks(final List<String> remarks) {
        this.remarks = Optional.of(Objects.requireNonNull(remarks, "remarks"));
        return self();
    }

    @Override
    public B setRemarks(final Optional<? extends List<String>> remarks) {
        Objects.requireNonNull(remarks, "remarks");
        this.remarks = remarks.isPresent() ? Optional.of(remarks.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearRemarks() {
        this.remarks = Optional.empty();
        return self();
    }

    @Override
    public Optional<AviationCodeListUser.PermissibleUsage> getPermissibleUsage() {
        return permissibleUsage;
    }

    @Override
    public B setPermissibleUsage(final AviationCodeListUser.PermissibleUsage permissibleUsage) {
        this.permissibleUsage = Optional.of(Objects.requireNonNull(permissibleUsage, "permissibleUsage"));
        return self();
    }

    @Override
    public B setPermissibleUsage(final Optional<? extends AviationCodeListUser.PermissibleUsage> permissibleUsage) {
        Objects.requireNonNull(permissibleUsage, "permissibleUsage");
        this.permissibleUsage = permissibleUsage.isPresent() ? Optional.of(permissibleUsage.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearPermissibleUsage() {
        this.permissibleUsage = Optional.empty();
        return self();
    }

    @Override
    public Optional<AviationCodeListUser.PermissibleUsageReason> getPermissibleUsageReason() {
        return permissibleUsageReason;
    }

    @Override
    public B setPermissibleUsageReason(final AviationCodeListUser.PermissibleUsageReason permissibleUsageReason) {
        this.permissibleUsageReason = Optional.of(Objects.requireNonNull(permissibleUsageReason, "permissibleUsageReason"));
        return self();
    }

    @Override
    public B setPermissibleUsageReason(final Optional<? extends AviationCodeListUser.PermissibleUsageReason> permissibleUsageReason) {
        Objects.requireNonNull(permissibleUsageReason, "permissibleUsageReason");
        this.permissibleUsageReason = permissibleUsageReason.isPresent() ? Optional.of(permissibleUsageReason.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearPermissibleUsageReason() {
        this.permissibleUsageReason = Optional.empty();
        return self();
    }

    @Override
    public Optional<String> getPermissibleUsageSupplementary() {
        return permissibleUsageSupplementary;
    }

    @Override
    public B setPermissibleUsageSupplementary(final String permissibleUsageSupplementary) {
        this.permissibleUsageSupplementary = Optional.of(Objects.requireNonNull(permissibleUsageSupplementary, "permissibleUsageSupplementary"));
        return self();
    }

    @Override
    public B setPermissibleUsageSupplementary(final Optional<? extends String> permissibleUsageSupplementary) {
        Objects.requireNonNull(permissibleUsageSupplementary, "permissibleUsageSupplementary");
        this.permissibleUsageSupplementary = permissibleUsageSupplementary.isPresent() ? Optional.of(permissibleUsageSupplementary.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearPermissibleUsageSupplementary() {
        this.permissibleUsageSupplementary = Optional.empty();
        return self();
    }

    @Override
    public Optional<String> getTranslatedBulletinID() {
        return translatedBulletinID;
    }

    @Override
    public B setTranslatedBulletinID(final String translatedBulletinID) {
        this.translatedBulletinID = Optional.of(Objects.requireNonNull(translatedBulletinID, "translatedBulletinID"));
        return self();
    }

    @Override
    public B setTranslatedBulletinID(final Optional<? extends String> translatedBulletinID) {
        Objects.requireNonNull(translatedBulletinID, "translatedBulletinID");
        this.translatedBulletinID = translatedBulletinID.isPresent() ? Optional.of(translatedBulletinID.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearTranslatedBulletinID() {
        this.translatedBulletinID = Optional.empty();
        return self();
    }

    @Override
    public Optional<ZonedDateTime> getTranslatedBulletinReceptionTime() {
        return translatedBulletinReceptionTime;
    }

    @Override
    public B setTranslatedBulletinReceptionTime(final ZonedDateTime translatedBulletinReceptionTime) {
        this.translatedBulletinReceptionTime = Optional.of(Objects.requireNonNull(translatedBulletinReceptionTime, "translatedBulletinReceptionTime"));
        return self();
    }

    @Override
    public B setTranslatedBulletinReceptionTime(final Optional<? extends ZonedDateTime> translatedBulletinReceptionTime) {
        Objects.requireNonNull(translatedBulletinReceptionTime, "translatedBulletinReceptionTime");
        this.translatedBulletinReceptionTime = translatedBulletinReceptionTime.isPresent() ? Optional.of(translatedBulletinReceptionTime.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearTranslatedBulletinReceptionTime() {
        this.translatedBulletinReceptionTime = Optional.empty();
        return self();
    }

    @Override
    public Optional<String> getTranslationCentreDesignator() {
        return translationCentreDesignator;
    }

    @Override
    public B setTranslationCentreDesignator(final String translationCentreDesignator) {
        this.translationCentreDesignator = Optional.of(Objects.requireNonNull(translationCentreDesignator, "translationCentreDesignator"));
        return self();
    }

    @Override
    public B setTranslationCentreDesignator(final Optional<? extends String> translationCentreDesignator) {
        Objects.requireNonNull(translationCentreDesignator, "translationCentreDesignator");
        this.translationCentreDesignator = translationCentreDesignator.isPresent() ? Optional.of(translationCentreDesignator.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearTranslationCentreDesignator() {
        this.translationCentreDesignator = Optional.empty();
        return self();
    }

    @Override
    public Optional<String> getTranslationCentreName() {
        return translationCentreName;
    }

    @Override
    public B setTranslationCentreName(final String translationCentreName) {
        this.translationCentreName = Optional.of(Objects.requireNonNull(translationCentreName, "translationCentreName"));
        return self();
    }

    @Override
    public B setTranslationCentreName(final Optional<? extends String> translationCentreName) {
        Objects.requireNonNull(translationCentreName, "translationCentreName");
        this.translationCentreName = translationCentreName.isPresent() ? Optional.of(translationCentreName.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearTranslationCentreName() {
        this.translationCentreName = Optional.empty();
        return self();
    }

    @Override
    public Optional<ZonedDateTime> getTranslationTime() {
        return translationTime;
    }

    @Override
    public B setTranslationTime(final ZonedDateTime translationTime) {
        this.translationTime = Optional.of(Objects.requireNonNull(translationTime, "translationTime"));
        return self();
    }

    @Override
    public B setTranslationTime(final Optional<? extends ZonedDateTime> translationTime) {
        Objects.requireNonNull(translationTime, "translationTime");
        this.translationTime = translationTime.isPresent() ? Optional.of(translationTime.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearTranslationTime() {
        this.translationTime = Optional.empty();
        return self();
    }

    @Override
    public Optional<String> getTranslatedTAC() {
        return translatedTAC;
    }

    @Override
    public B setTranslatedTAC(final String translatedTAC) {
        this.translatedTAC = Optional.of(Objects.requireNonNull(translatedTAC, "translatedTAC"));
        return self();
    }

    @Override
    public B setTranslatedTAC(final Optional<? extends String> translatedTAC) {
        Objects.requireNonNull(translatedTAC, "translatedTAC");
        this.translatedTAC = translatedTAC.isPresent() ? Optional.of(translatedTAC.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearTranslatedTAC() {
        this.translatedTAC = Optional.empty();
        return self();
    }

    @Override
    public Optional<PartialOrCompleteTimeInstant> getIssueTime() {
        return issueTime;
    }

    @Override
    public B setIssueTime(final PartialOrCompleteTimeInstant issueTime) {
        this.issueTime = Optional.of(Objects.requireNonNull(issueTime, "issueTime"));
        return self();
    }

    @Override
    public B setIssueTime(final Optional<? extends PartialOrCompleteTimeInstant> issueTime) {
        Objects.requireNonNull(issueTime, "issueTime");
        this.issueTime = issueTime.isPresent() ? Optional.of(issueTime.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearIssueTime() {
        this.issueTime = Optional.empty();
        return self();
    }

    @Override
    public Optional<NumericMeasure> getAirTemperature() {
        return airTemperature;
    }

    @Override
    public B setAirTemperature(final NumericMeasure airTemperature) {
        this.airTemperature = Optional.of(Objects.requireNonNull(airTemperature, "airTemperature"));
        return self();
    }

    @Override
    public B setAirTemperature(final Optional<? extends NumericMeasure> airTemperature) {
        Objects.requireNonNull(airTemperature, "airTemperature");
        this.airTemperature = airTemperature.isPresent() ? Optional.of(airTemperature.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearAirTemperature() {
        this.airTemperature = Optional.empty();
        return self();
    }

    @Override
    public Optional<NumericMeasure> getDewpointTemperature() {
        return dewpointTemperature;
    }

    @Override
    public B setDewpointTemperature(final NumericMeasure dewpointTemperature) {
        this.dewpointTemperature = Optional.of(Objects.requireNonNull(dewpointTemperature, "dewpointTemperature"));
        return self();
    }

    @Override
    public B setDewpointTemperature(final Optional<? extends NumericMeasure> dewpointTemperature) {
        Objects.requireNonNull(dewpointTemperature, "dewpointTemperature");
        this.dewpointTemperature = dewpointTemperature.isPresent() ? Optional.of(dewpointTemperature.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearDewpointTemperature() {
        this.dewpointTemperature = Optional.empty();
        return self();
    }

    @Override
    public Optional<NumericMeasure> getAltimeterSettingQNH() {
        return altimeterSettingQNH;
    }

    @Override
    public B setAltimeterSettingQNH(final NumericMeasure altimeterSettingQNH) {
        this.altimeterSettingQNH = Optional.of(Objects.requireNonNull(altimeterSettingQNH, "altimeterSettingQNH"));
        return self();
    }

    @Override
    public B setAltimeterSettingQNH(final Optional<? extends NumericMeasure> altimeterSettingQNH) {
        Objects.requireNonNull(altimeterSettingQNH, "altimeterSettingQNH");
        this.altimeterSettingQNH = altimeterSettingQNH.isPresent() ? Optional.of(altimeterSettingQNH.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearAltimeterSettingQNH() {
        this.altimeterSettingQNH = Optional.empty();
        return self();
    }

    @Override
    public Optional<ObservedSurfaceWind> getSurfaceWind() {
        return surfaceWind;
    }

    @Override
    public B setSurfaceWind(final ObservedSurfaceWind surfaceWind) {
        this.surfaceWind = Optional.of(Objects.requireNonNull(surfaceWind, "surfaceWind"));
        return self();
    }

    @Override
    public B setSurfaceWind(final Optional<? extends ObservedSurfaceWind> surfaceWind) {
        Objects.requireNonNull(surfaceWind, "surfaceWind");
        this.surfaceWind = surfaceWind.isPresent() ? Optional.of(surfaceWind.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearSurfaceWind() {
        this.surfaceWind = Optional.empty();
        return self();
    }

    @Override
    public Optional<HorizontalVisibility> getVisibility() {
        return visibility;
    }

    @Override
    public B setVisibility(final HorizontalVisibility visibility) {
        this.visibility = Optional.of(Objects.requireNonNull(visibility, "visibility"));
        return self();
    }

    @Override
    public B setVisibility(final Optional<? extends HorizontalVisibility> visibility) {
        Objects.requireNonNull(visibility, "visibility");
        this.visibility = visibility.isPresent() ? Optional.of(visibility.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearVisibility() {
        this.visibility = Optional.empty();
        return self();
    }

    @Override
    public Optional<List<RunwayVisualRange>> getRunwayVisualRanges() {
        return runwayVisualRanges;
    }

    @Override
    public B setRunwayVisualRanges(final List<RunwayVisualRange> runwayVisualRanges) {
        this.runwayVisualRanges = Optional.of(Objects.requireNonNull(runwayVisualRanges, "runwayVisualRanges"));
        return self();
    }

    @Override
    public B setRunwayVisualRanges(final Optional<? extends List<RunwayVisualRange>> runwayVisualRanges) {
        Objects.requireNonNull(runwayVisualRanges, "runwayVisualRanges");
        this.runwayVisualRanges = runwayVisualRanges.isPresent() ? Optional.of(runwayVisualRanges.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearRunwayVisualRanges() {
        this.runwayVisualRanges = Optional.empty();
        return self();
    }

    @Override
    public Optional<List<Weather>> getPresentWeather() {
        return presentWeather;
    }

    @Override
    public B setPresentWeather(final List<Weather> presentWeather) {
        this.presentWeather = Optional.of(Objects.requireNonNull(presentWeather, "presentWeather"));
        return self();
    }

    @Override
    public B setPresentWeather(final Optional<? extends List<Weather>> presentWeather) {
        Objects.requireNonNull(presentWeather, "presentWeather");
        this.presentWeather = presentWeather.isPresent() ? Optional.of(presentWeather.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearPresentWeather() {
        this.presentWeather = Optional.empty();
        return self();
    }

    @Override
    public Optional<ObservedClouds> getClouds() {
        return clouds;
    }

    @Override
    public B setClouds(final ObservedClouds clouds) {
        this.clouds = Optional.of(Objects.requireNonNull(clouds, "clouds"));
        return self();
    }

    @Override
    public B setClouds(final Optional<? extends ObservedClouds> clouds) {
        Objects.requireNonNull(clouds, "clouds");
        this.clouds = clouds.isPresent() ? Optional.of(clouds.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearClouds() {
        this.clouds = Optional.empty();
        return self();
    }

    @Override
    public Optional<List<Weather>> getRecentWeather() {
        return recentWeather;
    }

    @Override
    public B setRecentWeather(final List<Weather> recentWeather) {
        this.recentWeather = Optional.of(Objects.requireNonNull(recentWeather, "recentWeather"));
        return self();
    }

    @Override
    public B setRecentWeather(final Optional<? extends List<Weather>> recentWeather) {
        Objects.requireNonNull(recentWeather, "recentWeather");
        this.recentWeather = recentWeather.isPresent() ? Optional.of(recentWeather.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearRecentWeather() {
        this.recentWeather = Optional.empty();
        return self();
    }

    @Override
    public Optional<WindShear> getWindShear() {
        return windShear;
    }

    @Override
    public B setWindShear(final WindShear windShear) {
        this.windShear = Optional.of(Objects.requireNonNull(windShear, "windShear"));
        return self();
    }

    @Override
    public B setWindShear(final Optional<? extends WindShear> windShear) {
        Objects.requireNonNull(windShear, "windShear");
        this.windShear = windShear.isPresent() ? Optional.of(windShear.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearWindShear() {
        this.windShear = Optional.empty();
        return self();
    }

    @Override
    public Optional<SeaState> getSeaState() {
        return seaState;
    }

    @Override
    public B setSeaState(final SeaState seaState) {
        this.seaState = Optional.of(Objects.requireNonNull(seaState, "seaState"));
        return self();
    }

    @Override
    public B setSeaState(final Optional<? extends SeaState> seaState) {
        Objects.requireNonNull(seaState, "seaState");
        this.seaState = seaState.isPresent() ? Optional.of(seaState.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearSeaState() {
        this.seaState = Optional.empty();
        return self();
    }

    @Override
    public Optional<List<RunwayState>> getRunwayStates() {
        return runwayStates;
    }

    @Override
    public B setRunwayStates(final List<RunwayState> runwayStates) {
        this.runwayStates = Optional.of(Objects.requireNonNull(runwayStates, "runwayStates"));
        return self();
    }

    @Override
    public B setRunwayStates(final Optional<? extends List<RunwayState>> runwayStates) {
        Objects.requireNonNull(runwayStates, "runwayStates");
        this.runwayStates = runwayStates.isPresent() ? Optional.of(runwayStates.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearRunwayStates() {
        this.runwayStates = Optional.empty();
        return self();
    }

    @Override
    public Optional<List<TrendForecast>> getTrends() {
        return trends;
    }

    @Override
    public B setTrends(final List<TrendForecast> trends) {
        this.trends = Optional.of(Objects.requireNonNull(trends, "trends"));
        return self();
    }

    @Override
    public B setTrends(final Optional<? extends List<TrendForecast>> trends) {
        Objects.requireNonNull(trends, "trends");
        this.trends = trends.isPresent() ? Optional.of(trends.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearTrends() {
        this.trends = Optional.empty();
        return self();
    }

    @Override
    public Optional<AviationCodeListUser.ColorState> getColorState() {
        return colorState;
    }

    @Override
    public B setColorState(final AviationCodeListUser.ColorState colorState) {
        this.colorState = Optional.of(Objects.requireNonNull(colorState, "colorState"));
        return self();
    }

    @Override
    public B setColorState(final Optional<? extends AviationCodeListUser.ColorState> colorState) {
        Objects.requireNonNull(colorState, "colorState");
        this.colorState = colorState.isPresent() ? Optional.of(colorState.get()) : Optional.empty();
        return self();
    }

    @Override
    public B clearColorState() {
        this.colorState = Optional.empty();
        return self();
    }

    @Override
    public B clear() {
        this.aerodrome = null;
        this.reportStatus = null;
        this.translated = false;
        this.automatedStation = false;
        this.missingMessage = false;
        this.ceilingAndVisibilityOk = false;
        this.snowClosure = false;
        this.noSignificantChanges = false;
        this.remarks = Optional.empty();
        this.permissibleUsage = Optional.empty();
        this.permissibleUsageReason = Optional.empty();
        this.permissibleUsageSupplementary = Optional.empty();
        this.translatedBulletinID = Optional.empty();
        this.translatedBulletinReceptionTime = Optional.empty();
        this.translationCentreDesignator = Optional.empty();
        this.translationCentreName = Optional.empty();
        this.translationTime = Optional.empty();
        this.translatedTAC = Optional.empty();
        this.issueTime = Optional.empty();
        this.airTemperature = Optional.empty();
        this.dewpointTemperature = Optional.empty();
        this.altimeterSettingQNH = Optional.empty();
        this.surfaceWind = Optional.empty();
        this.visibility = Optional.empty();
        this.runwayVisualRanges = Optional.empty();
        this.presentWeather = Optional.empty();
        this.clouds = Optional.empty();
        this.recentWeather = Optional.empty();
        this.windShear = Optional.empty();
        this.seaState = Optional.empty();
        this.runwayStates = Optional.empty();
        this.trends = Optional.empty();
        this.colorState = Optional.empty();
        return self();
    }

    @Override
    public B mergeFrom(final T value) {
        Objects.requireNonNull(value, "value");
        this.aerodrome = value.getAerodrome();
        this.reportStatus = value.getReportStatus();
        this.translated = value.isTranslated();
        this.automatedStation = value.isAutomatedStation();
        this.missingMessage = value.isMissingMessage();
        this.ceilingAndVisibilityOk = value.isCeilingAndVisibilityOk();
        this.snowClosure = value.isSnowClosure();
        this.noSignificantChanges = value.isNoSignificantChanges();
        this.remarks = value.getRemarks();
        this.permissibleUsage = value.getPermissibleUsage();
        this.permissibleUsageReason = value.getPermissibleUsageReason();
        this.permissibleUsageSupplementary = value.getPermissibleUsageSupplementary();
        this.translatedBulletinID = value.getTranslatedBulletinID();
        this.translatedBulletinReceptionTime = value.getTranslatedBulletinReceptionTime();
        this.translationCentreDesignator = value.getTranslationCentreDesignator();
        this.translationCentreName = value.getTranslationCentreName();
        this.translationTime = value.getTranslationTime();
        this.translatedTAC = value.getTranslatedTAC();
        this.issueTime = value.getIssueTime();
        this.airTemperature = value.getAirTemperature();
        this.dewpointTemperature = value.getDewpointTemperature();
        this.altimeterSettingQNH = value.getAltimeterSettingQNH();
        this.surfaceWind = value.getSurfaceWind();
        this.visibility = value.getVisibility();
        this.runwayVisualRanges = value.getRunwayVisualRanges();
        this.presentWeather = value.getPresentWeather();
        this.clouds = value.getClouds();
        this.recentWeather = value.getRecentWeather();
        this.windShear = value.getWindShear();
        this.seaState = value.getSeaState();
        this.runwayStates = value.getRunwayStates();
        this.trends = value.getTrends();
        this.colorState = value.getColorState();
        return self();
    }

    @Override
    public B mergeFrom(final B template) {
        Objects.requireNonNull(template, "template");
        this.aerodrome = template.aerodrome;
        this.reportStatus = template.reportStatus;
        this.translated = template.translated;
        this.automatedStation = template.automatedStation;
        this.missingMessage = template.missingMessage;
        this.ceilingAndVisibilityOk = template.ceilingAndVisibilityOk;
        this.snowClosure = template.snowClosure;
        this.noSignificantChanges = template.noSignificantChanges;
        this.remarks = template.remarks;
        this.permissibleUsage = template.permissibleUsage;
        this.permissibleUsageReason = template.permissibleUsageReason;
        this.permissibleUsageSupplementary = template.permissibleUsageSupplementary;
        this.translatedBulletinID = template.translatedBulletinID;
        this.translatedBulletinReceptionTime = template.translatedBulletinReceptionTime;
        this.translationCentreDesignator = template.translationCentreDesignator;
        this.translationCentreName = template.translationCentreName;
        this.translationTime = template.translationTime;
        this.translatedTAC = template.translatedTAC;
        this.issueTime = template.issueTime;
        this.airTemperature = template.airTemperature;
        this.dewpointTemperature = template.dewpointTemperature;
        this.altimeterSettingQNH = template.altimeterSettingQNH;
        this.surfaceWind = template.surfaceWind;
        this.visibility = template.visibility;
        this.runwayVisualRanges = template.runwayVisualRanges;
        this.presentWeather = template.presentWeather;
        this.clouds = template.clouds;
        this.recentWeather = template.recentWeather;
        this.windShear = template.windShear;
        this.seaState = template.seaState;
        this.runwayStates = template.runwayStates;
        this.trends = template.trends;
        this.colorState = template.colorState;
        return self();
    }

    /**
     * Copies the properties declared by {@link MeteorologicalTerminalAirReportBuilder} (i.e. every
     * property this base class tracks) from any {@link MeteorologicalTerminalAirReport}. Subclasses
     * (see {@code METARImpl.Builder}/{@code SPECIImpl.Builder}) override this to also copy their own
     * additional properties.
     */
    @Override
    public B copyFrom(final MeteorologicalTerminalAirReport value) {
        Objects.requireNonNull(value, "value");
        MeteorologicalTerminalAirReportBuilderHelper.copyFrom(self(), value);
        return self();
    }
}

