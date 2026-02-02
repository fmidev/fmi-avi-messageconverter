package fi.fmi.avi.model.sigmet.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.bulletin.BulletinHeading;
import fi.fmi.avi.model.bulletin.MeteorologicalBulletinBuilderHelper;
import fi.fmi.avi.model.bulletin.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@FreeBuilder
@JsonDeserialize(builder = SIGMETBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"timeStamp", "timeStampFields", "heading", "collectIdentifier", "messages"})
public abstract class SIGMETBulletinImpl implements SIGMETBulletin, Serializable {

    private static final long serialVersionUID = 7742724278322130499L;

    public static Builder builder() {
        return new Builder();
    }

    public static SIGMETBulletinImpl immutableCopyOf(final SIGMETBulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof SIGMETBulletinImpl) {
            return (SIGMETBulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SIGMETBulletinImpl> immutableCopyOf(final Optional<SIGMETBulletin> bulletin) {
        return bulletin.map(SIGMETBulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SIGMETBulletinImpl_Builder {

        Builder() {
        }

        public static Builder from(final SIGMETBulletin value) {
            if (value instanceof SIGMETBulletinImpl) {
                return ((SIGMETBulletinImpl) value).toBuilder();
            } else {
                final Builder builder = builder();
                MeteorologicalBulletinBuilderHelper.copyFrom(builder, value, //
                        Builder::setHeading, //
                        Builder::addAllMessages, //
                        SIGMETImpl::immutableCopyOf, //
                        Builder::setTimeStamp, //
                        Builder::addAllTimeStampFields, //
                        Builder::setCollectIdentifier);
                return builder;
            }
        }

        @Override
        @JsonDeserialize(as = BulletinHeadingImpl.class)
        public Builder setHeading(final BulletinHeading heading) {
            return super.setHeading(heading);
        }

        @Override
        @JsonDeserialize(contentAs = SIGMETImpl.class)
        @JsonProperty("messages")
        public Builder addMessages(final SIGMET... messages) {
            return super.addMessages(messages);
        }
    }
}
