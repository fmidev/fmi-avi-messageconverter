package fi.fmi.avi.data;


public class GeoPosition {
	private double[] coordinates;
	private String coordinateReferenceSystemId;
	
	public GeoPosition() {
	}
	
	public GeoPosition(final String crsID, double...coordinates) {
		this.coordinateReferenceSystemId = crsID;
		this.coordinates = coordinates;
	}

	public String getCoordinateReferenceSystemId() {
		return coordinateReferenceSystemId;
	}

	public void setCoordinateReferenceSystemId(String coordinateReferenceSystemId) {
		this.coordinateReferenceSystemId = coordinateReferenceSystemId;
	}

	public double[] getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(double...coordinates) {
		this.coordinates = coordinates;
	}

	public String toString() {
		return new StringBuilder()
				.append(this.coordinates)
				.append('(')
				.append(this.coordinateReferenceSystemId)
				.append(')')
				.toString();
	}
}
