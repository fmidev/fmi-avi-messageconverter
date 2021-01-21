# Model Overview

The model is a set of data structure classes carrying the information being processed in the conversion.

## Design Principles

* Model the message content as is, not the information behind it.
  * Converter does not need to change any values by message rules, but use model values as is.
  * Thus, converter only needs to handle value representation.
* Model does not opine on correctness of the message or its content.
  * E.g. model may hold invalid values that rules of the message forbid.
* Model may declare properties mandatory or optional.
  * A property is declared mandatory when it is required to process message correctly.
  * A property is declared optional if it is optional in the message or message should be processable despite of a missing property.
  * Omitting an optional value in the model represents omitted value in the message.
  * An optional value may contain an empty or alike value (e.g. an empty collection), which represents an empty element or alike in the message.

## Model Implementation

The data model aims to create immutable objects where once the build method is called on an object it can no longer be changed.

* The model is built on interfaces. Only the get methods for each field are declared in the interface.
* Interfaces are implemented as abstract classes, which a are placed in a package named `immutable`. This package should be placed in the same place as the
  interface.
* [FreeBuilder](https://freebuilder.inferred.org/) is used in the model in order to quickly generate classes that conform to the builder pattern.
* The abstract class should be annotated with `@FreeBuilder` and should contain a static subclass called Builder that extends FreeBuilder implementation of the
  abstract class.
* The abstract classes are also annotated with [Jackson](https://github.com/FasterXML/jackson) JSON serialization instructions.
* `fi.fmi.avi.model.AviationWeatherMessage` interface is extended by the base object of each model in order to give them a baseline of required status and
  reporting fields.
* `fi.fmi.avi.model.AviationWeatherMessageOrCollection` is an empty interface that the main objects.
* Common values like statuses, variables and urls can be found and/or added in `fi.fmi.avi.model.AviationCodeListUser`.
* `java.lang.Optional` or it's primitive variants is used to represent an optional value. A `null` values shall not be used to represent non-existent value.
