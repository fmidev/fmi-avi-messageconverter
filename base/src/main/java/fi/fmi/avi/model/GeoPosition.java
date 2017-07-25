package fi.fmi.avi.model;

import java.util.Arrays;

public class GeoPosition {
	private Double[] coordinates;
	private String coordinateReferenceSystemId;
	private Double elevationValue = null;
	private String elevationUom = null;
	
	
	public GeoPosition() {
	}
	
	public GeoPosition(final String crsID, Double...coordinates) {
		this.coordinateReferenceSystemId = crsID;
		this.coordinates = coordinates;
	}

	public String getCoordinateReferenceSystemId() {
		return coordinateReferenceSystemId;
	}

	public void setCoordinateReferenceSystemId(String coordinateReferenceSystemId) {
		this.coordinateReferenceSystemId = coordinateReferenceSystemId;
	}

	public Double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Double...coordinates) {
		this.coordinates = coordinates;
	}

	
	public Double getElevationValue() {
    return elevationValue;
  }

  public void setElevationValue(Double elevationValue) {
    this.elevationValue = elevationValue;
  }

  public String getElevationUom() {
    return elevationUom;
  }

  public void setElevationUom(String elevationUom) {
    this.elevationUom = elevationUom;
  }

  public String toString() {
		return new StringBuilder()
				.append(this.coordinates)
				.append('(')
				.append(this.coordinateReferenceSystemId)
				.append(')')
				.toString();
	}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((coordinateReferenceSystemId == null) ? 0 : coordinateReferenceSystemId.hashCode());
    result = prime * result + Arrays.hashCode(coordinates);
    result = prime * result + ((elevationUom == null) ? 0 : elevationUom.hashCode());
    result = prime * result + ((elevationValue == null) ? 0 : elevationValue.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GeoPosition other = (GeoPosition) obj;
    if (coordinateReferenceSystemId == null) {
      if (other.coordinateReferenceSystemId != null)
        return false;
    } else if (!coordinateReferenceSystemId.equals(other.coordinateReferenceSystemId))
      return false;
    if (!Arrays.equals(coordinates, other.coordinates))
      return false;
    if (elevationUom == null) {
      if (other.elevationUom != null)
        return false;
    } else if (!elevationUom.equals(other.elevationUom))
      return false;
    if (elevationValue == null) {
      if (other.elevationValue != null)
        return false;
    } else if (!elevationValue.equals(other.elevationValue))
      return false;
    return true;
  }
}
