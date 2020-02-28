package fi.fmi.avi.model.SWX;

import java.util.List;

import fi.fmi.avi.model.Geometry;

public interface SWXGeometry extends Geometry {
    List<String> getLatitudeRegions();
    int getEasternLatitudeBand();
    int getWesternLatitudeBand();
}
