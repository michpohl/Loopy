//
// Created by michael on 01.11.20.
//

#include <string>
#include <iostream>
#include <set>
#include <dirent.h>
#include <cinttypes>
#include <media/NdkMediaExtractor.h>
#include <fstream>
#include <oboe/Oboe.h>
#include "Converter.h"
#include "utils/logging.h"
#include "NDKExtractor.h"
#include "AudioCallback.h"
#include <utils/AudioFile.h>

namespace fs = std::__fs::filesystem;

Converter::Converter(AudioCallback &callback) : mCallback(callback) {
}

constexpr float kScaleI16ToFloat = (1.0f / 32768.0f);

void Converter::interleave(const uint16_t *in_L,     // mono input buffer (left channel)
                           const uint16_t *in_R,     // mono input buffer (right channel)
                           uint16_t *out,            // stereo output buffer
                           const size_t num_samples)  // number of samples
{
    for (size_t i = 0; i < num_samples; ++i) {
        out[i * 2] = in_L[i];
        out[i * 2 + 1] = in_R[i];
    }
}


bool Converter::setDestinationFolder(const char *folderName) {
    mFolder = folderName;
    LOGD("Destination folder set: %s", mFolder);
    return true;
}

bool Converter::convertFolder() {
    LOGD("converting folder: %s", mFolder);
    DIR *dir;
    struct dirent *ent;
    if ((dir = opendir(mFolder)) != nullptr) {
        LOGD("Dir exists");

        std::set<std::string> allFileNames;
        std::set<std::string> excludedFileNames;
        while ((ent = readdir(dir)) != nullptr) {
            LOGD("Inside while loop");
            std::string name = std::string(ent->d_name);

            bool isConverted = endsWith(name, pcm);
            if (isConverted) {
                // Add names of converted files to the excluded list
                name.erase(name.length() - pcm.length());
                excludedFileNames.insert(name);
            } else {
                bool isValid = endsWith(name, wav) or endsWith(name, mp3) or endsWith(name, ogg);
                if (isValid) {
                    allFileNames.insert(name);
                }
            }

        }
        closedir(dir);
        for (const auto &name: excludedFileNames) {
            LOGD("Excluded name: %s", name.c_str());
        }

        for (const auto &name : allFileNames) {
            LOGD("Name: %s", name.c_str());
            if (excludedFileNames.find(name) == excludedFileNames.end()) {
                LOGD("Not yet converted");

            std::string fullPath = std::string(mFolder) + name;
            LOGD ("Starting conversion for: %s\n", fullPath.c_str());
            LOGE ("Batch conversion is turned off. See where this log is and fix it :-)");
            doConversion(std::string(fullPath), std::string(name));
            }

        }
    } else {
        /* could not open directory */
        LOGE("Dir does not exist");
        return EXIT_FAILURE;
    }
    return false;
}

bool Converter::doConversion(const std::string &fullPath, const std::string &name) {
    LOGD("Before callback");
    mCallback.updateConversionProgress(name.c_str(), 1);
    AMediaExtractor *extractor = AMediaExtractor_new();
    if (extractor == nullptr) {
        LOGE("Could not obtain AMediaExtractor");
        return false;
    }

    FILE* testFile = fopen(fullPath.c_str(), "r");
    std::ifstream stream;
    stream.open(fullPath, std::ifstream::in | std::ifstream::binary);

    if (!stream.is_open() || testFile == nullptr) {
        LOGE("File opening failed! %s", fullPath.c_str());
        return false;
    }
    stream.seekg(0, std::ios::end);
    int fd = fileno(testFile);
    long size = stream.tellg();
    stream.close();
    mCallback.updateConversionProgress(name.c_str(), 2);

    media_status_t amresult = AMediaExtractor_setDataSourceFd(extractor, fd, 0, size);
    if (amresult != AMEDIA_OK) {
        LOGE("Error setting extractor data source, err %d", amresult);
        return false;
    } else {
        LOGD("amresult ok");
    }

    constexpr int kMaxCompressionRatio{12};
    const long maximumDataSizeInBytes = kMaxCompressionRatio * (size) * sizeof(int16_t);
    auto decodedData = new uint8_t[maximumDataSizeInBytes];

    int numChannels = NDKExtractor::getChannelCount(*extractor);
    int64_t bytesDecoded = NDKExtractor::decode(*extractor, decodedData);
    mCallback.updateConversionProgress(name.c_str(), 3);
    auto numSamples = bytesDecoded / sizeof(int16_t);
    fclose(testFile); // close file after conversion is done to avoid memory leaks


    std::string outputName = std::string(mFolder) + "/" + name + ".pcm";
    LOGD("outputName: %s", outputName.c_str());
    std::ofstream outfile(outputName.c_str(), std::ios::out | std::ios::binary);
    mCallback.updateConversionProgress(name.c_str(), 4);
    if (numChannels == 1) {
        mCallback.updateConversionProgress(name.c_str(), 5);
        auto outData = new uint16_t[maximumDataSizeInBytes * 2];
        interleave(reinterpret_cast<uint16_t *>(decodedData),
                   reinterpret_cast<uint16_t *>(decodedData), outData, numSamples);

        mCallback.updateConversionProgress(name.c_str(), 6);
        outfile.write((char *) outData, numSamples * sizeof(int16_t) * 2);
        return true;
    } else if (numChannels > 2) {
        LOGE("Only mono and stereo files can be processed. Skipping %s", name.c_str());
        return false;
    }
    mCallback.updateConversionProgress(name.c_str(), 6);
    outfile.write((char *) decodedData, numSamples * sizeof(int16_t));
    return true;

}

bool Converter::convertSingleFile(const char *fullPath, const char *fileName) {
    return doConversion(std::string(fullPath), std::string(fileName));
}







