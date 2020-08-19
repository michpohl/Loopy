/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


#include <utils/logging.h>
#include <oboe/Oboe.h>
#include <inttypes.h>

#include "StorageDataSource.h"
#include <fstream>
#include <MultiChannelResampler.h>


#include "NDKExtractor.h"

StorageDataSource *StorageDataSource::newFromStorageAsset(AMediaExtractor &extractor,
                                                          const char *fileName,
                                                          AudioProperties targetProperties) {

    std::ifstream stream;
    stream.open(fileName, std::ifstream::in | std::ifstream::binary);

    if (!stream.is_open()) {
        LOGE("Opening stream failed! %s", fileName);
    } else {
        LOGD("Opened %s", fileName);

    }
    stream.seekg(0, std::ios::end);
    long size = stream.tellg();
    LOGD("size %ld", size);
    stream.close();

    constexpr int kMaxCompressionRatio{12};
    const long maximumDataSizeInBytes =
            kMaxCompressionRatio * (size) * sizeof(int16_t);
    auto decodedData = new uint8_t[maximumDataSizeInBytes];

    int32_t rate = NDKExtractor::getSampleRate(extractor);
    int32_t bitRate = NDKExtractor::getBitRate(extractor);
    int32_t *inputSampleRate = &rate;

    int64_t bytesDecoded = NDKExtractor::decode(extractor, decodedData, targetProperties);
    auto numSamples = bytesDecoded / sizeof(int16_t);

    auto outputBuffer = std::make_unique<float[]>(numSamples);
    LOGD("Bytes decoded: %" PRId64 "\n", bytesDecoded);
    LOGD("OutputBuffer: %zu\n", sizeof(outputBuffer));
    LOGD("Number of Samples: %i", numSamples);
    // The NDK decoder can only decode to int16, we need to convert to floats
    oboe::convertPcm16ToFloat(
            reinterpret_cast<int16_t *>(decodedData),
            outputBuffer.get(),
            bytesDecoded / sizeof(int16_t));


    if (*inputSampleRate == targetProperties.sampleRate) {
        LOGD("Target sample rate:%i", targetProperties.sampleRate);
        LOGD("Input sample rate:%i", *inputSampleRate);
        return new StorageDataSource(std::move(outputBuffer),
                                     numSamples,
                                     targetProperties);
    } else {
        LOGD("SampleRate is off");


        float *inputBuffer;
        LOGD("Casting");
        inputBuffer = reinterpret_cast<float *>(decodedData);
        LOGD("Size of inputBuffer: %zu\n", sizeof(inputBuffer));
        LOGD("Divide by this to get the number of samples: %i", sizeof(int16_t));
        auto numSamples2 = bytesDecoded / sizeof(int16_t);

//
        float *outputBuffer2;    // multi-channel buffer to be filled
        auto outputBuffer3 = std::make_unique<float[]>(numSamples);
        int numInputFrames;  // number of frames of input
//
//        // TODO is this correct?
//        numInputFrames = numSamples2/16;
        numInputFrames = 192;
        LOGD("numInputFrames: %i", numInputFrames);
        LOGD("numSamples: %i", numSamples);


        int numOutputFrames = 0;
        int channelCount = 2;    // 1 for mono, 2 for stereo // TODO needs to be collected from file too

        resampler::MultiChannelResampler *mResampler = resampler::MultiChannelResampler::make(
                2, // channel count
                44100, // input sampleRate
                48000, // output sampleRate
                resampler::MultiChannelResampler::Quality::Best); // conversion quality

        int inputFramesLeft = numInputFrames;
//
        while (inputFramesLeft > 0) {
            LOGD("frames left: %i", inputFramesLeft);
//            LOGD("while...%i", inputFramesLeft);
            if (mResampler->isWriteNeeded()) {
//                LOGD("Write is needed");
                mResampler->writeNextFrame(inputBuffer);
                inputBuffer += channelCount;
                inputFramesLeft--;
            } else {
//                LOGD("Write is not needed");
                mResampler->readNextFrame(outputBuffer2);

                outputBuffer2 += channelCount;
                numOutputFrames++;
            }
        }

        for (int i = 0; i < numSamples; i++) {
            outputBuffer3.get()[i] = outputBuffer2[i];
        }

        return new StorageDataSource(std::move(outputBuffer3),
                                     numSamples,
                                     targetProperties);
        delete mResampler;
    }
    LOGD("Before return");
    return new StorageDataSource(std::move(outputBuffer),
                                 numSamples,
                                 targetProperties);
}
