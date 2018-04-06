package fi.fmi.avi.model;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface CloudForecast extends AviationCodeListUser {

    boolean isNoSignificantCloud();

    Optional<NumericMeasure> getVerticalVisibility();

    Optional<List<CloudLayer>> getLayers();

    Builder toBuilder();

    class Builder extends CloudForecast_Builder {
        public Builder() {
            setNoSignificantCloud(false);
        }
    }
}
