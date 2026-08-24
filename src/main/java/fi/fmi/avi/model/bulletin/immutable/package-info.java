/**
 * Package-wide Immutables style: generated builder setters are named {@code setXxx(...)} (matching
 * this codebase's pre-existing FreeBuilder-era convention, and what the {@code @JsonDeserialize
 * (builder = ...)} Jackson config on the classes in this package already expects), rather than
 * Immutables' own default bare-property-name convention. See docs/07-modernization-plan.md.
 */
@org.immutables.value.Value.Style(init = "set*", get = {"is*", "get*"}, passAnnotations = {com.fasterxml.jackson.databind.annotation.JsonDeserialize.class, com.fasterxml.jackson.annotation.JsonProperty.class})
package fi.fmi.avi.model.bulletin.immutable;
