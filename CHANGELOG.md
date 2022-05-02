# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]
### Bugs squashed
### Changed
- The centroid coordinates for the radius measurement are now printed to the information panel.

## 2022-04-18
### Bugs squashed
- Fixed erroneous error message that was occurring when all files were loaded successfully.
- When reading multiple images files, if the user cancels the progress bar then anything loaded before cancelling is removed (the entire loading procedure is cancelled).
### Changed
- Changed contact email.
- Duplicate image files (same image used for multiple samples) are no longer kept in memory (only one copy is kept in memory).
- Added options in new Advanced menu to read and store image files all at once (memory heavy), as encountered, or each time the sample image changes (memory light, time heavy).
- If a file can not be found when loading, all samples are still created. No warning message is provided after loading but the image will not show up when navigating to the sample and the sample information panel will provide further information.

## 2022-03-04
### Bugs squashed
- If image files exist but can not be read, the GUI now indicates this correctly (can happen for some CMYK and tiff images). Also added an appropriate error message when this happens.
- Sample info panel was displaying a sample after removing the last one (a very minor insignificant issue).

## 2018-01-11
### Changed
- Added better cross-platform support (newline characters for non *nix platforms).
