#include "VL53L0X.hpp"

jstring NATIVE_FUNC(stringFromJNI)(JNIEnv *env, jobject self) {


    return env->NewStringUTF("Hello");
}
