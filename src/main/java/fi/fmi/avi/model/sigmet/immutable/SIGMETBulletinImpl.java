package fi.fmi.avi.model.sigmet.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.BulletinHeading;
import fi.fmi.avi.model.immutable.BulletinHeadingImpl;
import fi.fmi.avi.model.sigmet.SIGMET;
import fi.fmi.avi.model.sigmet.SIGMETBulletin;

@FreeBuilder
@JsonDeserialize(builder = SIGMETBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "timeStamp", "timeStampFields", "heading", "messages" })
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

        @Deprecated
        public Builder() {
        }

        public static Builder from(final SIGMETBulletin value) {
            if (value instanceof SIGMETBulletinImpl) {
                return ((SIGMETBulletinImpl) value).toBuilder();
            } else {
                return SIGMETBulletinImpl.builder()//
                        .setHeading(BulletinHeadingImpl.immutableCopyOf(value.getHeading()))//
                        .setTimeStamp(value.getTimeStamp())//
                        .addAllMessages(value.getMessages())//
                        .addAllTimeStampFields(value.getTimeStampFields());
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
