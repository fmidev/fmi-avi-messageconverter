package fi.fmi.avi.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ConversionChainBuilder<S> {
    private final AviMessageConverter converter;
    private final List<AviMessageSpecificConverter> converters = new ArrayList<>();
    private final List<ConversionSpecification> specifications = new ArrayList<>();
    private final ConversionResult.Status requiredStatus;

    public ConversionChainBuilder(final AviMessageConverter converter, final ConversionSpecification<S, ?> initialStep,
            final ConversionResult.Status requiredStatus) {
        this.converter = converter;
        this.requiredStatus = requiredStatus;
        this.withConversionStep(initialStep);
    }

    public ConversionChainBuilder(final AviMessageConverter converter, final ConversionSpecification<S, ?> initialStep) {
        this(converter, initialStep, ConversionResult.Status.SUCCESS);
    }

    public ConversionChainBuilder<S> withConversionStep(final ConversionSpecification<?, ?> step) {
        if (this.converter.isSpecificationSupported(step)) {
            this.specifications.add(step);
            this.converters.add(this.converter.getConverter(step));
        } else {
            throw new IllegalArgumentException("Conversion " + step + " not supported, please check converter configuration");
        }
        return this;
    }

    public <U, T> ConversionChainBuilder<S> withMutator(final Function<U, T> mutator, final Class<U> inputClz, final Class<T> outputClz) {
        this.converters.add((AviMessageSpecificConverter<U, T>) (input, hints) -> {
            final ConversionResult<T> result = new ConversionResult<>();
            try {
                result.setConvertedMessage(mutator.apply(input));
            } catch (final Exception e) {
                result.addIssue(new ConversionIssue(ConversionIssue.Severity.ERROR, ConversionIssue.Type.OTHER, "Exception in applying mutator function", e));
            }
            return result;
        });
        this.specifications.add(new ConversionSpecification<>(inputClz, outputClz));
        return this;
    }

    public <T> AviMessageSpecificConverter<S, T> build(final ConversionSpecification<?, T> finalStep) {
        this.withConversionStep(finalStep);
        return (input, hints) -> {
            final ConversionResult<T> retval = new ConversionResult<>();
            final AviMessageSpecificConverter initial = converters.get(0);
            ConversionResult result = initial.convertMessage(input, hints);
            for (int i = 1; i < converters.size(); i++) {
                retval.addIssue(result.getConversionIssues());
                if (ConversionResult.Status.isMoreCritical(result.getStatus(), requiredStatus)) {
                    result.setConvertedMessage(null);
                    break;
                }
                if (!result.getConvertedMessage().isPresent()) {
                    break;
                }
                final Object msg = result.getConvertedMessage().get();
                result = convertSingleMessage(msg, specifications.get(i), converters.get(i), hints);

            }
            if (result.getConvertedMessage().isPresent()) {
                retval.setConvertedMessage((T) result.getConvertedMessage().get());
            }
            return retval;
        };
    }

    private ConversionResult<?> convertSingleMessage(final Object msg, final ConversionSpecification spec, final AviMessageSpecificConverter converter,
            final ConversionHints hints) {
        ConversionResult<?> retval = new ConversionResult<>();
        if (!spec.getInputClass().isAssignableFrom(msg.getClass())) {
            retval.addIssue(new ConversionIssue(ConversionIssue.Severity.ERROR, ConversionIssue.Type.OTHER,
                    "Cannot use intermediate result of " + "type " + msg.getClass().getCanonicalName() + " as input for the conversion step " + spec));
            retval.setConvertedMessage(null);
        } else {
            retval = converter.convertMessage(msg, hints);
        }
        return retval;
    }
}
