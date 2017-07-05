package fi.fmi.avi.data.metar;

import java.util.List;

import fi.fmi.avi.data.AviationCodeListUser;
import fi.fmi.avi.data.RunwayDirection;

public interface WindShear extends AviationCodeListUser {

    boolean isAllRunways();

    List<RunwayDirection> getRunwayDirections();


    void setAllRunways(boolean allRunways);

    void setRunwayDirections(List<RunwayDirection> runwayDirections);

}
