package fi.fmi.avi.model.swx;

import java.util.Optional;

import fi.fmi.avi.model.PartialOrCompleteTimeInstant;

public interface NextAdvisory {
    Type getTimeSpecifier();

    Optional<PartialOrCompleteTimeInstant> getTime();

    enum Type {
        NO_FURTHER_ADVISORIES, NEXT_ADVISORY_AT, NEXT_ADVISORY_BY

    }
}
