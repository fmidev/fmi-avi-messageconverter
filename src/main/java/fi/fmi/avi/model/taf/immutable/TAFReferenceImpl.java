package fi.fmi.avi.model.taf.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.immutable.AerodromeImpl;
import fi.fmi.avi.model.taf.TAFReference;

/**
 * Created by rinne on 18/04/2018.
 */
@FreeBuilder
@JsonDeserialize(builder = TAFReferenceImpl.Builder.class)
public abstract class TAFReferenceImpl implements TAFReference, Serializable {

    public static TAFReferenceImpl immutableCopyOf(final TAFReference tafReference) {
        checkNotNull(tafReference);
        if (tafReference instanceof TAFReferenceImpl) {
            return (TAFReferenceImpl) tafReference;
        } else {
            return Builder.from(tafReference).build();
        }
    }

    public static Optional<TAFReferenceImpl> immutableCopyOf(final Optional<TAFReference> tafReference) {
        checkNotNull(tafReference);
        return tafReference.map(TAFReferenceImpl::immutableCopyOf);
    }

    abstract Builder toBuilder();

    public static class Builder extends TAFReferenceImpl_Builder {

        public static Builder from(final TAFReference value) {
            return new Builder().setAerodrome(AerodromeImpl.immutableCopyOf(value.getAerodrome()))
                    .setStatus(value.getStatus())
                    .setIssueTime(value.getIssueTime())
                    .setValidityTime(value.getValidityTime());
        }
    }
}
