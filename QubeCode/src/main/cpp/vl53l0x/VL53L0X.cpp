#include "VL53L0X.hpp"

#include <iostream>
#include <string>
#include <vector>

using namespace std;

jstring NATIVE_FUNC(stringFromJNI)(JNIEnv *env, jobject self) {
    return env->NewStringUTF("Hello");
}
