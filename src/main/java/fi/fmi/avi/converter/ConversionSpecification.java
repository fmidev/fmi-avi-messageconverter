package fi.fmi.avi.converter;

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

    private final Class<S> inputClass;
    private final Class<T> outputClass;
    private final Object inputSpecifier;
    private final Object outputSpecifier;

    /**
     * Constructor for a conversion specified fully by the input and output Classes.
     *
     * @param input
     *         Java class of the input message type
     * @param output
     *         Java class of the output message type
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
    public Object getOutputSpecifier() {
        return outputSpecifier;
    }

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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConversionSpecification<?, ?> other = (ConversionSpecification<?, ?>) obj;
        if (inputClass == null) {
            if (other.inputClass != null) {
                return false;
            }
        } else if (!inputClass.equals(other.inputClass)) {
            return false;
        }
        if (outputClass == null) {
            if (other.outputClass != null) {
                return false;
            }
        } else if (!outputClass.equals(other.outputClass)) {
            return false;
        }
        if (inputSpecifier == null) {
            if (other.inputSpecifier != null) {
                return false;
            }
        } else if (!inputSpecifier.equals(other.inputSpecifier)) {
            return false;
        }
        if (outputSpecifier == null) {
            return other.outputSpecifier == null;
        } else {
            return outputSpecifier.equals(other.outputSpecifier);
        }
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
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