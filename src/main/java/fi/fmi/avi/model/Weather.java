package fi.fmi.avi.model;

import java.util.Optional;

public interface Weather {

    String getCode();

    Optional<String> getDescription();
}
