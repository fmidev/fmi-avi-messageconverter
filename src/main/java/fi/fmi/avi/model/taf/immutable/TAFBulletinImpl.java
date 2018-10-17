package fi.fmi.avi.model.taf.immutable;

import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.taf.TAFBulletin;

@FreeBuilder
@JsonDeserialize(builder = TAFBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "heading", "messages" })
public abstract class TAFBulletinImpl implements TAFBulletin {

    public static TAFBulletinImpl immutableCopyOf(final TAFBulletin bulletin) {
        Objects.requireNonNull(bulletin);
        if (bulletin instanceof TAFBulletinImpl) {
            return (TAFBulletinImpl) bulletin;
        } else {
            return Builder.from(bulletin).build();
        }
    }

    public static Optional<TAFBulletinImpl> immutableCopyOf(final Optional<TAFBulletin> bulletin) {
        return bulletin.map(TAFBulletinImpl::immutableCopyOf);
    }

    public abstract Builder toBuilder();

    public static class Builder extends TAFBulletinImpl_Builder {

        public static Builder from(final TAFBulletin value) {
            if (value instanceof TAFBulletinImpl) {
                return ((TAFBulletinImpl) value).toBuilder();
            } else {
                return new TAFBulletinImpl.Builder()//
                        .setIssueTime(value.getIssueTime())//
                        .setHeading(TAFBulletinHeadingImpl.immutableCopyOf(value.getHeading()))//
                        .addAllMessages(value.getMessages());
            }
        }
    }
}
