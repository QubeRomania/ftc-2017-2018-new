set("SRCS"
    "VL53L0X.hpp"
    "VL53L0X.cpp")

add_library(
    "vl53l0x" SHARED ${SRCS})
