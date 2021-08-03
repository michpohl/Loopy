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
#include <cinttypes>

#include "StorageDataSource.h"
#include <fstream>
//#include <MultiChannelResampler.h>
#include <utils/AudioFile.h>
#include "NDKExtractor.h"


StorageDataSource *StorageDataSource::newFromStorageAsset(AMediaExtractor &extractor,
                                                          const char *fileName,
                                                          AudioProperties targetProperties) {
    LOGD("Start newFromStorageAsset");
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

//
//        resampler::MultiChannelResampler *mResampler = resampler::MultiChannelResampler::make(
//                2, // channel count
//                44100, // input sampleRate
//                48000, // output sampleRate
//                resampler::MultiChannelResampler::Quality::Best); // conversion quality
//
//        int inputFramesLeft = numInputFrames;
////
//        while (inputFramesLeft > 0) {
//            LOGD("frames left: %i", inputFramesLeft);
////            LOGD("while...%i", inputFramesLeft);
//            if (mResampler->isWriteNeeded()) {
////                LOGD("Write is needed");
//                mResampler->writeNextFrame(inputBuffer);
//                inputBuffer += channelCount;
//                inputFramesLeft--;
//            } else {
////                LOGD("Write is not needed");
//                mResampler->readNextFrame(outputBuffer2);
//
//                outputBuffer2 += channelCount;
//                numOutputFrames++;
//            }
//        }
//        LOGD("While loop ended");
//        delete mResampler;
//        for (int i = 0; i < numSamples2; i++) {
//            outputBuffer3.get()[i] = outputBuffer2[i];
//        }
//
//        return new StorageDataSource(std::move(outputBuffer3),
//                                     numSamples2,
//                                     targetProperties);
    }
    LOGD("Before return");
    return new StorageDataSource(std::move(outputBuffer),
                                 numSamples,
                                 targetProperties);
}

StorageDataSource *
StorageDataSource::openFromSet(const char *fileName, AudioProperties targetProperties) {
    LOGD("Start openFromset");

    long bufferSize;
    char * inputBuffer;

    std::ifstream stream;
    stream.open(fileName, std::ifstream::in | std::ifstream::binary);

    if (!stream.is_open()) {
        LOGE("Opening stream failed! %s", fileName);
    } else {
        LOGD("Opened %s", fileName);

    }

    stream.seekg (0, std::ios::end);
    bufferSize = stream.tellg();
    LOGD("size %ld", bufferSize);
    stream.seekg (0, std::ios::beg);

    inputBuffer = new char [bufferSize];

    stream.read(inputBuffer, bufferSize);
    LOGD("Successfully read: %i", stream.gcount());
    stream.close();
    auto numSamples = bufferSize / sizeof(int16_t);
    LOGD("NumSamples: %i", numSamples);

    auto outputBuffer = std::make_unique<float[]>(numSamples);

//    // The NDK decoder can only decode to int16, we need to convert to floats
    oboe::convertPcm16ToFloat(
            reinterpret_cast<int16_t *>(inputBuffer),
            outputBuffer.get(),
            bufferSize / sizeof(int16_t));

    return new StorageDataSource(std::move(outputBuffer),
                                 numSamples,
                                 targetProperties);
}
