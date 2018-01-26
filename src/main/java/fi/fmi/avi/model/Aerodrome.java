package fi.fmi.avi.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Container for the metadata for a single aerodrome.
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Aerodrome implements Serializable {
    private static final long serialVersionUID = 4530699187269337382L;

    private String designator = null;
    private String name = null;
    private String locationIndicatorICAO = null;
    private String designatorIATA = null;
    private Double fieldElevationValue = null;
    private String fieldElevationUnit = null;
    private GeoPosition referencePoint = null;

    public Aerodrome() {
    }

    public Aerodrome(final Aerodrome input) {
        this.designator = input.designator;
        this.name = input.name;
        this.locationIndicatorICAO = input.locationIndicatorICAO;
        this.designatorIATA = input.designatorIATA;
        this.fieldElevationValue = input.fieldElevationValue;
        this.fieldElevationUnit = input.fieldElevationUnit;
        if (input.referencePoint != null) {
            this.setReferencePoint(new GeoPosition(input.referencePoint.getCoordinateReferenceSystemId(), input.referencePoint.getCoordinates()));
        }
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

    @JsonIgnore
    public boolean isResolved() {
        return this.designator != null && this.name != null && this.referencePoint != null && this.fieldElevationValue != null;
    }

    public String toString() {
        return "Aerodrome info for '" + this.designator + "':" + "\n\tName: " + this.name + "\n\tLocationIndicatorICAO:" + this.locationIndicatorICAO
                + "\n\tDesignatorIATA:" + this.designatorIATA + "\n\tFieldElevation:" + this.fieldElevationValue + ' ' + this.fieldElevationUnit + "\n\tARP:"
                + this.referencePoint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((designator == null) ? 0 : designator.hashCode());
        result = prime * result + ((designatorIATA == null) ? 0 : designatorIATA.hashCode());
        result = prime * result + ((fieldElevationUnit == null) ? 0 : fieldElevationUnit.hashCode());
        result = prime * result + ((fieldElevationValue == null) ? 0 : fieldElevationValue.hashCode());
        result = prime * result + ((locationIndicatorICAO == null) ? 0 : locationIndicatorICAO.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((referencePoint == null) ? 0 : referencePoint.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Aerodrome other = (Aerodrome) obj;
        if (designator == null) {
            if (other.designator != null) {
                return false;
            }
        } else if (!designator.equals(other.designator)) {
            return false;
        }
        if (designatorIATA == null) {
            if (other.designatorIATA != null) {
                return false;
            }
        } else if (!designatorIATA.equals(other.designatorIATA)) {
            return false;
        }
        if (fieldElevationUnit == null) {
            if (other.fieldElevationUnit != null) {
                return false;
            }
        } else if (!fieldElevationUnit.equals(other.fieldElevationUnit)) {
            return false;
        }
        if (fieldElevationValue == null) {
            if (other.fieldElevationValue != null) {
                return false;
            }
        } else if (!fieldElevationValue.equals(other.fieldElevationValue)) {
            return false;
        }
        if (locationIndicatorICAO == null) {
            if (other.locationIndicatorICAO != null) {
                return false;
            }
        } else if (!locationIndicatorICAO.equals(other.locationIndicatorICAO)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (referencePoint == null) {
            if (other.referencePoint != null) {
                return false;
            }
        } else if (!referencePoint.equals(other.referencePoint)) {
            return false;
        }
        return true;
    }

}
