cmake_minimum_required(VERSION 3.20)
project(mcef_jni CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ARM64 specific optimizations
if(ANDROID_ABI STREQUAL "arm64-v8a")
    add_compile_options(
        -march=armv8-a
        -mtune=cortex-a72
        -O3
        -fPIC
    )
    message(STATUS "Building for ARM64-v8a (aarch64)")
endif()

# Find JNI
find_package(JNI REQUIRED)

# Source files
set(SOURCES
    jcef_jni.cpp
    cef_bridge.cpp
)

# Create shared library
add_library(jcef_jni SHARED ${SOURCES})

# Include directories
target_include_directories(jcef_jni PRIVATE
    ${JAVA_INCLUDE_PATH}
    ${JAVA_INCLUDE_PATH}/linux
    ${CMAKE_SOURCE_DIR}/include
)

# Compiler flags
if(UNIX AND NOT APPLE)
    target_compile_options(jcef_jni PRIVATE
        -Wall
        -Wextra
        -Wpedantic
        -fvisibility=hidden
        -fvisibility-inlines-hidden
    )
endif()

# Link libraries
target_link_libraries(jcef_jni PRIVATE
    log
)

# Output directory
set_target_properties(jcef_jni PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib/arm64-v8a"
)
