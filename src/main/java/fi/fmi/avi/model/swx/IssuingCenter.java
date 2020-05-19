package fi.fmi.avi.model.swx;

import java.util.Optional;

public interface IssuingCenter {
    Optional<String> getName();

    Optional<String> getType();

    Optional<String> getInterpretation();
}
