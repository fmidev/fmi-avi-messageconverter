package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.MeteorologicalBulletinBuilderHelper;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.sigmet.AIRMET;
import fi.fmi.avi.model.sigmet.AIRMETBulletin;

@FreeBuilder
@JsonDeserialize(builder = AIRMETBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "timeStamp", "timeStampFields", "heading", "messages" })
public abstract class AIRMETBulletinImpl implements AIRMETBulletin, Serializable {

   private static final long serialVersionUID = 7742724278322130498L;

    public static Builder builder() {
        return new Builder();
    }

    public static AIRMETBulletinImpl immutableCopyOf(final AIRMETBulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof AIRMETBulletinImpl) {
            return (AIRMETBulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<AIRMETBulletinImpl> immutableCopyOf(final Optional<AIRMETBulletin> bulletin) {
        return bulletin.map(AIRMETBulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends AIRMETBulletinImpl_Builder {

        Builder() {
        }

        public static Builder from(final AIRMETBulletin value) {
            if (value instanceof AIRMETBulletinImpl) {
                return ((AIRMETBulletinImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                MeteorologicalBulletinBuilderHelper.copyFrom(builder, value, //
                        Builder::setHeading, //
                        Builder::addAllMessages, //
                        AIRMETImpl::immutableCopyOf, //
                        Builder::setTimeStamp, //
                        Builder::addAllTimeStampFields);
                return builder;
            }
        }

        @Override
        @JsonDeserialize(as = BulletinHeadingImpl.class)
        public Builder setHeading(final BulletinHeading heading) {
            return super.setHeading(heading);
        }

        @Override
        @JsonDeserialize(contentAs = AIRMETImpl.class)
        @JsonProperty("messages")
        public Builder addMessages(final AIRMET... messages) {
            return super.addMessages(messages);
        }
    }
}
