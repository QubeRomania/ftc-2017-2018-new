set(SRCS
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_calibration.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_core.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_ranging.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_strings.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_def.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_device.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_interrupt_threshold_settings.h"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_tuning.h"

    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api.c"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_calibration.c"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_core.c"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_ranging.c"
    "${CMAKE_CURRENT_LIST_DIR}/vl53l0x_api_strings.c"
)

target_sources("vl53l0x" PRIVATE ${SRCS})
