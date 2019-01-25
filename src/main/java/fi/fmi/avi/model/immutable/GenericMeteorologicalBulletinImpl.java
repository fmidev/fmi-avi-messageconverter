package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.BulletinHeading;
import fi.fmi.avi.model.GenericAviationWeatherMessage;
import fi.fmi.avi.model.GenericMeteorologicalBulletin;

@FreeBuilder
@JsonDeserialize(builder = SurfaceWindImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "heading", "messages" })
public abstract class GenericMeteorologicalBulletinImpl implements GenericMeteorologicalBulletin, Serializable {

    public static GenericMeteorologicalBulletinImpl immutableCopyOf(final GenericMeteorologicalBulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof GenericMeteorologicalBulletinImpl) {
            return (GenericMeteorologicalBulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<GenericMeteorologicalBulletinImpl> immutableCopyOf(final Optional<GenericMeteorologicalBulletin> bulletin) {
        return bulletin.map(GenericMeteorologicalBulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends GenericMeteorologicalBulletinImpl_Builder {

        public static Builder from(final GenericMeteorologicalBulletin value) {
            if (value instanceof GenericMeteorologicalBulletinImpl) {
                return ((GenericMeteorologicalBulletinImpl) value).toBuilder();
            } else {
                return new GenericMeteorologicalBulletinImpl.Builder()//
                        .setIssueTime(value.getIssueTime())//
                        .setHeading(GenericBulletinHeadingImpl.immutableCopyOf(value.getHeading()))//
                        .addAllMessages(value.getMessages());
            }
        }

        @Override
        @JsonDeserialize(as = GenericBulletinHeadingImpl.class)
        public Builder setHeading(final BulletinHeading heading) {
            return super.setHeading(heading);
        }

        @Override
        @JsonDeserialize(contentAs = GenericAviationWeatherMessageImpl.class)
        @JsonProperty("messages")
        public Builder addMessages(final GenericAviationWeatherMessage... messages) {
            return super.addMessages(messages);
        }
    }
}
