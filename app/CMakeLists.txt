cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/cpp/)

add_library(native-lib
        SHARED

        src/main/cpp/native-lib.cpp

        src/main/cpp/AudioEngine.cpp

        src/main/cpp/DataSource.h

        src/main/cpp/AAssetDataSource.cpp

        src/main/cpp/LockFreeQueue.h

        src/main/cpp/logging.h

        src/main/cpp/NDKExtractor.cpp

        src/main/cpp/Constants.h

        src/main/cpp/OpenGLFunctions.cpp

        src/main/cpp/Player.cpp

        src/main/cpp/Mixer.h

        src/main/cpp/IRenderableAudio.h

        src/main/cpp/UtilityFunctions.cpp

        )

set (TARGET_LIBS log android oboe GLESv2)

add_definitions(-DUSE_FFMPEG=0)
set (TARGET_LIBS ${TARGET_LIBS} mediandk)

target_link_libraries( native-lib ${TARGET_LIBS} )

#set(CMAKE_CXX_STANDARD 14)

# Set the path to the Oboe directory.
set(OBOE_DIR /Volumes/Work/Repos/Android/oboe-test/oboe)

# Add the Oboe library as a subdirectory.
add_subdirectory(${OBOE_DIR} ./oboe)

# Specify the path to the Oboe header files.
include_directories(${OBOE_DIR}/include)

#target_compile_options(native-lib
#        PRIVATE -std=c++14 -Wall -Werror "$<$<CONFIG:RELEASE>:-Ofast>")

