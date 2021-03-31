Change Log
==========

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
