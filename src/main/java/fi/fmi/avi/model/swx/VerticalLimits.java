package fi.fmi.avi.model.swx;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.NumericMeasure;

import java.util.Optional;

/**
 * Utility for Space Weather Advisory vertical limits.
 */
public interface VerticalLimits {

    /**
     * The vertical distance is measured with an altimeter set to the standard atmosphere.
     * See
     * <a href="http://aixm.aero/sites/aixm.aero/files/imce/AIXM511HTML/AIXM/DataType_CodeVerticalReferenceType.html">AIXM 5.1.1 CodeVerticalReferenceType</a>.
     */
    String STANDARD_ATMOSPHERE = "STD";

    Optional<NumericMeasure> getLowerLimit();

    Optional<NumericMeasure> getUpperLimit();

    Optional<AviationCodeListUser.RelationalOperator> getOperator();

    String getVerticalReference();

}
