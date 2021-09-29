package fi.fmi.avi.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.TacGeometryImpl;

@JsonDeserialize(builder = TacGeometryImpl.Builder.class)
public interface TacGeometry{
    String getData();
}
