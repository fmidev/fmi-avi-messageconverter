package fi.fmi.avi.model.metar;

import java.util.List;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.RunwayDirection;

public interface WindShear extends AviationCodeListUser {

    boolean isAllRunways();

    List<RunwayDirection> getRunwayDirections();


    void setAllRunways(boolean allRunways);

    void setRunwayDirections(List<RunwayDirection> runwayDirections);

}
