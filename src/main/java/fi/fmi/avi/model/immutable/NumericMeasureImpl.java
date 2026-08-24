package fi.fmi.avi.model.immutable;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fi.fmi.avi.model.NumericMeasure;

/**
 * Created by rinne on 17/04/2018.
 *
 * <p>Migrated from FreeBuilder to <a href="https://immutables.github.io/">Immutables</a>
 * (org.immutables:value) as a proof-of-concept for the modernization effort described in
 * docs/07-modernization-plan.md. Almost all of the public shape of this class (interfaces
 * implemented, {@code builder()}/{@code of(...)}/{@code immutableCopyOf(...)} static methods, the
 * nested {@code Builder} type and its setter names) is unchanged from the FreeBuilder version, so
 * almost no caller in this repo, fmi-avi-messageconverter-tac, or fmi-avi-messageconverter-iwxxm
 * needs to change. Only the generated-code mechanism underneath changed: {@code @Value.Immutable}
 * makes Immutables generate the sibling class {@code ImmutableNumericMeasureImpl} (a concrete
 * subclass of this abstract class, mirroring how {@code NumericMeasureImpl_Builder} used to work),
 * and {@code @Value.Style(init = "set*")} tells it to name the generated builder setters
 * {@code setValue}/{@code setUom} rather than its own default bean-style {@code value}/{@code uom},
 * matching the FreeBuilder-era convention this codebase (and its Jackson builder-deserialization
 * config below) already expects.
 *
 * <p>The one real, mechanical API change: the nested {@code Builder}'s bulk-copy static factory is
 * named {@code copyOf(NumericMeasure)} here, not {@code from(NumericMeasure)} as it was under
 * FreeBuilder. Immutables auto-generates its own <em>instance</em>-level {@code from(NumericMeasure)}
 * method on the builder whenever the annotated class implements an interface exposing matching
 * accessors (which {@code NumericMeasure} does) - a static method of the same name and parameter
 * type cannot coexist with it ("static method cannot override instance method"). Because of this,
 * every FreeBuilder-era hand-written {@code Builder.from(...)} static factory in this codebase was
 * renamed to {@code Builder.copyOf(...)} - declarations and call sites, main and test sources, in
 * one repo-wide sweep - before migrating classes individually, rather than doing the rename
 * class-by-class as each one's turn came up.
 */
@Value.Immutable
@Value.Style(init = "set*")
@JsonDeserialize(builder = NumericMeasureImpl.Builder.class)
@JsonInclude(JsonInclude.Include.ALWAYS)
//TODO @JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({ "value", "uom" })
public abstract class NumericMeasureImpl implements NumericMeasure, Serializable {

    private static final long serialVersionUID = 8955711992731295488L;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Note for the FreeBuilder-&gt;Immutables migration (see docs/07-modernization-plan.md):
     * FreeBuilder auto-implemented an {@code abstract Builder toBuilder()} declaration. Immutables
     * does not - declaring it {@code abstract} here would make Immutables treat it as an ordinary
     * generated property (compiles, but silently requires a manual {@code .setToBuilder(...)} call
     * and is otherwise broken). It must instead be a concrete method.
     */
    public Builder toBuilder() {
        return builder().setUom(getUom()).setValue(getValue());
    }

    public static NumericMeasureImpl of(final Integer value, final String uom) {
        return builder().setValue(value.doubleValue()).setUom(uom).build();
    }

    public static NumericMeasureImpl of(final Double value, final String uom) {
        return builder().setValue(value).setUom(uom).build();
    }

    public static NumericMeasureImpl immutableCopyOf(final NumericMeasure numericMeasure) {
        Objects.requireNonNull(numericMeasure);
        if (numericMeasure instanceof NumericMeasureImpl) {
            return (NumericMeasureImpl) numericMeasure;
        } else {
            return Builder.copyOf(numericMeasure).build();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static Optional<NumericMeasureImpl> immutableCopyOf(final Optional<NumericMeasure> numericMeasure) {
        return numericMeasure.map(NumericMeasureImpl::immutableCopyOf);
    }

    public static class Builder extends ImmutableNumericMeasureImpl.Builder {

        Builder() {
        }

        public static Builder copyOf(final NumericMeasure value) {
            if (value instanceof NumericMeasureImpl) {
                return ((NumericMeasureImpl) value).toBuilder();
            } else {
                return NumericMeasureImpl.builder()//
                        .setUom(value.getUom())//
                        .setValue(value.getValue());
            }
        }
    }
}
