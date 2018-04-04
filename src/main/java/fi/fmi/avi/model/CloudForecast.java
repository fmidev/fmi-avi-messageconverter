package fi.fmi.avi.model;

import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface CloudForecast extends AviationCodeListUser {

    boolean noSignificantCloud();

    Optional<NumericMeasure> verticalVisibility();

    Optional<List<CloudLayer>> layers();

    Builder toBuilder();

    class Builder extends CloudForecast_Builder {
        public Builder() {
            noSignificantCloud(false);
        }
    }
}
