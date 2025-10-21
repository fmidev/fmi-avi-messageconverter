package fi.fmi.avi.model.swx.amd82.immutable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fi.fmi.avi.model.swx.amd82.AdvisoryNumber;
import org.inferred.freebuilder.FreeBuilder;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@FreeBuilder
@JsonDeserialize(builder = AdvisoryNumberImpl.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"year", "serialNumber"})
public abstract class AdvisoryNumberImpl implements AdvisoryNumber, Serializable {

    private static final long serialVersionUID = -8532026521718086632L;

    public static Builder builder() {
        return new Builder();
    }

    public static AdvisoryNumberImpl immutableCopyOf(final AdvisoryNumber advisoryNumber) {
        Objects.requireNonNull(advisoryNumber);
        if (advisoryNumber instanceof AdvisoryNumberImpl) {
            return (AdvisoryNumberImpl) advisoryNumber;
        } else {
            return Builder.from(advisoryNumber).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<AdvisoryNumberImpl> immutableCopyOf(final Optional<AdvisoryNumber> advisoryNumber) {
        Objects.requireNonNull(advisoryNumber);
        return advisoryNumber.map(AdvisoryNumberImpl::immutableCopyOf);
    }

    @Override
    public String asAdvisoryNumber() {
        return getYear() + "/" + getSerialNumber();
    }

    public abstract Builder toBuilder();

    public static class Builder extends AdvisoryNumberImpl_Builder {
        private static final Pattern ADVISORY_NO_FORMAT = Pattern.compile("^(?<year>[0-9]{4})/(?<serialNo>[0-9]*)$");

        @Deprecated
        Builder() {
        }

        public static Builder from(final AdvisoryNumber value) {
            if (value instanceof AdvisoryNumberImpl) {
                return ((AdvisoryNumberImpl) value).toBuilder();
            } else {
                return builder()//
                        .setSerialNumber(value.getSerialNumber()).setYear(value.getYear());
            }
        }

        public static Builder from(final fi.fmi.avi.model.swx.amd79.AdvisoryNumber value) {
            return builder().setSerialNumber(value.getSerialNumber()).setYear(value.getYear());
        }

        /**
         * Parses AdvisoryNumber from a String matching format declared in ICAO Annex 3.
         * <pre><code>nnnn/[n][n][n]n</code></pre>
         * <p>
         * No strict checking is made, this method may accept more digits than specified.
         */
        public static Builder from(final String value) {
            final Matcher m = ADVISORY_NO_FORMAT.matcher(value);
            if (m.matches()) {
                return builder().setYear(Integer.parseInt(m.group("year"))).setSerialNumber(Integer.parseInt(m.group("serialNo")));
            } else {
                throw new IllegalArgumentException("Input does not match advisory number pattern " + ADVISORY_NO_FORMAT);
            }
        }
    }
}
