# FreeBuilder → Immutables migration notes

The model classes in this repo used to be generated with
[FreeBuilder](https://freebuilder.inferred.org/). FreeBuilder is unmaintained (last release 2.3.0,
2019) and, on this repo's current toolchain, its implicit classpath-based annotation-processor
discovery silently stopped running at all (a build with FreeBuilder on the compile classpath but no
explicit `-processorpath`/`annotationProcessorPaths` entry generates zero sources - no warning, no
error, just "cannot find symbol" for the generated builder classes). The model has since been fully
migrated to [Immutables](https://immutables.github.io/) (`org.immutables:value`), which generates
the same shape of artifact - an immutable value class plus a fluent builder - from an annotated
abstract class. Migration is complete: no `@FreeBuilder` annotation remains anywhere in this repo,
and the `org.inferred:freebuilder` dependency has been removed from `pom.xml` entirely.

This note captures the non-obvious parts of that migration that still shape the code today, so a
reader doesn't have to reverse-engineer them from the diff.

## The migration recipe

Given a FreeBuilder class shaped like this codebase's convention -

```java
@FreeBuilder
@JsonDeserialize(builder = FooImpl.Builder.class)
public abstract class FooImpl implements Foo, Serializable {
    public static Builder builder() { return new Builder(); }
    public abstract Builder toBuilder();
    public static class Builder extends FooImpl_Builder {
        Builder() {}
        public static Builder from(final Foo value) { ... }
    }
}
```

the mechanical steps are:

1. Replace `@FreeBuilder` with `@Value.Immutable` **and** `@Value.Style(init = "set*")`. The style
   annotation is not optional: Immutables' own default setter-naming convention is the bare property
   name (`.value(...)`, `.uom(...)`), not `setXxx(...)`; `init = "set*"` is what makes the generated
   builder match this codebase's FreeBuilder-era `setXxx(...)` convention (and therefore what keeps
   the existing `@JsonDeserialize(builder = FooImpl.Builder.class)` Jackson config working
   unchanged). Most packages set this once, package-wide, via a `@Value.Style(...)` annotation on
   `package-info.java`, rather than repeating it per class.
2. Change the nested `Builder`'s supertype from `FooImpl_Builder` to `ImmutableFooImpl.Builder`
   (Immutables' default generated name for a `@Value.Immutable`-annotated class named `FooImpl` is
   `ImmutableFooImpl` - prepending `Immutable`, not replacing the `Impl` suffix). **This generated
   `Builder` class is not `final` and can be subclassed** - for the majority of classes in this
   codebase this is all that's needed: `public static class Builder extends ImmutableFooImpl.Builder { ... }`,
   keeping all hand-written convenience methods (`copyOf`, `immutableCopyOf`, etc.) as overrides or
   additions on top. See [the "detached builder" pattern](#when-extends-immutablexbuilder-isnt-enough-the-detached-builder-pattern)
   below for the classes where this wasn't sufficient.
3. **`public abstract Builder toBuilder();` must become a concrete method.** This is one of the most
   important gotchas found, and it fails silently in a way worth calling out explicitly: unlike
   FreeBuilder, Immutables does **not** recognize an abstract no-arg method returning the builder
   type as a "generate a reverse-builder" instruction. It treats it as an ordinary attribute
   declaration instead - it compiles, and generates a real (broken) `toBuilder` field requiring a
   `.setToBuilder(...)` call, throwing `IllegalStateException: ... required attributes are not set
   [toBuilder]` at runtime the first time `build()` is called without one. The fix is to write it by
   hand instead: `public Builder toBuilder() { return builder().setX(getX()).setY(getY())...; }` (or,
   for classes without extra hand-rolled `Builder.from(...)`/`copyOf(...)` logic, the generated
   builder already has an instance-level bulk-copy `from(<AnnotatedType> instance)` method you can
   delegate to instead of listing every field).
4. **Check for a `from`-name collision before keeping a static `Builder.from(Foo value)` factory.**
   Immutables auto-generates an *instance* method `from(<T> instance)` on the builder for the
   annotated type itself, and *also* for any interface it implements whose accessor methods match
   (e.g. `NumericMeasureImpl implements NumericMeasure`, so Immutables also generates
   `from(NumericMeasure instance)`). If the hand-written companion class already declares a
   **static** method with that exact name and parameter type (as this codebase's FreeBuilder-era
   convention does, e.g. `Builder.from(final NumericMeasure value)`), the build fails with
   `from(...) cannot override from(...): overriding method is static` - a real compile error, not a
   hypothetical. The fix applied throughout this repo: **every hand-written static factory of this
   shape was renamed from `from` to `copyOf`** (`Builder.copyOf(value)`), freeing up `from` for
   Immutables' own generated instance method (used internally by `toBuilder()` implementations).
   Grepping the whole repo for `Impl.Builder.from(` should now return nothing outside of Immutables'
   own generated sources.
5. Everything else - `builder()`, `of(...)`, `immutableCopyOf(...)`, `Optional` handling, Jackson
   annotations, `Serializable`/`serialVersionUID` - is untouched hand-written code; Immutables only
   generates the implementation of the abstract accessor methods and the builder.

## When `extends ImmutableX.Builder` isn't enough: the "detached builder" pattern

A recurring, load-bearing pattern throughout this migration, referenced by name from every file that
uses it: **when a class's builder needs cross-field validation, mid-construction reads of its own
not-yet-built state, or has to satisfy an externally-defined generic builder interface that declares
builder-side getters** - none of which Immutables supports, because **Immutables generates no
builder-side getters at all, ever**, and its generated setter/build methods are `final` and cannot be
overridden to add such logic - the fix is to stop extending the generated `ImmutableX.Builder` at
all. Instead:

1. Write `Builder` as a **completely standalone class**: plain hand-written fields (one per
   property, typically `protected` so a shared abstract base can be extracted), and hand-written
   `get`/`set`/`clear`/`mutate`/`merge` methods implementing whatever interface contract the class
   needs (an externally-defined generic builder interface, or just the ad hoc shape other code
   expects).
2. Only inside `build()` does the detached builder ever touch Immutables: it assembles the final
   immutable value by calling `ImmutableX.internalBuilder()` - **`internalBuilder`, not the default
   `builder`**, because the class also declares its own hand-written static `builder()` factory
   method returning the *detached* `Builder`, and that name would otherwise collide with Immutables'
   own generated static `ImmutableX.builder()` factory. The rename is done via a class-level style
   override: `@Value.Style(typeInnerBuilder = "InternalImmutableBuilder", builder = "internalBuilder")`
   (`typeInnerBuilder` renames the generated nested `Builder` class the same way, since a detached
   builder already declares its own `Builder` and the two would otherwise collide too).
3. Because the detached `Builder` has real fields, it can freely implement `getX()` methods,
   `clearX()` methods (see the corrected finding below), `mutateX(Consumer<T>)` methods, and any
   cross-field validation `build()` needs, none of which a plain `extends ImmutableX.Builder` class
   could do.

Classes using this pattern in this repo: `TAFImpl`, `METARImpl`, `SPECIImpl` (via the shared
`AbstractMeteorologicalTerminalAirReportBuilderImpl<T, B>` base class both extend),
`TAFBaseForecastImpl`/`TAFChangeForecastImpl` (via the shared `AbstractTAFForecastBuilderImpl<T, B>`
base class both extend), `PartialOrCompleteTimeInstant`, `PartialOrCompleteTimePeriod`,
`SpaceWeatherAdvisoryAmd79Impl`, `SpaceWeatherAdvisoryAmd82Impl`, `GTSMeteorologicalMessage`,
`GTSExchangeFileTemplate`, `TAFReferenceImpl` - plus, in `src/test`, the small test-only fixture
types `SWXAmd79Tests.AnalysisBuilderSpec`, `SWXAmd82Tests.AnalysisBuilderSpec`, and
`AbstractTAFForecast_Builder_MergeFromTest.TestTAFForecast` (this last one needs no fields or logic
of its own at all - it just extends the shared `AbstractTAFForecastBuilderImpl<T, B>` base directly
and implements `build()`, since every property it needs is already covered by that shared base).
Plain, non-detached `class Builder extends ImmutableX.Builder` classes (the majority of this
codebase) do **not** need the `@Value.Style(typeInnerBuilder = ..., builder = ...)` override - it is
specifically a detached-builder concern, needed only to avoid the `builder()`/`Builder` name
collision described in step 2 above.

**Corrected finding: `clearX()` does *not* exist by default on a plain `extends ImmutableX.Builder`
class, for any property type.** An earlier working assumption during this migration held that
Immutables generates a `clearX()` method for `Optional`-typed scalar properties specifically; this
was empirically disproven via `javap` on a compiled generated builder class (e.g.
`ImmutableNextAdvisoryImpl$Builder.class`), which showed **no `clearX()` method at all** - only
`setX(T)` and the `Optional`-argument overload `setX(Optional<? extends T>)`. The correct
replacement for a removed `.clearX()` call on a **non-detached** builder is always
`.setX(Optional.empty())`, never a `clearX()` call. `clearX()` calls are only ever valid against one
of the **detached** builder classes listed above, where it is a real, hand-written method - check
which kind of builder a class has (search its `Builder` for `extends ImmutableX.Builder` vs. a
plain, field-based class declaration) before assuming either way.

## The nested `ParseResult` naming collision

`GTSDataExchangeTranscoder` and `GTSExchangeFileTemplate` each declare their own nested
`ParseResult` abstract value class. Left at Immutables' defaults, both would generate a top-level
`ImmutableParseResult` class in the same package (`fi.fmi.avi.util`), colliding with each other.
Fixed with a per-class `@Value.Style(typeImmutable = "Immutable<Outer><Inner>")` override - e.g.
`ImmutableGTSDataExchangeTranscoderParseResult` and `ImmutableGTSExchangeFileTemplateParseResult` -
giving each generated class a distinct, outer-class-qualified name.

## Jackson builder-deserialization gotchas

These were not caught by `test-compile` or by object-graph-only unit tests - only by tests that
actually serialize an object to JSON and deserialize it back (`mvn test`, not just
`mvn test-compile`). Each is a genuine behavioral difference from FreeBuilder-era Jackson binding,
not a compile-time concern:

* **`@JsonDeserialize(as = XImpl.class)` on an abstract getter of type `Optional<X>`** (scalar, not a
  collection) silently breaks deserialization on a **non-detached** builder - Jackson's Jdk8Module
  raises `Failed to narrow type ... with annotation ...: Class XImpl not subtype of Optional<X>`.
  The fix is always `contentAs`, not `as`, for a scalar `Optional<X>`-typed property:
  `@JsonDeserialize(contentAs = XImpl.class)`. (`as` remains correct for a property whose declared
  type is already the plain, non-Optional `X`.)
* **`@JsonDeserialize(contentAs = XImpl.class)` on an abstract getter of type `Optional<List<X>>`**
  (a collection *nested inside* `Optional`) is *also* broken on a non-detached builder, in the
  opposite direction: Immutables' `passAnnotations` style copies the hint only onto the generated
  `setX(Optional<? extends List<X>>)` overload (the plain-`List<X>` overload is left unannotated and
  Jackson never binds to it), and Jackson then misapplies `contentAs` to the *first* container level
  it finds - i.e. it tries to narrow the whole `List<X>` down to `XImpl`, not `X`'s own list
  elements - producing `Class XImpl not subtype of List<X>`. There is no single-annotation,
  property-level fix for this doubly-nested shape. **The fix that works**: give the *element*
  interface `X` itself a class-level `@JsonDeserialize(as = XImpl.class)` hint (removing the
  now-redundant/broken property-level `contentAs`), so Jackson resolves `X → XImpl` correctly no
  matter how deeply `X` is nested (`List<X>`, `Optional<X>`, `Optional<List<X>>`, ...). This turned
  out to be needed even for **required** (non-`Optional`) `List<X>` properties on a non-detached
  builder too (e.g. `SIGMETBulletin`/`AIRMETBulletin`/`TAFBulletin`/`GenericMeteorologicalBulletin`'s
  `getMessages()`, and `SpaceWeatherAdvisoryAnalysis`'s `getRegions()`/`getIntensityAndRegions()`) -
  Jackson does not reliably pick the annotated `addAllX(List<X>)` override over the unannotated,
  Immutables-generated `setX(Iterable<? extends X>)` for builder-style deserialization, so the same
  class-level-hint fix was applied there too. Interfaces that now carry this hint:
  `PhenomenonGeometry`, `PhenomenonGeometryWithHeight`, `ObservedCloudLayer`, `Weather`, `CloudLayer`,
  `RunwayDirection`, `SpaceWeatherRegion` (both the `amd79` and `amd82` variants - distinct
  interfaces in distinct packages), `SpaceWeatherIntensityAndRegion`, `TAF`, `SIGMET`, `AIRMET`,
  `SpaceWeatherAdvisoryAmd79`, `SpaceWeatherAdvisoryAmd82`, and `GenericAviationWeatherMessage`. This
  does put a compile-time dependency from the plain `fi.fmi.avi.model` interface onto its own
  `fi.fmi.avi.model.immutable` implementation class (a precedent already set by
  `TacOrGeoGeometry`/`TacOrGeoGeometryImpl` before this migration) - an acceptable, narrow exception
  to interfaces otherwise staying implementation-agnostic, in exchange for deserialization actually
  working.
* **A hand-written field on a *detached* builder silently wins over its own annotated setter for
  Jackson deserialization, regardless of the field's visibility (`private` included).** Jackson's
  builder-style introspection detects same-named fields and setter methods as two accessors of one
  logical property and, empirically, prefers the field - so a `@JsonDeserialize(as = XImpl.class)`
  hint placed only on the setter (the correct place for a *non*-detached builder, where there is no
  competing field) is silently never applied when the class is a *detached* builder with a real
  field of the same name. Symptom: `InvalidDefinitionException: Cannot construct instance of
  <interface> (no Creators...)`, exactly as if no hint existed at all. The fix, applied to every
  detached-builder field whose declared type is an interface needing polymorphic resolution:
  `@JsonIgnore` **on the field** (to remove it from Jackson's candidate accessors entirely) plus
  `@JsonProperty("x")` **on the setter** (a plain `@JsonIgnore` on one accessor of a merged property
  otherwise suppresses the *whole* property unless another accessor explicitly re-declares
  `@JsonProperty`). Fields not needing this (primitives, enums, concrete non-polymorphic types) were
  left untouched. Affected: `TAFImpl` (`aerodrome`, `baseForecast`, `changeForecasts`),
  `TAFReferenceImpl` (`aerodrome`), `AbstractMeteorologicalTerminalAirReportBuilderImpl` (shared by
  `METARImpl`/`SPECIImpl`: `aerodrome`, `airTemperature`, `dewpointTemperature`,
  `altimeterSettingQNH`, `surfaceWind`, `visibility`, `runwayVisualRanges`, `presentWeather`,
  `clouds`, `recentWeather`, `windShear`, `seaState`, `runwayStates`, `trends`),
  `AbstractTAFForecastBuilderImpl` (shared by `TAFBaseForecastImpl`/`TAFChangeForecastImpl`:
  `prevailingVisibility`, `surfaceWind`, `forecastWeather`, `cloud`), `SpaceWeatherAdvisoryAmd79Impl`
  (`issuingCenter`, `advisoryNumber`, `replaceAdvisoryNumber`, `analyses`, `nextAdvisory`),
  `SpaceWeatherAdvisoryAmd82Impl` (the same five, `replaceAdvisoryNumbers` instead of the singular).
* A **duplicated, explicit `@JsonDeserialize`/`@JsonProperty` annotation placed on *both* the scalar
  and the `Optional`-argument overload of a hand-written detached-builder setter** (as opposed to
  the auto-propagated case above) produces a different, louder failure:
  `InvalidDefinitionException: Conflicting setter definitions for property ...`. Found once, in
  `SpaceWeatherAdvisoryAmd79Impl.setReplaceAdvisoryNumber` - fixed by keeping the annotation on only
  the scalar overload (matching every other property in this codebase), never both.

## Runtime behavior gap intentionally not carried over

FreeBuilder's `buildPartial()` (build without required-field validation, for constructing partial
test fixtures) has no Immutables equivalent and was not reimplemented. Every test call site that
used it was rewritten to call `.build()` after explicitly setting every required (non-`Optional`,
no-constructor-default) property on that builder - using the corresponding `src/main` class's
interface to determine which properties are actually required. This is a deliberate, accepted API
change, not an oversight.
