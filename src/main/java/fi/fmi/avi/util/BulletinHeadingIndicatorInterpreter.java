package fi.fmi.avi.util;

import java.util.function.Function;

/**
 * The result of this function should be a valid BBB indicator as defined in WMO-No. 386 Manual on the Global Telecommunication System, 2015 edition (updated
 * 2017).
 */
public interface BulletinHeadingIndicatorInterpreter extends Function<String, String> {
}
