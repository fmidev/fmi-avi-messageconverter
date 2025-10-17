package fi.fmi.avi.model.swx.amd82;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class SpaceWeatherPhenomenonTest {
    @Test
    public void covers_all_combinations_of_type_and_severity() {
        final EnumSet<SpaceWeatherPhenomenon> result = EnumSet.noneOf(SpaceWeatherPhenomenon.class);
        for (final SpaceWeatherPhenomenon.Type type : SpaceWeatherPhenomenon.Type.values()) {
            for (final SpaceWeatherPhenomenon.Severity severity : SpaceWeatherPhenomenon.Severity.values()) {
                result.add(SpaceWeatherPhenomenon.from(type, severity));
            }
        }
        assertEquals(EnumSet.allOf(SpaceWeatherPhenomenon.class), result);
    }

    @Test
    public void from_given_type_and_severity_of_value_returns_same_instance() {
        for (final SpaceWeatherPhenomenon value : SpaceWeatherPhenomenon.values()) {
            assertSame(value, SpaceWeatherPhenomenon.from(value.getType(), value.getSeverity()));
        }
    }

    @Test
    public void combined_codes_are_unique() {
        final Set<String> codes = new HashSet<>();
        for (final SpaceWeatherPhenomenon value : SpaceWeatherPhenomenon.values()) {
            final String code = value.asCombinedCode();
            assertFalse("unexpectedly duplicated: " + code, codes.contains(code));
            codes.add(code);
        }
    }

    @Test
    public void wmoCodeListValues_are_unique() {
        final Set<String> codes = new HashSet<>();
        for (final SpaceWeatherPhenomenon value : SpaceWeatherPhenomenon.values()) {
            final String code = value.asWMOCodeListValue();
            assertFalse("unexpectedly duplicated: " + code, codes.contains(code));
            codes.add(code);
        }
    }

    @Test
    public void fromCombinedCode_given_result_of_asCombinedCode_returns_same_instance() {
        for (final SpaceWeatherPhenomenon value : SpaceWeatherPhenomenon.values()) {
            assertSame(value, SpaceWeatherPhenomenon.fromCombinedCode(value.asCombinedCode()));
        }
    }

    @Test
    public void fromCombinedCode_given_illegal_code_throws_exception() {
        final List<String> passedIllegalCodes = Stream.of("", "GNSS_MOD", "GNSS_MOD", "GNSS_SEV", "HF_COM_MOD", "HF_COM_SEV", "SATCO MOD", "SATCOM MO",
                        "SATCOMM MOD", "SATCOM MODD", "SATCOM_ MOD", "SATCOM _MOD", "SATCOM  MOD", "satcom mod")//
                .filter(code -> {
                    try {
                        SpaceWeatherPhenomenon.fromCombinedCode(code);
                        return true;
                    } catch (final IllegalArgumentException e) {
                        return false;
                    }
                })//
                .collect(Collectors.toList());
        assertEquals(Collections.emptyList(), passedIllegalCodes);
    }

    @Test
    public void fromWMOCodeListValue_given_result_of_asWMOCodeListValue_returns_same_instance() {
        for (final SpaceWeatherPhenomenon value : SpaceWeatherPhenomenon.values()) {
            assertSame(value, SpaceWeatherPhenomenon.fromWMOCodeListValue(value.asWMOCodeListValue()));
        }
    }

    @Test
    public void fromWMOCodeListValue_given_illegal_code_throws_exception() {
        final List<String> passedIllegalCodes = Stream.concat(//
                        Stream.of("", "GNSS MOD", "GNSS MOD", "GNSS SEV", "HF COM MOD", "HF COM SEV", "SATCO_MOD", "SATCOM_MO", "SATCOMM_MOD", "SATCOM_MODD",
                                        "SATCOM_ MOD", "SATCOM _MOD", "SATCOM__MOD", "satcom_mod")//
                                .map(code -> "http://codes.wmo.int/49-2/SpaceWxPhenomena/" + code), //
                        Stream.of("", "http://codes.wmo.int/49-2/SpaceWxPhenomen/SATCOM_MOD", "http://codes.wmo.int/49-2/SpaceWxPhenomenaa/SATCOM_MOD",
                                "http://wmo.int/49-2/SpaceWxPhenomena/SATCOM_MOD", "http://codes.wmo.int/49-3/SpaceWxPhenomena/SATCOM_MOD"))//
                .filter(code -> {
                    try {
                        SpaceWeatherPhenomenon.fromWMOCodeListValue(code);
                        return true;
                    } catch (final IllegalArgumentException e) {
                        return false;
                    }
                })//
                .collect(Collectors.toList());
        assertEquals(Collections.emptyList(), passedIllegalCodes);
    }
}
