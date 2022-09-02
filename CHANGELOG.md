Change Log
==========

## Version 2.4.0
git
### General

- Gradle plugin is now published to maven instead of Gradle plugin portal
- Update readme to reflect new publishing changes

## Version 2.3.0

### General

- Sort out artefact publishing
- Make runtime multiplatform again
- Publish gradle plugin to maven instead of gradle plugin portal

## Version 2.2.0

### General

- Runtime is JVM Only from now on
- Update kotlin to 1.7.0

## Version 2.1.0
### General
- Improve naming in runtime library to better capture the intention

## Version 2.0.4
### General
- Update gradle to 7.4.1
- Update kotlin to 1.6
- Gradle scan is now published with every task
- Update docs to better explain purpose of Krang
### Bugfixes
- Fix a bug in a gradle plugin where runtime library wasn't applied to common mpp targets 
### New
- Add more multiplatform sample
### Removed
- Removed native compiler plugin since kotlin 1.6 supports unified compiler abi

## Version 2.0.3
### Removed
- Removed all targets except jvm, this also resolved issues with mpp projects having both android and jvm targets
rest of the targets will come in future updates

## Version 2.0.2
### New
- Added android target for runtime library
### Removed
- Removed ios targets from runtime library

## Version 2.0.1
### New
- Added Redact annotation for value parameters which should not be passed to krang
- Added Gradle extension and CLI parameter to disable krang during compilation
- Added runtime flag to disable krang during runtime

## Version 2.0.0
### New
- Made the runtime library multiplatform
- Gradle plugin now applies runtime library automatically
### Changed
- IR Plugin now passes actuall arguments instead of preformatted strings

## Version 1.0.0-SNAPSHOT
 * Initial release
