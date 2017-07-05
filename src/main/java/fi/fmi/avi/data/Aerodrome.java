package fi.fmi.avi.data;

/**
 * Container for the metadata for a single aerodrome.
 */
public class Aerodrome {
    private String designator;
    private String name;
    private String locationIndicatorICAO;
    private String designatorIATA;
    private Double fieldElevationValue;
    private String fieldElevationUnit;
    private GeoPosition referencePoint;

    public Aerodrome() {
    }
    
    public Aerodrome(final String designator) {
        this.designator = designator;
    }

    public String getDesignator() {
        return designator;
    }

    public void setDesignator(final String designator) {
        this.designator = designator;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getLocationIndicatorICAO() {
        return locationIndicatorICAO;
    }

    public void setLocationIndicatorICAO(final String locationIndicatorICAO) {
        this.locationIndicatorICAO = locationIndicatorICAO;
    }

    public String getDesignatorIATA() {
        return designatorIATA;
    }

    public void setDesignatorIATA(final String designatorIATA) {
        this.designatorIATA = designatorIATA;
    }

    public Double getFieldElevationValue() {
        return fieldElevationValue;
    }

    public void setFieldElevation(final Double fieldElevation) {
        this.fieldElevationValue = fieldElevation;
    }

    public GeoPosition getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(final GeoPosition point) {
        this.referencePoint = point;
    }
    
    public boolean isResolved() {
    	return this.designator != null && this.name != null && this.referencePoint != null && this.fieldElevationValue != null;
    }

    public String toString() {
        return new StringBuilder().append("Aerodrome info for '")
                .append(this.designator)
                .append("':")
                .append("\n\tName: ")
                .append(this.name)
                .append("\n\tLocationIndicatorICAO:")
                .append(this.locationIndicatorICAO)
                .append("\n\tDesignatorIATA:")
                .append(this.designatorIATA)
                .append("\n\tFieldElevation:")
                .append(this.fieldElevationValue)
                .append(' ')
                .append(this.fieldElevationUnit)
                .append("\n\tARP:")
                .append(this.referencePoint)
                .toString();
    }
}
