set(SRCS
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_platform.h"

    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_platform.cpp"
)

target_sources("vl53l0x" PRIVATE ${SRCS})
