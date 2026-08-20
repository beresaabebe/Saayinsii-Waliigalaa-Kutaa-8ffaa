# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.7.2] - 2026-08-20

### Added
- **Rewarded Ad Support for Ad Disabling**: Implemented a high-eCPM rewarded ad flow to allow users to disable ads for a 4-minute study period.
- **Enterprise-level Documentation**: Added comprehensive `README.md` and `CHANGELOG.md`.

### Fixed
- **R8 Build Errors**: Resolved "Missing classes" issue during release builds by adding necessary `-dontwarn` rules for security providers (Bouncy Castle, Conscrypt, OpenJSSE).
- **Ad Manager Synchronization**: Fixed ad chain initialization and container visibility logic.

### Changed
- **Ad Disabling Flow**: Migrated from a simple button click to a rewarded video incentive model.
- **ProGuard/R8 Rules**: Optimized obfuscation and shrinking configuration.

## [1.7.1] - 2026-07-15

### Added
- **16KB Page Size Compatibility**: Overrides for `sqlite`, `work-runtime`, and `play-services-basement` to support modern Android performance standards.
- **Multi-Network Ad Mediation**: Integrated Facebook Audience Network, Vungle, and Unity Ads as fallback providers.

### Fixed
- **Memory Leaks**: Optimized `PdfRenderer` lifecycle management in `BookDetailActivity`.
- **UI Responsiveness**: Improved `ViewPager2` transition smoothness.

## [1.0.0] - 2026-01-10

### Added
- Initial release of Saayinsii Waliigalaa Kutaa 8ffaa.
- Native PDF rendering with zoom support.
- Chapter-based navigation.
- Firebase Analytics and Crashlytics integration.
