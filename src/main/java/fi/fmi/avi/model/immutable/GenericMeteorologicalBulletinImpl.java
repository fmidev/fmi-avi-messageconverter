package fi.fmi.avi.model.immutable;

import java.io.Serializable;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.GenericMeteorologicalBulletin;

@FreeBuilder
@JsonDeserialize(builder = SurfaceWindImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "heading", "messages" })
public abstract class GenericMeteorologicalBulletinImpl implements GenericMeteorologicalBulletin, Serializable {
    //TODO: FreeBuilder boilerplate
}
