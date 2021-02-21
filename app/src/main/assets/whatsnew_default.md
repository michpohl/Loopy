### What's new in Loopy 1.0

Loopy 1.0 has a code base that is 90% different from Loopy 0.9, but it mostly works the same. Key improvements:

* Now uses low level C++ code to process audio playback. It's faster and more reliable.
* Audio files are now pre-transcoded and stored internally, for better performance ( If you remove an entry from your player, the file is removed from Loopy)
* You can now pick between three different sample rates, to adjust for your audio files' encoding
* Updated design
* Many small bugs got fixed
