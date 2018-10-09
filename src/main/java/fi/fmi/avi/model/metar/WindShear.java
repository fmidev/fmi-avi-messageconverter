package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.RunwayDirection;

public interface WindShear extends AviationCodeListUser {

    boolean isAppliedToAllRunways();

    Optional<List<RunwayDirection>> getRunwayDirections();



}
