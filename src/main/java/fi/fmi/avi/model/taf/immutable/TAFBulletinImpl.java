package fi.fmi.avi.model.taf.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.taf.TAF;
import fi.fmi.avi.model.taf.TAFBulletin;
import fi.fmi.avi.model.taf.TAFBulletinHeading;

@FreeBuilder
@JsonDeserialize(builder = TAFBulletinImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "issueTime", "heading", "messages" })
public abstract class TAFBulletinImpl implements TAFBulletin, Serializable {

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

        @Override
        public TAFBulletinImpl build() {
            //NOTE: check disabled, it seems that the length of TAF is no longer relevant in bulletins / Ilkka Rinne 27th Nov 2018
            //check the all the TAFs are short or long consistently with the heading info
            /*
            Optional<PartialOrCompleteTimePeriod> validity;
            Optional<Duration> span;
            Duration twelweHours = Duration.ofHours(12);
            for (TAF taf : this.getMessages()) {
                validity = taf.getValidityTime();
                if (validity.isPresent()) {
                    span = validity.get().getValidityTimeSpan();
                    if (span.isPresent()) {
                        if (getHeading().isValidLessThan12Hours() == (span.get().compareTo(twelweHours) >= 0)) {
                            throw new IllegalStateException("TAF contained in bulletin has time span of " + span.get().toHours() + " hours which is "
                                    + "inconsistent with the heading isValidLessThan12Hours: " + getHeading().isValidLessThan12Hours());
                        }
                    }
                }
            }
            */
            return super.build();
        }

        @Override
        @JsonDeserialize(as = TAFBulletinHeadingImpl.class)
        public Builder setHeading(final TAFBulletinHeading heading) {
            return super.setHeading(heading);
        }

        @Override
        @JsonDeserialize(contentAs = TAFImpl.class)
        @JsonProperty("messages")
        public Builder addMessages(final TAF... messages) {
            return super.addMessages(messages);
        }
    }
}
