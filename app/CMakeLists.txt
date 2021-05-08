cmake_minimum_required(VERSION 3.10)

include_directories(src/main/cpp/)

add_library(native-lib

        SHARED

        src/main/cpp/native-lib.cpp
        src/main/cpp/AudioEngine.cpp
        src/main/cpp/AudioCallback.cpp
        src/main/cpp/DataSource.h
        src/main/cpp/StorageDataSource.cpp
        src/main/cpp/LockFreeQueue.h
        src/main/cpp/utils/logging.h
        src/main/cpp/NDKExtractor.cpp
        src/main/cpp/Constants.h
        src/main/cpp/OpenGLFunctions.cpp
        src/main/cpp/Player.cpp
        src/main/cpp/Mixer.h
        src/main/cpp/IRenderableAudio.h
        src/main/cpp/utils/UtilityFunctions.cpp
        src/main/cpp/ObserverChain.h
        src/main/cpp/ObserverChain.cpp
        src/main/cpp/Converter.h
        src/main/cpp/Converter.cpp
        src/main/cpp/utils/AudioFile.h
        )

set (TARGET_LIBS log android GLESv2)

# get oboe
find_package (oboe REQUIRED CONFIG)

add_definitions(-DUSE_FFMPEG=0)
set (TARGET_LIBS ${TARGET_LIBS} mediandk)

target_link_libraries( native-lib ${TARGET_LIBS} oboe::oboe)

# Specify the path to the Oboe header files.
include_directories(${OBOE_DIR}/include)
include_directories(${OBOE_DIR}/src/flowgraph/resampler)

