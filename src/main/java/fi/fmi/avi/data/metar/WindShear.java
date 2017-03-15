package fi.fmi.avi.data.metar;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.AviationCodeListUser.MissingReason;
import fi.fmi.avi.data.PossiblyMissingContent;

public interface WindShear extends AviationCodeListUser, PossiblyMissingContent {

    boolean isAllRunways();

    List<String> getRunwayDirectionDesignators();


    void setAllRunways(boolean allRunways);

    void setRunwayDirectionDesignators(List<String> runwayDirectionDesignators);

}
