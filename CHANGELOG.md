# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]
### Bugs squashed
- Fixed erroneous error message that was occurring when all files were loaded successfully.
- When reading multiple images files, if user cancels the progress bar, nothing that has been loaded is cleared (previously everything was cleared).
### Changed
- Changed contact email.
- Duplicate image files (same image used for multiple samples) are no longer kept in memory.
- Added options in new Advanced menu to read and store image files all at once (memory heavy), as encountered, or each time the sample image changes (memory light, time heavy).

## 2022-03-04
### Bugs squashed
- If image files exist but can not be read, the GUI now indicates this correctly (can happen for some CMYK and tiff images). Also added an appropriate error message when this happens.
- Sample info panel was displaying a sample after removing the last one (a very minor insignificant issue).

## 2018-01-11
### Changed
- Added better cross-platform support (newline characters for non *nix platforms).
