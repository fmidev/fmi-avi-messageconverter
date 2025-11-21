package fi.fmi.avi.model.swx.amd82;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

import java.util.Optional;

public interface NextAdvisory {
    Type getTimeSpecifier();

    Optional<PartialOrCompleteTimeInstant> getTime();

    enum Type {
        NO_FURTHER_ADVISORIES, NEXT_ADVISORY_AT, NEXT_ADVISORY_BY

    }
}
