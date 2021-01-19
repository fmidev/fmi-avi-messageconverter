# Model

The data model aims to create immutable objects where once the build method is called on an object it can no longer be changed.

- The Model is built on interfaces. Only the get methods for each field iare decalred in the interface
- Intefaces are implemented as abstract classes, which a are placed in a package named <b>immutable</b>. This package should be placed in the same place as the interface.
- FreeBuilder is used in the model in order to quickly generate classes that conform to the builder pattern (https://freebuilder.inferred.org/)
- The abstract class should be annotated with <b>@FreeBuilder</b> and should contain a static subclass called Builder that extends FreeBuilder implementation of the abstract class.
- The abstract classes are also annotated with JSON serialization instructions
- fi.fmi.avi.model.AviationWeatherMessage interface is extended by the base object of each model in order to give them a baseline of required status and reporting fields.
- fi.fmi.avi.model.AviationWeatherMessageOrCollection is an empty interface that the main objects
- Common values like statuses, variables and urls can be found and/or added in **fi.fmi.avi.model.AviationCodeListUser**
