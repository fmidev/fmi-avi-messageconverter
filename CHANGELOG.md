# Changelog - fmi-avi-messageconverter

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- ...

### Changed

- ...

### Deprecated

- ...

### Removed

- ...

### Fixed

- ...

### Security

- ...

## [v8.2.0] - 2026-01-12

### Added

- Added METAR/SPECI observation time to the `GenericAviationWeatherMessage` model [#135]
- Documented limitations of `Winding` for polygons crossing the antimeridian [#136]

## [v8.1.0] - 2025-12-08

### Added

- NIGHTSIDE location indicator geometry computation for Annex 3 Amendment 82 Space Weather Advisories [#134]

## [v8.0.0] - 2025-11-21

### Added

- Added support for Annex 3 Amendment 82 Space Weather Advisories. Support for Amd 79 SWX is retained and the models can
  be bidirectionally transformed [#121]

## [v7.0.0-beta3] - 2023-11-24

### Changed

- Added SIGMET phenomenon type to the SIGMET model. This will enable the use of minimal test sigmets of different types
  without meteorological information [#120]
- Made SIGMET analysis type optional [#114]

## [v7.0.0-beta2] - 2023-10-23

### Changed

- Moved SIGMET and AIRMET getAnalysisGeometries() to SIGMETAIRMET interface [#116]

## [v7.0.0-beta1] - 2023-09-27

### Added

- Experimental support for SIGMET/AIRMET messages [#100]

## [v6.3.0] - 2023-02-15

### Added

- ConversionHint for disabling line wrapping in TAC serialization [#105]

## [v6.2.0] - 2022-08-24

### Added

- Add a general supertype AviationWeatherMessageCollection for MeteorologicalBulletin [#103]

## [v6.1.0] - 2022-06-06

### Added

- Add support for GTS socket protocol. [#101]

## [v6.0.0] - 2022-02-22

### Added

- Added a utility and a model to parse and serialize GTS exchange file structure. [#83], [#93]
- Enabled invalid BBB augmentation indicator handling in bulletin heading. [#90]
- Add xmlNamespace in GenericAviationWeatherMessage [#92]
- Dependency updates [#98]

### Changed

- Replaced GenericAviationMessage targetAerodrome property with a map of location indicators. [#85]

## [v5.0.0] - 2021-04-13

### Added

- Created overview documentation for developers. [#76]

### Changed

- The model objects have been adapted for IWXXM 3.0.0 compatibility. The changes are backward compatible, but some features have been deprecated. [#66]
- `AviationWeatherMessage.getReportStatus()` was made non-`Optional` (backwards-incompatible). [#74]
- Code quality enhancements. [#80]

### Deprecated

- Deprecated TAFStatus, MetarStatus and SigmetAirmetReportStatus enums. AviationWeatherMessage.ReportStatus should be
  used instead. See the Javadocs on the deprecated enums and methods that use them for more details.
- Deprecated TAFReference class.

## Past Changelog

Previous changelog entries are available
on [GitHub releases page](https://github.com/fmidev/fmi-avi-messageconverter/releases) in a more freeform format.


[Unreleased]: https://github.com/fmidev/fmi-avi-messageconverter/compare/fmi-avi-messageconverter-8.2.0...HEAD

[v8.2.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-8.2.0

[v8.1.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-8.1.0

[v8.0.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-8.0.0

[v7.0.0-beta3]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-7.0.0-beta3

[v7.0.0-beta2]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-7.0.0-beta2

[v7.0.0-beta1]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-7.0.0-beta1

[v6.3.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-6.3.0

[v6.2.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-6.2.0

[v6.1.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-6.1.0

[v6.0.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-6.0.0

[v5.0.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-5.0.0

[#66]: https://github.com/fmidev/fmi-avi-messageconverter/issues/66

[#74]: https://github.com/fmidev/fmi-avi-messageconverter/issues/74

[#76]: https://github.com/fmidev/fmi-avi-messageconverter/issues/76

[#80]: https://github.com/fmidev/fmi-avi-messageconverter/issues/80

[#83]: https://github.com/fmidev/fmi-avi-messageconverter/issues/83

[#85]: https://github.com/fmidev/fmi-avi-messageconverter/issues/85

[#90]: https://github.com/fmidev/fmi-avi-messageconverter/issues/90

[#93]: https://github.com/fmidev/fmi-avi-messageconverter/issues/93

[#92]: https://github.com/fmidev/fmi-avi-messageconverter/pull/92

[#98]: https://github.com/fmidev/fmi-avi-messageconverter/pull/98

[#100]: https://github.com/fmidev/fmi-avi-messageconverter/pull/100

[#101]: https://github.com/fmidev/fmi-avi-messageconverter/issues/101

[#103]: https://github.com/fmidev/fmi-avi-messageconverter/pull/103

[#105]: https://github.com/fmidev/fmi-avi-messageconverter/pull/105

[#114]: https://github.com/fmidev/fmi-avi-messageconverter/pull/114

[#116]: https://github.com/fmidev/fmi-avi-messageconverter/pull/116

[#120]: https://github.com/fmidev/fmi-avi-messageconverter/pull/120

[#121]: https://github.com/fmidev/fmi-avi-messageconverter/pull/121

[#134]: https://github.com/fmidev/fmi-avi-messageconverter/pull/134

[#135]: https://github.com/fmidev/fmi-avi-messageconverter/pull/135

[#136]: https://github.com/fmidev/fmi-avi-messageconverter/pull/136