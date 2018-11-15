package fi.fmi.avi.model.sigmet.immutable;

import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.sigmet.SIGMETBulletin;

@FreeBuilder
@JsonDeserialize(builder = SIGMETBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "heading", "messages" })
public abstract class SIGMETBulletinImpl implements SIGMETBulletin {

    public static SIGMETBulletinImpl immutableCopyOf(final SIGMETBulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof SIGMETBulletinImpl) {
            return (SIGMETBulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    public static Optional<SIGMETBulletinImpl> immutableCopyOf(final Optional<SIGMETBulletin> bulletin) {
        return bulletin.map(SIGMETBulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends SIGMETBulletinImpl_Builder {

        public static Builder from(final SIGMETBulletin value) {
            if (value instanceof SIGMETBulletinImpl) {
                return ((SIGMETBulletinImpl) value).toBuilder();
            } else {
                return new SIGMETBulletinImpl.Builder()//
                        .setIssueTime(value.getIssueTime())//
                        .setHeading(SIGMETBulletinHeadingImpl.immutableCopyOf(value.getHeading()))//
                        .addAllMessages(value.getMessages());
            }
        }
    }
}
