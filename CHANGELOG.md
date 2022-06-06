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


[Unreleased]: https://github.com/fmidev/fmi-avi-messageconverter/compare/fmi-avi-messageconverter-6.1.0...HEAD

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

[#101]: https://github.com/fmidev/fmi-avi-messageconverter/issues/101
