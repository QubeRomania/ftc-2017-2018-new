set("SRCS"
    "VL53L0X.hpp"
    "VL53L0X.cpp")

add_library(
    "vl53l0x" SHARED ${SRCS})

target_include_directories("vl53l0x" PRIVATE "core")
target_include_directories("vl53l0x" PRIVATE "platform")

add_subdirectory("core")
add_subdirectory("platform")
