package fi.fmi.avi.model.swx.amd79;

import java.util.Optional;

public interface IssuingCenter {

    Optional<String> getDesignator();

    Optional<String> getName();

    Optional<String> getType();

}
