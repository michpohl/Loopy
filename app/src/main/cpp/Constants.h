//
// Created by Michael on 2019-07-20.
//

#ifndef OBOE_TEST_CONSTANTS_H
#define OBOE_TEST_CONSTANTS_H

#include "OpenGLFunctions.h"

struct AudioProperties {
    int32_t channelCount;
    int32_t sampleRate;
};

constexpr char loopFileName[] { "testing.mp3" };

constexpr int kBufferSizeInBursts = 2; // Use 2 bursts as the buffer size (double buffer)
constexpr int kMaxQueueItems = 4; // Must be power of 2

// Colors for game states and visual feedback for taps
constexpr ScreenColor kPlayingColor = GREY;
constexpr ScreenColor kLoadingColor = YELLOW;
constexpr ScreenColor kLoadingFailedColor = RED;
constexpr ScreenColor kTapSuccessColor = GREEN;
constexpr ScreenColor kTapEarlyColor = ORANGE;
constexpr ScreenColor kTapLateColor = PURPLE;

// This defines the size of the tap window in milliseconds. For example, if defined at 100ms the
// player will have 100ms before and after the centre of the tap window to tap on the screen and
// be successful
constexpr int kWindowCenterOffsetMs = 100;

// Filename for clap sound asset (in assets folder)
constexpr char kClapFilename[] { "CLAP.mp3" };

// Filename for the backing track asset (in assets folder)
constexpr char kBackingTrackFilename[] { "testing.mp3" };

// The game will first demonstrate the pattern which the user should copy. It does this by
// "clapping" (playing a clap sound) at certain times during the song. We can specify these times
// here in milliseconds. Our backing track has a tempo of 120 beats per minute, which is 2 beats per
// second. This means a pattern of 3 claps starting on the first beat of the first bar would mean
// playing claps at 0ms, 500ms and 1000ms
constexpr int64_t kClapEvents[] { 0, 500, 1000 };

// We then want the user to tap on the screen exactly 4 beats after the first clap so we add clap
// windows at 2000ms, 2500ms and 3000ms (or 2, 2.5 and 3 seconds). @see getTapResult for more info.
constexpr int64_t kClapWindows[] { 2000, 2500, 3000 };

#endif //OBOE_TEST_CONSTANTS_H
