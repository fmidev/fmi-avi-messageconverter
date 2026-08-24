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
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Value.Immutable
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
            return Builder.copyOf(bulletin).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<SIGMETBulletinImpl> immutableCopyOf(final Optional<SIGMETBulletin> bulletin) {
        return bulletin.map(SIGMETBulletinImpl::immutableCopyOf);
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    @Override
    @JsonDeserialize(as = BulletinHeadingImpl.class)
    public abstract BulletinHeading getHeading();

    @Override
    @JsonProperty("messages")
    public abstract List<SIGMET> getMessages();

    public static class Builder extends ImmutableSIGMETBulletinImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final SIGMETBulletin value) {
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
    }
}
