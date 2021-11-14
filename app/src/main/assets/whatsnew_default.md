### Changelog

## Version 1.2.2

* Fixed a crash that could randomly occur
* Fixed back button behavior in media browser
* Fixed a bug with albums that contain just a single song

## Version 1.2.1

* Fixed a bug that would prevent playback after loading new files in some cases
* Fixed a localisation issue

## Version 1.2

* Loopy 2 now respects AudioFocus. It will pause playback when other apps need to output sound, or during phone calls
* Improved background playback. Loopy will now show a notification with basic control options  
* Fixed a bug that could cause noise at playback start on some devices
* Fixed a bug related to sample rate switching

## Version 1.1

* Add crash reporting dialog

## Version 1.0

Loopy 2 brings a lot of changes over Loopy I. Key improvements:

* Now uses low level C++ code to process audio playback. It's faster and helps making sure the looping is really seamless.
* Audio files are now pre-transcoded and stored internally, for better performance ( If you remove an entry from your player,
  the file is removed from Loopy's storage)
* You can now pick between three different sample rates, to adjust for your audio files' encoding
* Updated and improved design
* Many small bugs got fixed

**If you find bugs or errors, please contact me by clicking** *Contact and Feedback* **in the menu.**


