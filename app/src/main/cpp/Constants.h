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

constexpr int kBufferSizeInBursts = 2; // Use 2 bursts as the buffer size (double buffer)
constexpr int kMaxQueueItems = 4; // Must be power of 2

#endif //OBOE_TEST_CONSTANTS_H
