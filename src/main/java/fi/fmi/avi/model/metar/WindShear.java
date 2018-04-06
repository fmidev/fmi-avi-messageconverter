package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.RunwayDirection;

@FreeBuilder
@JsonDeserialize(builder = WindShear.Builder.class)
public interface WindShear extends AviationCodeListUser {

    boolean isAppliedToAllRunways();

    Optional<List<RunwayDirection>> getRunwayDirections();

    Builder toBuilder();

    class Builder extends WindShear_Builder {
    }

}
