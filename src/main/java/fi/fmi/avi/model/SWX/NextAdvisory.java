package fi.fmi.avi.model.SWX;

import java.util.Optional;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

public interface NextAdvisory {
    Optional<String> noFurtherAdvisory();
    Optional<PartialOrCompleteTimeInstant> nextAdvisory();
}
