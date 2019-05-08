package fi.fmi.avi.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.model.BulletinHeading;
import fi.fmi.avi.model.PartialDateTime;
import fi.fmi.avi.model.PartialOrCompleteTimeInstant;
import fi.fmi.avi.model.immutable.BulletinHeadingImpl;

public class BulletinHeadingDecoder {
    private static final Pattern ABBREVIATED_HEADING = Pattern.compile(
        "^(?<TT>[A-Z]{2})(?<AA>[A-Z]{2})(?<ii>[0-9]{2})\\s*(?<CCCC>[A-Z]{4})\\s*(?<YY>[0-9]{2})(?<GG>[0-9]{2})(?<gg>[0-9]{2})\\s*(?<BBB>(CC|RR|AA)[A-Z])?$");

    public static BulletinHeading decode(final String input, final ConversionHints hints) throws IllegalArgumentException {
        final ConversionResult<BulletinHeading> retval = new ConversionResult<>();
        final Matcher m = ABBREVIATED_HEADING.matcher(input);
        if (!m.matches()) {
            throw new IllegalArgumentException(
                    "String '" + input + "' does not match the Abbreviated heading formats 'T1T2A1A2iiCCCCYYGGgg[BBB]' "
                            + "or 'T1T2A1A2ii CCCC YYGGgg[ BBB]' as defined in "
                            + "WMO-No. 386 Manual on the Global Telecommunication System, 2015 edition (updated 2017)");
        }
        final String bbb = m.group("BBB");
        BulletinHeading.Type type = BulletinHeading.Type.NORMAL;
        Integer bulletinAugmentationNumber = null;
        if (bbb != null) {
            type = BulletinHeading.Type.fromCode(bbb.substring(0, 2));
            bulletinAugmentationNumber = bbb.charAt(2) - 'A' + 1;
        }
        final BulletinHeading.DataTypeDesignatorT2 t2;
        final BulletinHeading.DataTypeDesignatorT1 t1 = BulletinHeading.DataTypeDesignatorT1.fromCode(m.group("TT").charAt(0));
        if (BulletinHeading.DataTypeDesignatorT1.FORECASTS == t1) {
            t2 = BulletinHeading.ForecastsDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
        } else if (BulletinHeading.DataTypeDesignatorT1.WARNINGS == t1) {
            t2 = BulletinHeading.WarningsDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
        } else if (BulletinHeading.DataTypeDesignatorT1.AVIATION_INFORMATION_IN_XML == t1) {
            t2 = BulletinHeading.XMLDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
        } else if (BulletinHeading.DataTypeDesignatorT1.UPPER_AIR_DATA == t1) {
            t2 = BulletinHeading.UpperAirDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
        } else if (BulletinHeading.DataTypeDesignatorT1.SURFACE_DATA == t1) {
            t2 = BulletinHeading.SurfaceDataTypeDesignatorT2.fromCode(m.group("TT").charAt(1));
        } else {
            throw new IllegalArgumentException("Only forecast ('F') , warning ('W'), XML('L'), upper-air ('U') and surface data ('S') type headings "
                    + "currently supported, t1 is '" + t1 + "'");
        }
        final String issueTime = "--" + m.group("YY") + "T" + m.group("GG") + ":" + m.group("gg");
        return BulletinHeadingImpl.builder()//
                .setLocationIndicator(m.group("CCCC"))//
                .setGeographicalDesignator(m.group("AA"))//
                .setBulletinNumber(Integer.parseInt(m.group("ii")))//
                .setType(type)//
                .setBulletinAugmentationNumber(Optional.ofNullable(bulletinAugmentationNumber))//
                .setDataTypeDesignatorT1ForTAC(t1)//
                .setDataTypeDesignatorT2(t2)//
                .setIssueTime(PartialOrCompleteTimeInstant.of(PartialDateTime.parse(issueTime)))//
                .build();
    }
}
