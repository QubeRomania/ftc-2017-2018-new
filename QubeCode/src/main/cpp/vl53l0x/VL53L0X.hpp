#pragma once

#include <jni.h>

#define NAME_PREFIX \
    Java_ro_cnmv_qube_hardware_sensors_VL53L0X

#define NATIVE_FUNC(name) \
    JNICALL JNIEXPORT NAME_PREFIX##_##name

extern "C"
jstring NATIVE_FUNC(stringFromJNI)(
    JNIEnv *env,
    jobject self
);

class VL53L0X {
public:

private:
};
