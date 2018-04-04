package fi.fmi.avi.model.taf;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.Aerodrome;
import fi.fmi.avi.model.PartialOrCompleteTimeInstance;
import fi.fmi.avi.model.PartialOrCompleteTimePeriod;

/**
 * Created by rinne on 22/11/17.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFReference.Builder.class)
public interface TAFReference {

    Aerodrome aerodrome();

    PartialOrCompleteTimeInstance issueTime();

    PartialOrCompleteTimePeriod validityTime();

    TAF.TAFStatus status();

    Builder toBuilder();

    class Builder extends TAFReference_Builder {
    }
}
