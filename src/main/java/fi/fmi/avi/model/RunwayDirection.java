package fi.fmi.avi.model;

import java.util.Optional;

public interface RunwayDirection {

    String getDesignator();

    Optional<Double> getTrueBearing();

    Optional<Double> getElevationTDZMeters();

    Optional<Aerodrome> getAssociatedAirportHeliport();

}