# Loopy Audio Looper 2

This is the repository for **Loopy Audio Looper 2**, which is available on the Play Store:

[<img src="https://user-images.githubusercontent.com/25121161/124374823-96549080-dc9e-11eb-8e76-0621073f9b62.png" alt="play store logo" width="196">
](https://play.google.com/store/apps/details?id=com.michaelpohl.loopyplayer2)

## About the app

This app is meant to provide a simple solution for anyone who wants to loop audio files easily, like
DJs, artists or musicians. It can be used for practice sessions, as a source of uninterrupted
background atmospheres or to play endless beat loops.

If you have tried it before, you might know that it is really hard to find a player that just
perfectly and reliably loops an audio track for you. It's a known problem of the Android OS. This
player aims to give you just that: Seamless looping. Loopy 2 uses low-level code to make sure there
are no hickups, which could still be observed in some situations with its predecessor.

The app plays audio files from your media library or files you select from your file browser. You
can have a file repeat endlessly, or automatically skip to another loop when it reaches the end.

Important: If you find bugs in the app, use the contact function in the app so I can fix them
faster. If you like the app but are missing a feature, feel free to let me know that too so I can
consider it in future releases.

Please be aware, that Loopy 2 is not an audio editor! That means if you need exact results, your
files need to be prepared and cut properly.

## About the source code

This is an Android project using Kotlin and C++. If you want to play around with it, or fork it, you
should be able to just clone the repository and build the app in Android studio.

Internally, Loopy 2 uses [oboe](https://github.com/google/oboe) to do the sound processing. Files
selected by the user get converted to raw PCM and stored internally to minimize startup time, then
oboe handles queueing and playback of the files.

There is also a basic CI/CD setup using Github Actions (it's very basic, it just creates an apk and
uploads it to the Play Store ), so if you fork this repo and get automated builds you can easily
tweak that to your needs - just add your own secrets.

**Please be aware: This is a hobby project, and I work on this very infrequently in my very limited
spare time. This is often visible in the code base (and, for example, its lack of test coverage). So
if you're looking for examples or a reference how to do something, don't use this!!! Come back in 3
to 5 years, by then I should have cleaned up most of it :-) (Of course, if you feel inclined to help
improve this, I am happy to merge any usefull pull requests).**

### License

Copyright 2019 Michael Pohl

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0
