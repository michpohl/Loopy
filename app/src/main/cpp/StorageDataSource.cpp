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


#include "NDKExtractor.h"



constexpr int kMaxCompressionRatio{12};

StorageDataSource *StorageDataSource::newFromStorageAsset(AMediaExtractor &extractor,
                                                        const char *fileName,
                                                        AudioProperties targetProperties) {

    std::ifstream stream;
    stream.open(fileName, std::ifstream::in | std::ifstream::binary);

    if (!stream.is_open()) {
        LOGE("Opening stream failed in the DataSource! %s", fileName);
    } else {
        LOGD("Opened %s", fileName);

    }
    stream.seekg(0, std::ios::end);
    long size = stream.tellg();
    LOGD("size %ld", size);
    stream.close();

//    size_t *size = nullptr;
//    AMediaFormat *format = AMediaExtractor_getTrackFormat(&extractor, 0);
//    AMediaFormat_getSize(format, fileName, size);
//    LOGD("Opened %s, size %zu\n", fileName, *size);

    constexpr int kMaxCompressionRatio{12};
    const long maximumDataSizeInBytes =
            kMaxCompressionRatio * (size) * sizeof(int16_t);
    auto decodedData = new uint8_t[maximumDataSizeInBytes];

    int64_t bytesDecoded = NDKExtractor::decode(extractor, decodedData, targetProperties);
    auto numSamples = bytesDecoded / sizeof(int16_t);

    auto outputBuffer = std::make_unique<float[]>(numSamples);
    LOGD("Bytes decoded: %"
    PRId64
    "\n", bytesDecoded);
    LOGD("OutputBuffer: %zu\n", sizeof(outputBuffer));
    // The NDK decoder can only decode to int16, we need to convert to floats
    oboe::convertPcm16ToFloat(
            reinterpret_cast<int16_t *>(decodedData),
            outputBuffer.get(),
            bytesDecoded / sizeof(int16_t));

    return new StorageDataSource(std::move(outputBuffer),
                                numSamples,
                                targetProperties);
}