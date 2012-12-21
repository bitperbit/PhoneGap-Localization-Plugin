package ch.hsr.sa.lbn.service;

public class GeoJSONGeometry {
	private final double latitude;
	private final double longitude;

	public GeoJSONGeometry(final double latitude, final double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String toString(){
		final StringBuilder str = new StringBuilder();
		str.append("\"geometry\":{");
		str.append("\"type\":\"Point\",");
		str.append("\"coordinates\":[");
		str.append(longitude);
		str.append(",");
		str.append(latitude);
		str.append("]}");
		return str.toString();
	}
}
