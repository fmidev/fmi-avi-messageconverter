# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- The model objects have been adapted for IWXXM 3.0.0 compatibility. The changes are backward compatible, but some features have been deprecated.

### Deprecated

- Deprecated TAFStatus, MetarStatus and SigmetAirmetReportStatus enums. AviationWeatherMessage.ReportStatus should be used instead. See the Javadocs on the
  deprecated enums and methods that use them for more details.
- Deprecated TAFReference class.

## Past Changelog

Previous changelog entries are available on [GitHub releases page](https://github.com/fmidev/fmi-avi-messageconverter/releases) in a more freeform format. 


