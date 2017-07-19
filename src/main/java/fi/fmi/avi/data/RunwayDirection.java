package fi.fmi.avi.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Container for a single runway direction within an aerodrome, as part of a runway.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class RunwayDirection {
    private String designator;
    private Double trueBearing;
    private Double elevationTDZMeters;
    private Aerodrome associatedAirportHeliport;

    public RunwayDirection() {
    }
    
    public RunwayDirection(final String designator) {
        this.designator = designator;
    }

    public String getDesignator() {
        return designator;
    }

    public void setDesignator(final String designator) {
        this.designator = designator;
    }

    public Double getTrueBearing() {
        return trueBearing;
    }

    public void setTrueBearing(final Double trueBearing) {
        this.trueBearing = trueBearing;
    }

    public Double getElevationTDZMeters() {
        return elevationTDZMeters;
    }

    public void setElevationTDZMeters(final Double elevationTDZMeters) {
        this.elevationTDZMeters = elevationTDZMeters;
    }

    public Aerodrome getAssociatedAirportHeliport() {
        return this.associatedAirportHeliport;
    }

    public void setAssociatedAirportHeliport(final Aerodrome airportHeliport) {
        this.associatedAirportHeliport = airportHeliport;
    }
    
    @JsonIgnore
    public boolean isResolved() {
    	return this.designator != null && this.associatedAirportHeliport != null && this.associatedAirportHeliport.isResolved() && this.trueBearing != null && this.elevationTDZMeters != null;
    }

}
