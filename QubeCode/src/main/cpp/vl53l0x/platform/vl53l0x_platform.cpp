#include <vl53l0x_platform.h>

#include <vl53l0x_api.h>
#include <vl53l0x_def.h>

#include <unistd.h>

static Device device;
static VL53L0X_DEV Dev;

extern "C" jint JNICALL JNIEXPORT Java_ro_cnmv_qube_hardware_sensors_VL53L0X_distance(JNIEnv* env, jobject self) {
    device.env = env;
    device.self = self;

    Dev = &device;

    VL53L0X_Error Status = VL53L0X_ERROR_NONE;
    VL53L0X_RangingMeasurementData_t RangingMeasurementData;
    FixPoint1616_t LimitCheckCurrent;
    uint32_t refSpadCount;
    uint8_t isApertureSpads;
    uint8_t VhvSettings;
    uint8_t PhaseCal;

    if(Status == VL53L0X_ERROR_NONE)
        Status = VL53L0X_StaticInit(Dev);

    if(Status == VL53L0X_ERROR_NONE)
        Status = VL53L0X_PerformRefCalibration(Dev,
                                               &VhvSettings, &PhaseCal);

    if(Status == VL53L0X_ERROR_NONE)
        Status = VL53L0X_PerformRefSpadManagement(Dev,
                                                  &refSpadCount, &isApertureSpads);

    if(Status == VL53L0X_ERROR_NONE)
        Status = VL53L0X_SetDeviceMode(Dev, VL53L0X_DEVICEMODE_SINGLE_RANGING);

    // Enable/Disable Sigma and Signal check
    if (Status == VL53L0X_ERROR_NONE) {
        Status = VL53L0X_SetLimitCheckEnable(Dev,
                                             VL53L0X_CHECKENABLE_SIGMA_FINAL_RANGE, 1);
    }
    if (Status == VL53L0X_ERROR_NONE) {
        Status = VL53L0X_SetLimitCheckEnable(Dev,
                                             VL53L0X_CHECKENABLE_SIGNAL_RATE_FINAL_RANGE, 1);
    }

    if (Status == VL53L0X_ERROR_NONE) {
        Status = VL53L0X_SetLimitCheckEnable(Dev,
                                             VL53L0X_CHECKENABLE_RANGE_IGNORE_THRESHOLD, 1);
    }

    if (Status == VL53L0X_ERROR_NONE) {
        Status = VL53L0X_SetLimitCheckValue(Dev,
                                            VL53L0X_CHECKENABLE_RANGE_IGNORE_THRESHOLD,
                                            (FixPoint1616_t)(1.5*0.023*65536));
    }

    if(Status == VL53L0X_ERROR_NONE)
    {
        Status = VL53L0X_PerformSingleRangingMeasurement(Dev, &RangingMeasurementData);

        VL53L0X_GetLimitCheckCurrent(Dev, VL53L0X_CHECKENABLE_RANGE_IGNORE_THRESHOLD, &LimitCheckCurrent);
    }

    if (Status != VL53L0X_ERROR_NONE)
        return Status;

    return RangingMeasurementData.RangeMilliMeter;
}

VL53L0X_Error VL53L0X_WriteMulti(VL53L0X_DEV Dev, uint8_t index, uint8_t *pdata, uint32_t count) {
    auto env = Dev->env;
    auto self = Dev->self;

    auto klass = env->GetObjectClass(self);
    auto id = env->GetMethodID(klass, "writeMulti", "(I[B)V");

    auto data = env->NewByteArray(count);
    env->SetByteArrayRegion(data, 0, count, (const jbyte*)pdata);

    env->CallVoidMethod(self, id, index, data);

    env->DeleteLocalRef(data);

    env->DeleteLocalRef(klass);

    return VL53L0X_ERROR_NONE;
}

VL53L0X_Error VL53L0X_ReadMulti(VL53L0X_DEV Dev, uint8_t index,
                                uint8_t *pdata, uint32_t count) {
    auto env = Dev->env;
    auto self = Dev->self;

    auto klass = env->GetObjectClass(self);
    auto id = env->GetMethodID(klass, "readMulti", "(II)[B");

    auto ret = env->CallObjectMethod(self, id, index, count);

    env->GetByteArrayRegion((jbyteArray)ret, 0, count, (jbyte*)pdata);

    env->DeleteLocalRef(ret);

    env->DeleteLocalRef(klass);

    return VL53L0X_ERROR_NONE;
}

VL53L0X_Error VL53L0X_PollingDelay(VL53L0X_DEV) {
    return VL53L0X_ERROR_NONE;
}

VL53L0X_Error VL53L0X_WrByte(VL53L0X_DEV Dev, uint8_t index, uint8_t data) {
    return VL53L0X_WriteMulti(Dev, index, &data, 1);
}

VL53L0X_Error VL53L0X_WrWord(VL53L0X_DEV Dev, uint8_t index, uint16_t data) {
    return VL53L0X_WriteMulti(Dev, index, (uint8_t*)&data, 2);
}

VL53L0X_Error VL53L0X_WrDWord(VL53L0X_DEV Dev, uint8_t index, uint32_t data) {
    return VL53L0X_WriteMulti(Dev, index, (uint8_t*)&data, 4);
}

VL53L0X_Error VL53L0X_RdByte(VL53L0X_DEV Dev, uint8_t index, uint8_t *data) {
    return VL53L0X_ReadMulti(Dev, index, data, 1);
}

VL53L0X_Error VL53L0X_RdWord(VL53L0X_DEV Dev, uint8_t index, uint16_t *data) {
    return VL53L0X_ReadMulti(Dev, index, (uint8_t*)data, 2);
}

VL53L0X_Error VL53L0X_RdDWord(VL53L0X_DEV Dev, uint8_t index, uint32_t *data) {
    return VL53L0X_ReadMulti(Dev, index, (uint8_t*)data, 4);
}

VL53L0X_Error VL53L0X_UpdateByte(VL53L0X_DEV Dev, uint8_t index,
                                        uint8_t AndData, uint8_t OrData) {
    uint8_t data = 0;
    VL53L0X_RdByte(Dev, index, &data);
    data &= AndData;
    data |= OrData;
    return VL53L0X_WrByte(Dev, index, data);
}
