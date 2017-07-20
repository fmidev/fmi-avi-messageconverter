package fi.fmi.avi.converter.tac.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fi.fmi.avi.converter.AviMessageConverter;
import fi.fmi.avi.converter.AviMessageSpecificConverter;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.converter.ConversionSpecification;
import fi.fmi.avi.converter.tac.METARTACParser;
import fi.fmi.avi.converter.tac.METARTACSerializer;
import fi.fmi.avi.converter.tac.TACParser;
import fi.fmi.avi.converter.tac.TAFTACParser;
import fi.fmi.avi.converter.tac.TAFTACSerializer;
import fi.fmi.avi.converter.tac.lexer.AviMessageLexer;
import fi.fmi.avi.converter.tac.lexer.AviMessageTACTokenizer;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexingFactory;
import fi.fmi.avi.converter.tac.lexer.impl.AviMessageLexerImpl;
import fi.fmi.avi.converter.tac.lexer.impl.AviMessageTACTokenizerImpl;
import fi.fmi.avi.converter.tac.lexer.impl.LexingFactoryImpl;
import fi.fmi.avi.converter.tac.lexer.impl.RecognizingAviMessageTokenLexer;
import fi.fmi.avi.converter.tac.lexer.impl.PrioritizedLexemeVisitor.Priority;
import fi.fmi.avi.converter.tac.lexer.impl.token.AirDewpointTemperature;
import fi.fmi.avi.converter.tac.lexer.impl.token.Amendment;
import fi.fmi.avi.converter.tac.lexer.impl.token.AtmosphericPressureQNH;
import fi.fmi.avi.converter.tac.lexer.impl.token.AutoMetar;
import fi.fmi.avi.converter.tac.lexer.impl.token.CAVOK;
import fi.fmi.avi.converter.tac.lexer.impl.token.Cancellation;
import fi.fmi.avi.converter.tac.lexer.impl.token.ChangeForecastTimeGroup;
import fi.fmi.avi.converter.tac.lexer.impl.token.CloudLayer;
import fi.fmi.avi.converter.tac.lexer.impl.token.ColorCode;
import fi.fmi.avi.converter.tac.lexer.impl.token.Correction;
import fi.fmi.avi.converter.tac.lexer.impl.token.EndToken;
import fi.fmi.avi.converter.tac.lexer.impl.token.ForecastChangeIndicator;
import fi.fmi.avi.converter.tac.lexer.impl.token.ForecastMaxMinTemperature;
import fi.fmi.avi.converter.tac.lexer.impl.token.FractionalHorizontalVisibility;
import fi.fmi.avi.converter.tac.lexer.impl.token.ICAOCode;
import fi.fmi.avi.converter.tac.lexer.impl.token.IssueTime;
import fi.fmi.avi.converter.tac.lexer.impl.token.MetarStart;
import fi.fmi.avi.converter.tac.lexer.impl.token.MetricHorizontalVisibility;
import fi.fmi.avi.converter.tac.lexer.impl.token.Nil;
import fi.fmi.avi.converter.tac.lexer.impl.token.NoSignificantWeather;
import fi.fmi.avi.converter.tac.lexer.impl.token.Remark;
import fi.fmi.avi.converter.tac.lexer.impl.token.RemarkStart;
import fi.fmi.avi.converter.tac.lexer.impl.token.RunwayState;
import fi.fmi.avi.converter.tac.lexer.impl.token.RunwayVisualRange;
import fi.fmi.avi.converter.tac.lexer.impl.token.SeaState;
import fi.fmi.avi.converter.tac.lexer.impl.token.SnowClosure;
import fi.fmi.avi.converter.tac.lexer.impl.token.SurfaceWind;
import fi.fmi.avi.converter.tac.lexer.impl.token.TAFStart;
import fi.fmi.avi.converter.tac.lexer.impl.token.ValidTime;
import fi.fmi.avi.converter.tac.lexer.impl.token.VariableSurfaceWind;
import fi.fmi.avi.converter.tac.lexer.impl.token.Weather;
import fi.fmi.avi.converter.tac.lexer.impl.token.WindShear;

/**
 * Created by rinne on 10/02/17.
 */
@Configuration
public class TACConverter {
    /**
     * Pre-configured spec for ICAO Annex 3 TAC format to {@link METAR} POJO.
     */
    public static final ConversionSpecification<String, METAR> TAC_TO_METAR_POJO = new ConversionSpecification<>(String.class, METAR.class, "ICAO Annex 3 TAC",
            null);

    /**
     * Pre-configured spec for {@link METAR} to ICAO Annex 3 TAC String.
     */
    public static final ConversionSpecification<METAR, String> METAR_POJO_TO_TAC = new ConversionSpecification<>(METAR.class, String.class, null,
            "ICAO Annex 3 TAC");

    /**
     * Pre-configured spec for ICAO Annex 3 TAC format to {@link TAF} POJO.
     */
    public static final ConversionSpecification<String, TAF> TAC_TO_TAF_POJO = new ConversionSpecification<>(String.class, TAF.class, "ICAO Annex 3 TAC", null);

    /**
     * Pre-configured spec for {@link TAF} to ICAO Annex 3 TAC String.
     */
    public static final ConversionSpecification<TAF, String> TAF_POJO_TO_TAC = new ConversionSpecification<>(TAF.class, String.class, null, "ICAO Annex 3 TAC");


    @Bean
    public AviMessageLexer aviMessageLexer() {
        AviMessageLexerImpl l = new AviMessageLexerImpl();
        l.setLexingFactory(lexingFactory());
        l.addTokenLexer("METAR", metarTokenLexer());
        l.addTokenLexer("TAF", tafTokenLexer());
        return l;
    }

    @Bean
    public AviMessageConverter aviMessageConverter() {
        AviMessageConverter p = new AviMessageConverter();
        p.setMessageSpecificConverter(TAC_TO_METAR_POJO, metarTACParser());
        p.setMessageSpecificConverter(TAC_TO_TAF_POJO, tafTACParser());
        p.setMessageSpecificConverter(METAR_POJO_TO_TAC, metarTACSerializer());
        p.setMessageSpecificConverter(TAF_POJO_TO_TAC, tafTACSerializer());
        return p;
    }

    @Bean
    public AviMessageTACTokenizer tacTokenizer() {
        AviMessageTACTokenizerImpl tokenizer = new AviMessageTACTokenizerImpl();
        tokenizer.setMETARSerializer(metarTACSerializer());
        tokenizer.setTAFSerializer(tafTACSerializer());
        return tokenizer;
    }
    
    @Bean
    public LexingFactory lexingFactory() {
        return new LexingFactoryImpl();
    }
    
    
    AviMessageSpecificConverter<String, METAR> metarTACParser() {
        TACParser<METAR> p = new METARTACParser();
        p.setTACLexer(aviMessageLexer());
        return p;
    }

    AviMessageSpecificConverter<String, TAF> tafTACParser() {
        TACParser<TAF> p = new TAFTACParser();
        p.setTACLexer(aviMessageLexer());
        return p;
    }

    METARTACSerializer metarTACSerializer() {
        METARTACSerializer s = new METARTACSerializer();
        s.setLexingFactory(lexingFactory());
        s.addReconstructor(Lexeme.Identity.METAR_START, new MetarStart.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CORRECTION, new Correction.Reconstructor());
        s.addReconstructor(Lexeme.Identity.AERODROME_DESIGNATOR, new ICAOCode.Reconstructor());
        s.addReconstructor(Lexeme.Identity.ISSUE_TIME, new IssueTime.Reconstructor());
        s.addReconstructor(Lexeme.Identity.AUTOMATED, new AutoMetar.Reconstructor());
        s.addReconstructor(Lexeme.Identity.SURFACE_WIND, new SurfaceWind.Reconstructor());
        s.addReconstructor(Lexeme.Identity.VARIABLE_WIND_DIRECTION, new VariableSurfaceWind.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CAVOK, new CAVOK.Reconstructor());
        s.addReconstructor(Lexeme.Identity.HORIZONTAL_VISIBILITY, new MetricHorizontalVisibility.Reconstructor());
        s.addReconstructor(Lexeme.Identity.WEATHER, new Weather.Reconstructor(false));
        s.addReconstructor(Lexeme.Identity.NO_SIGNIFICANT_WEATHER, new NoSignificantWeather.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CLOUD, new CloudLayer.Reconstructor());
        s.addReconstructor(Lexeme.Identity.FORECAST_CHANGE_INDICATOR, new ForecastChangeIndicator.Reconstructor());
        s.addReconstructor(Lexeme.Identity.RUNWAY_VISUAL_RANGE, new RunwayVisualRange.Reconstructor());
        s.addReconstructor(Lexeme.Identity.AIR_DEWPOINT_TEMPERATURE, new AirDewpointTemperature.Reconstructor());
        s.addReconstructor(Lexeme.Identity.AIR_PRESSURE_QNH, new AtmosphericPressureQNH.Reconstructor());
        s.addReconstructor(Lexeme.Identity.RECENT_WEATHER, new Weather.Reconstructor(true));
        s.addReconstructor(Lexeme.Identity.WIND_SHEAR, new WindShear.Reconstructor());
        s.addReconstructor(Lexeme.Identity.SEA_STATE, new SeaState.Reconstructor());
        s.addReconstructor(Lexeme.Identity.RUNWAY_STATE, new RunwayState.Reconstructor());
        s.addReconstructor(Lexeme.Identity.COLOR_CODE, new ColorCode.Reconstructor());
        s.addReconstructor(Lexeme.Identity.REMARKS_START, new RemarkStart.Reconstructor());
        s.addReconstructor(Lexeme.Identity.END_TOKEN, new EndToken.Reconstructor());
        return s;
    }

    TAFTACSerializer tafTACSerializer() {
        TAFTACSerializer s = new TAFTACSerializer();
        s.setLexingFactory(lexingFactory());
        s.addReconstructor(Lexeme.Identity.TAF_START, new TAFStart.Reconstructor());
        s.addReconstructor(Lexeme.Identity.AMENDMENT, new Amendment.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CORRECTION, new Correction.Reconstructor());
        s.addReconstructor(Lexeme.Identity.AERODROME_DESIGNATOR, new ICAOCode.Reconstructor());
        s.addReconstructor(Lexeme.Identity.ISSUE_TIME, new IssueTime.Reconstructor());
        s.addReconstructor(Lexeme.Identity.NIL, new Nil.Reconstructor());
        s.addReconstructor(Lexeme.Identity.VALID_TIME, new ValidTime.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CANCELLATION, new Cancellation.Reconstructor());
        s.addReconstructor(Lexeme.Identity.SURFACE_WIND, new SurfaceWind.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CAVOK, new CAVOK.Reconstructor());
        s.addReconstructor(Lexeme.Identity.HORIZONTAL_VISIBILITY, new MetricHorizontalVisibility.Reconstructor());
        s.addReconstructor(Lexeme.Identity.WEATHER, new Weather.Reconstructor(false));
        s.addReconstructor(Lexeme.Identity.NO_SIGNIFICANT_WEATHER, new NoSignificantWeather.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CLOUD, new CloudLayer.Reconstructor());
        s.addReconstructor(Lexeme.Identity.MAX_TEMPERATURE, new ForecastMaxMinTemperature.Reconstructor());
        // No need to register MIN_TEMPERATURE as ForecastMaxMinTemperature.Reconstructor will do both if both set
        s.addReconstructor(Lexeme.Identity.FORECAST_CHANGE_INDICATOR, new ForecastChangeIndicator.Reconstructor());
        s.addReconstructor(Lexeme.Identity.CHANGE_FORECAST_TIME_GROUP, new ChangeForecastTimeGroup.Reconstructor());
        s.addReconstructor(Lexeme.Identity.REMARKS_START, new RemarkStart.Reconstructor());
        s.addReconstructor(Lexeme.Identity.END_TOKEN,new EndToken.Reconstructor());
        return s;
    }

    RecognizingAviMessageTokenLexer metarTokenLexer() {
        RecognizingAviMessageTokenLexer l = new RecognizingAviMessageTokenLexer();

        //The METAR token lexer can understand the following tokens (low priority = should be tried sooner):
        l.teach(new MetarStart(Priority.HIGH));
        l.teach(new ICAOCode(Priority.LOW));
        l.teach(new IssueTime(Priority.LOW));
        l.teach(new CloudLayer(Priority.HIGH));
        l.teach(new Weather(Priority.NORMAL));
        l.teach(new SurfaceWind(Priority.LOW));
        l.teach(new VariableSurfaceWind(Priority.LOW));
        l.teach(new MetricHorizontalVisibility(Priority.NORMAL));
        l.teach(new FractionalHorizontalVisibility(Priority.NORMAL));
        l.teach(new ForecastChangeIndicator(Priority.LOW));
        l.teach(new ColorCode(Priority.LOW));
        l.teach(new CAVOK(Priority.LOW));
        l.teach(new Correction(Priority.LOW));
        l.teach(new RunwayVisualRange(Priority.HIGH));
        l.teach(new AirDewpointTemperature(Priority.LOW));
        l.teach(new AtmosphericPressureQNH(Priority.LOW));
        l.teach(new RunwayState(Priority.LOW));
        l.teach(new SnowClosure(Priority.LOW));
        l.teach(new AutoMetar(Priority.LOW));
        l.teach(new NoSignificantWeather(Priority.LOW));
        l.teach(new RemarkStart(Priority.HIGH));
        l.teach(new Remark(Priority.HIGH));
        l.teach(new WindShear(Priority.LOW));
        l.teach(new SeaState(Priority.LOW));
        l.teach(new EndToken(Priority.LOW));
        return l;
    }

    RecognizingAviMessageTokenLexer tafTokenLexer() {
        RecognizingAviMessageTokenLexer l = new RecognizingAviMessageTokenLexer();
        l.teach(new TAFStart(Priority.HIGH));
        l.teach(new ICAOCode(Priority.LOW));
        l.teach(new ValidTime(Priority.LOW));
        l.teach(new IssueTime(Priority.LOW));
        l.teach(new CloudLayer(Priority.HIGH));
        l.teach(new Weather(Priority.NORMAL));
        l.teach(new SurfaceWind(Priority.LOW));
        l.teach(new VariableSurfaceWind(Priority.LOW));
        l.teach(new MetricHorizontalVisibility(Priority.NORMAL));
        l.teach(new FractionalHorizontalVisibility(Priority.NORMAL));
        l.teach(new ForecastChangeIndicator(Priority.LOW));
        l.teach(new ChangeForecastTimeGroup(Priority.LOW));
        l.teach(new Correction(Priority.LOW));
        l.teach(new Amendment(Priority.LOW));
        l.teach(new Nil(Priority.HIGH));
        l.teach(new Cancellation(Priority.LOW));
        l.teach(new CAVOK(Priority.LOW));
        l.teach(new NoSignificantWeather(Priority.LOW));
        l.teach(new ForecastMaxMinTemperature(Priority.LOW));
        l.teach(new RemarkStart(Priority.HIGH));
        l.teach(new Remark(Priority.HIGH));
        l.teach(new EndToken(Priority.LOW));
        return l;
    }

    

}
