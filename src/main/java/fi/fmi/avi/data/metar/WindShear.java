package fi.fmi.avi.data.metar;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;

public interface WindShear extends AviationCodeListUser {

    boolean isAllRunways();

    List<String> getRunwayDirectionDesignators();


    void setAllRunways(boolean allRunways);

    void setRunwayDirectionDesignators(List<String> runwayDirectionDesignators);

}
