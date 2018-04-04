package fi.fmi.avi.model.metar;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.AviationCodeListUser;
import fi.fmi.avi.model.CloudLayer;
import fi.fmi.avi.model.NumericMeasure;

@FreeBuilder
@JsonDeserialize(builder = ObservedClouds.Builder.class)
public interface ObservedClouds extends AviationCodeListUser {

    boolean amountAndHeightUnobservableByAutoSystem();

    boolean noSignificantCloud();

    Optional<NumericMeasure> verticalVisibility();

    Optional<List<CloudLayer>> layers();

    Builder toBuilder();

    class Builder extends ObservedClouds_Builder {
    }

}
