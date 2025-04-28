cmake_minimum_required(VERSION 3.22.1)

project("MatrixCalculator")

# Include Eigen headers (you must have Eigen downloaded inside cpp/eigen/)
include_directories(src/main/cpp/Eigen)

# Create the shared library from MatrixCalculator.cpp
add_library(${CMAKE_PROJECT_NAME} SHARED
        MatrixCalculator.cpp)


find_library(log-lib log)
find_library(c++_shared-lib c++_shared)


# Link necessary libraries
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        c++_shared
        ${log-lib}
        ${c++_shared-lib})

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17 -fexceptions -frtti -DANDROID_STL=c++_shared")
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)


