package fi.fmi.avi.model;

import java.util.Optional;

public interface VolcanoDescription {
    Optional<String> getVolcanoName();

    Optional<ElevatedPoint> getVolcanoPosition();
}
