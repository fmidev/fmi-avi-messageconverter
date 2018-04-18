package fi.fmi.avi.model;

import java.util.Optional;

public interface CloudLayer extends AviationCodeListUser {

    CloudAmount getAmount();

    Optional<NumericMeasure> getBase();

    Optional<CloudType> getCloudType();

}
