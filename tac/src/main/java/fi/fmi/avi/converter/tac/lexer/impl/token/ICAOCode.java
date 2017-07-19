package fi.fmi.avi.converter.tac.lexer.impl.token;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity.AERODROME_DESIGNATOR;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.COUNTRY;
import static fi.fmi.avi.converter.tac.lexer.Lexeme.ParsedValueName.VALUE;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.impl.FactoryBasedReconstructor;
import fi.fmi.avi.converter.tac.lexer.impl.RegexMatchingLexemeVisitor;

/**
 * Created by rinne on 10/02/17.
 */
public class ICAOCode extends RegexMatchingLexemeVisitor {
    /*
        The code-to-country mapping is not really needed here in the lexer, but these could be useful in other classes.
        Updating the list is also less error-prone with the name of the country attached to the code.

        The list copied from https://en.wikipedia.org/wiki/International_Civil_Aviation_Organization_airport_code
        on 11th Jan 2017.
     */

    public enum ICAOCodeCountryPrefix {
        SOLOMON_ISLANDS("AG"),
        NAURU("AN"),
        PAPUA_NEW_GUINEA("AY"),
        GREENLAND("BG"),
        ICELAND("BI"),
        KOSOVO("BK"),
        CANADA("C"),
        ALGERIA("DA"),
        BENIN("DB"),
        BURKINA_FASO("DF"),
        GHANA("DG"),
        COTE_D_IVOIRE("DI"),
        NIGERIA("DN"),
        NIGER("DR"),
        TUNISIA("DT"),
        TOGOLESE_REPUBLIC("DX"),
        BELGIUM("EB"),
        GERMANY("ED", "ET"),
        ESTONIA("EE"),
        FINLAND("EF"),
        UNITED_KINDOM("EG", "SF", "TQ", "TR", "TU", "TX"),
        NETHERLANDS("EH"),
        IRELAND("EI"),
        DENMARK("EK"),
        LUXEMBOURGH("EL"),
        NORWAY("EN"),
        POLAND("EP"),
        SWEDEN("ES"),
        LATVIA("EV"),
        LITHUANIA("EY"),
        SOUTH_AFRICA("FA"),
        BOTSWANA("FB"),
        REPUBLIC_OF_CONGO("FC"),
        SWAZILAND("FD"),
        CENTRAL_AFRICAN_REPUBLIC("FE"),
        EQUITORIAL_GUINEA("FG"),
        SAINT_HELENA_ASCENSION_TRISTAN_DA_CUNHA("FH"),
        MAURITIUS("FI"),
        BRITISH_INDIAN_OCEAN_TERRITORY("FJ"),
        CAMEROON("FK"),
        ZAMBIA("FL"),
        COMOROS_MAYOTTE_REUNION_MADAGASCAR("FM"),
        ANGOLA("FN"),
        GABON("FO"),
        SAO_TOME_PRINCIPE("FP"),
        MOZAMBIQUE("FQ"),
        SEYCHELLES("FS"),
        CHAD("FT"),
        ZIMBABWE("FV"),
        MALAWI("FW"),
        LESOTHO("FX"),
        NAMIBIA("FY"),
        DEMOCRATIC_REPUBLIC_CONGO("FZ"),
        MALI("GA"),
        GAMBIA("GB"),
        SIERRA_LEONE("GF"),
        GUINEA_BISSAU("GG"),
        LIBERIA("GL"),
        MOROCCO("GM"),
        SENEGAL("GO"),
        MAURITANIA("GQ"),
        WESTERN_SAHARA("GS"),
        GUINEA("GU"),
        CAPE_VERDE("GV"),
        ETHIOPIA("HA"),
        BURUNDI("HB"),
        SOMALIA("HC"),
        DJIBOUTI("HD"),
        EGYPT("HE"),
        ERITREA("HH"),
        KENYA("HK"),
        LIBYA("HL"),
        RWANDA("HR"),
        SUDAN("HS"),
        TANZANIA("HT"),
        UGANDA("HU"),
        USA("K", "NS", "PA", "PB", "PF", "PG", "PH", "PJ", "PM", "PO", "PP", "PW", "TI", "TJ"),
        ALBANIA("LA"),
        BULGARIA("LB"),
        CYPRYS("LC"),
        CROATIA("LD"),
        SPAIN("LE", "GC", "GE"),
        FRANCE("LF", "NT", "NW", "SO", "TF"),
        GREECE("LG"),
        HUNGARY("LH"),
        ITALY("LI"),
        SLOVENIA("LJ"),
        CZECH_REPUBLIC("LK"),
        ISRAEL("LL"),
        MALTA("LM"),
        MONACO("LN"),
        AUSTRIA("LO"),
        PORTUGAL("LP"),
        BOSNIA_HERZEGOVINA("LQ"),
        ROMANIA("LR"),
        SWITZERLAND("LS"),
        TURKEY("LT"),
        MOLDOVA("LU"),
        PALESTINIAN_TERRITORIES("LV"),
        MACEDONIA("LW"),
        GIBRALTAR("LX"),
        SERBIA_MONTENEGRO("LY"),
        SLOVAKIA("LZ"),
        TURKS_CAICOS_ISLANDS("MB"),
        DOMINICAN_REPUBLIC("MD"),
        GUATEMALA("MG"),
        HONDURAS("MH"),
        JAMAICA("MK"),
        MEXICO("MM"),
        NICARAGUA("MN"),
        PANAMA("MP"),
        COSTA_RICA("MR"),
        EL_SALVADOR("MS"),
        HAITI("MT"),
        CUBA("MU"),
        CAYMAN_ISLANDS("MW"),
        BAHAMAS("MY"),
        BELIZE("MZ"),
        COOK_ISLANDS("NC"),
        FIJI_TONGA("NF"),
        KIRIBATI_TUVALU("NG"),
        NIUE("NI"),
        VANUATU("NV"),
        NEW_ZEALAND("NZ"),
        AFGANISTAN("OA"),
        BAHRAIN("OB"),
        SAUDI_ARABIA("OE"),
        IRAN("OI"),
        JORDAN_WEST_BANK("OJ"),
        KUWAIT("OK"),
        LEBANON("OL"),
        UNITED_ARAB_EMIRATES("OM"),
        OMAN("OO"),
        PAKISTAN("OP"),
        IRAQ("OR"),
        SYRIA("OS"),
        QATAR("OT"),
        YEMEN("OY"),
        KIRIBATI("PC", "PL"),
        MARSHALL_ISLANDS("PK"),
        FEDRATED_STATES_MICRONESIA("PT"),
        REPUBLIC_CHINA_TAIWAN("RC"),
        JAPAN("RJ", "RO"),
        REPUBLIC_KOREA("RK"),
        PHILIPPINES("RP"),
        ARGENTINA("SA"),
        BRAZIL("SB", "SD", "SI", "SJ", "SN", "SS", "SW"),
        CHILE("SC", "SH"),
        EQUADOR("SE"),
        PARAGUAY("SG"),
        COLUMBIA("SK"),
        BOLIVIA("SL"),
        SURINAME("SM"),
        PERU("SP"),
        URUGUAY("SU"),
        VENEZUELA("SV"),
        GUYANA("SY"),
        ANTUGUA_BARBUDA("TA"),
        BARBADOS("TB"),
        DOMINICA("TD"),
        GRANADA("TG"),
        SAINT_KITTS_NEVIS("TK"),
        SAINT_LUCIA("TL"),
        CARIBBEAN_NETHERLANDS_ARUBA_CURACAO_SINT_MAARTEN("TN"),
        TRINIDAD_TOBACO("TT"),
        SAINT_VICANT_GRENADINES("TV"),
        RUSSIA("U"),
        KAZAKHSTAN("UA"),
        AZERBAIJAN("UB"),
        KYRGYZSTAN("UC"),
        ARMENIA("UD"),
        GEORGIA("UG"),
        UKRAINE("UK"),
        BELARUS_KALINGRAD("UM"),
        TAJIKISTAN("UT"),
        INDIA("VA", "VE", "VI", "VO"),
        SRI_LANKA("VC"),
        CAMBODIA("VD"),
        BANGLADSH("VG"),
        HONG_KONG("VH"),
        LAOS("VL"),
        MACAU("VM"),
        NEPAL("VN"),
        BHUTAN("VQ"),
        MALDIVES("VR"),
        THAILAND("VT"),
        VIETNAM("VV"),
        MYANMAR("VY"),
        INDONESIA("WA", "WI", "WQ", "WR"),
        BRUNEI_EAST_MALAYSIA("WB"),
        MALAYSIA("WM"),
        TIMOR_LESTE("WP"),
        SINGAPORE("WS"),
        AUSTRALIA("Y"),
        CHINA("Z"),
        DEMOCRATIC_PEOPLES_REPUBLIC_KOREA("ZK"),
        MONGOLIA("ZM"),
        GOV_MIL_EXPERIMENTAL_INTERNAL("X"),
        NO_CODE("ZZZZ");

        private final String[] codes;

        ICAOCodeCountryPrefix(final String... codes) {
            this.codes = codes;
        }

        public static Map<String, ICAOCodeCountryPrefix> getCodeToCountryMap() {
            ICAOCodeCountryPrefix[] all = ICAOCodeCountryPrefix.values();
            HashMap<String, ICAOCodeCountryPrefix> retval = new HashMap<String, ICAOCodeCountryPrefix>();
            for (ICAOCodeCountryPrefix prefix : all) {
                for (String code : prefix.codes) {
                    if (retval.put(code, prefix) != null) {
                        throw new RuntimeException("Duplicate ICAO country code prefix '" + code + "' in enum MetarLexer.ICAOCodeCountryPrefix values, this is"
                                + " a coding error");
                    }
                }
            }
            return retval;
        }
    }

    private final static Map<String, ICAOCodeCountryPrefix> codeToCountryMap = ICAOCodeCountryPrefix.getCodeToCountryMap();

    public ICAOCode(final Priority prio) {
        super("^[A-Z]{4,}$", prio);
    }

    @Override
    public void visitIfMatched(final Lexeme token, final Matcher match, final ConversionHints hints) {
        //Must be second or third token:
        if (token.getPrevious() == token.getFirst() || (token.hasPrevious() && token.getPrevious().getPrevious() == token.getFirst())) {
            for (String s : codeToCountryMap.keySet()) {
                if (token.getTACToken().startsWith(s)) {
                	token.identify(AERODROME_DESIGNATOR);
                	token.setParsedValue(COUNTRY, codeToCountryMap.get(s));
                    token.setParsedValue(VALUE,token.getTACToken());
                    return;
                }
            }
            token.identify(AERODROME_DESIGNATOR, Lexeme.Status.SYNTAX_ERROR, "Invalid ICAO code country prefix");
        }
    }

    public static class Reconstructor extends FactoryBasedReconstructor {

        @Override
        public <T extends AviationWeatherMessage> Lexeme getAsLexeme(final T msg, Class<T> clz, final ConversionHints hints, final Object... specifier) {
            Lexeme retval = null;
            if (METAR.class.isAssignableFrom(clz)) {
                METAR m = (METAR) msg;
                if (m.getAerodrome() != null) {
                    retval = this.createLexeme(m.getAerodrome().getDesignator(), AERODROME_DESIGNATOR);
                }

            } else if (TAF.class.isAssignableFrom(clz)) {
                TAF t = (TAF) msg;
                if (t.getAerodrome() != null) {
                    retval = this.createLexeme(t.getAerodrome().getDesignator(), AERODROME_DESIGNATOR);
                }
            }
            return retval;
        }
    }
}
