# Changelog - fmi-avi-messageconverter

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v5.0.0] - 2014-04-13

### Added

- Created overview documentation for developers. [#76]

### Changed

- The model objects have been adapted for IWXXM 3.0.0 compatibility. The changes are backward compatible, but some features have been deprecated. [#66]
- `AviationWeatherMessage.getReportStatus()` was made non-`Optional` (backwards-incompatible). [#74]
- Code quality enhancements. [#80]

### Deprecated

- Deprecated TAFStatus, MetarStatus and SigmetAirmetReportStatus enums. AviationWeatherMessage.ReportStatus should be used instead. See the Javadocs on the
  deprecated enums and methods that use them for more details.
- Deprecated TAFReference class.

## Past Changelog

Previous changelog entries are available on [GitHub releases page](https://github.com/fmidev/fmi-avi-messageconverter/releases) in a more freeform format.


[Unreleased]: https://github.com/fmidev/fmi-avi-messageconverter/compare/fmi-avi-messageconverter-5.0.0...HEAD

[v5.0.0]: https://github.com/fmidev/fmi-avi-messageconverter/releases/tag/fmi-avi-messageconverter-5.0.0

[#66]: https://github.com/fmidev/fmi-avi-messageconverter/issues/66

[#74]: https://github.com/fmidev/fmi-avi-messageconverter/issues/74

[#76]: https://github.com/fmidev/fmi-avi-messageconverter/issues/76

[#80]: https://github.com/fmidev/fmi-avi-messageconverter/issues/80
