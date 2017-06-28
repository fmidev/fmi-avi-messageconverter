package fi.fmi.avi.parser;

import fi.fmi.avi.data.metar.METAR;
import fi.fmi.avi.data.taf.TAF;

/**
 * Specifies an aviation weather message conversion from one format to another.
 *
 * @param <S>
 *         the input message kind
 * @param <T>
 *         the output message kind
 *
 * @author Ilkka Rinne / Spatineo Oy 2017
 */
public class ConversionSpecification<S, T> {

    /**
     * Pre-configured spec for ICAO Annex 3 TAC format to {@link METAR} POJO.
     */
    public static final ConversionSpecification<String, METAR> TAC_TO_METAR_POJO = new ConversionSpecification<>(String.class, METAR.class, "ICAO Annex 3 TAC",
            null);

    /**
     * Pre-configured spec for {@link METAR} to ICAO Annex 3 TAC String.
     */
    public static final ConversionSpecification<METAR, String> METAR_POJO_TO_TAC = new ConversionSpecification<>(METAR.class, String.class, null,
            "ICAO Annex 3 TAC");

    /**
     * Pre-configured spec for ICAO Annex 3 TAC format to {@link TAF} POJO.
     */
    public static final ConversionSpecification<String, TAF> TAC_TO_TAF_POJO = new ConversionSpecification<>(String.class, TAF.class, "ICAO Annex 3 TAC", null);

    /**
     * Pre-configured spec for {@link TAF} to ICAO Annex 3 TAC String.
     */
    public static final ConversionSpecification<TAF, String> TAF_POJO_TO_TAC = new ConversionSpecification<>(TAF.class, String.class, null, "ICAO Annex 3 TAC");

    private Class<S> inputClass;
    private Class<T> outputClass;
    private Object inputSpecifier;
    private Object outputSpecifier;

    /**
     * Constructor for a conversion specified fully by the input and output Classes.
     *
     * @param input Java class of the input message type
     * @param output Java class of the output message type
     */
    public ConversionSpecification(final Class<S> input, final Class<T> output) {
        this(input, output, null, null);
    }

    /**
     * Constructor for a conversion specified by both the input and output Classes and the specifiers.
     *
     * @param input
     *         Java class of the input message type
     * @param output
     *         Java class of the output message type
     * @param inputSpecifier
     *         detailed specifier of the input message type
     * @param outputSpecifier
     *         detailed specifier of the output message type
     */
    public ConversionSpecification(final Class<S> input, final Class<T> output, final Object inputSpecifier, final Object outputSpecifier) {
        this.inputClass = input;
        this.outputClass = output;
        this.inputSpecifier = inputSpecifier;
        this.outputSpecifier = outputSpecifier;
    }

    /**
     * Returns the input message type.
     *
     * @return Java class of the input message type
     */
    public Class<S> getInputClass() {
        return inputClass;
    }

    /**
     * Returns the output message type.
     *
     * @return Java class of the output message type
     */
    public Class<T> getOutputClass() {
        return outputClass;
    }

    /**
     * Returns the input message specifier.
     *
     * @return detailed specifier of the input message type, or null if not given
     */
    public Object getInputSpecifier() {
        return inputSpecifier;
    }

    /**
     * Returns the output message specifier.
     *
     * @return detailed specifier of the output message type, or null if not given
     */
    public Object getOutputSpecifier() { return outputSpecifier; }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((inputClass == null) ? 0 : inputClass.hashCode());
        result = prime * result + ((outputClass == null) ? 0 : outputClass.hashCode());
        result = prime * result + ((inputSpecifier == null) ? 0 : inputSpecifier.hashCode());
        result = prime * result + ((outputSpecifier == null) ? 0 : outputSpecifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConversionSpecification other = (ConversionSpecification) obj;
        if (inputClass == null) {
            if (other.inputClass != null)
                return false;
        } else if (!inputClass.equals(other.inputClass))
            return false;
        if (outputClass == null) {
            if (other.outputClass != null)
                return false;
        } else if (!outputClass.equals(other.outputClass))
            return false;
        if (inputSpecifier == null) {
            if (other.inputSpecifier != null)
                return false;
        } else if (!inputSpecifier.equals(other.inputSpecifier))
            return false;
        if (outputSpecifier == null) {
            if (other.outputSpecifier != null) {
                return false;
            }
        } else if (!outputSpecifier.equals(other.outputSpecifier)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.inputClass != null) {
            sb.append(inputClass.getSimpleName());
        }
        if (this.inputSpecifier != null) {
            sb.append('(').append(inputSpecifier).append(')');
        }
        sb.append("->");
        if (this.outputClass != null) {
            sb.append(this.outputClass.getSimpleName());
        }
        if (this.outputSpecifier != null) {
            sb.append('(').append(outputSpecifier).append(')');
        }
        return sb.toString();

    }

}