//
// Created by Tungsten on 2022/10/11.
//

#include "h2co3Launcher_internal.h"

#include <android/native_window_jni.h>
#include <jni.h>
#include <android/log.h>
#include <assert.h>

struct H2CO3LauncherInternal *h2co3Launcher;

__attribute__((constructor)) void env_init() {
    char* strptr_env = getenv("H2CO3LAUNCH_ENVIRON");
    if (strptr_env == NULL) {
        __android_log_print(ANDROID_LOG_INFO, "Environ", "No H2CO3Launcher environ found, creating...");
        h2co3Launcher = malloc(sizeof(struct H2CO3LauncherInternal));
        assert(h2co3Launcher);
        memset(h2co3Launcher, 0 , sizeof(struct H2CO3LauncherInternal));
        if (asprintf(&strptr_env, "%p", h2co3Launcher) == -1)
            abort();
        setenv("H2CO3LAUNCH_ENVIRON", strptr_env, 1);
        free(strptr_env);
    } else {
        __android_log_print(ANDROID_LOG_INFO, "Environ", "Found existing H2CO3Launcher environ: %s", strptr_env);
        h2co3Launcher = (void*) strtoul(strptr_env, NULL, 0x10);
    }
    __android_log_print(ANDROID_LOG_INFO, "Environ", "%p", h2co3Launcher);
}

void h2co3LauncherSetPrimaryClipString(const char* string) {
    PrepareH2CO3LauncherBridgeJNI();
    CallH2CO3LauncherBridgeJNIFunc( , Void, setPrimaryClipString, "(Ljava/lang/String;)V", (*env)->NewStringUTF(env, string));
}

const char* h2co3LauncherGetPrimaryClipString() {
    PrepareH2CO3LauncherBridgeJNI();
    if (h2co3Launcher->clipboard_string != NULL) {
        free(h2co3Launcher->clipboard_string);
        h2co3Launcher->clipboard_string = NULL;
    }
    CallH2CO3LauncherBridgeJNIFunc(jstring clipstr = , Object, getPrimaryClipString, "()Ljava/lang/String;");
    const char* string = NULL;
    if (clipstr != NULL) {
        string = (*env)->GetStringUTFChars(env, clipstr, NULL);
        if (string != NULL) {
            h2co3Launcher->clipboard_string = strdup(string);
        }
    }
    return h2co3Launcher->clipboard_string;
}

JNIEXPORT void JNICALL Java_org_koishi_launcher_h2co3launcher_bridge_H2CO3LauncherBridge_setH2CO3LauncherBridge(JNIEnv *env, jobject thiz, jobject h2co3Launcher_bridge) {
    h2co3Launcher->object_H2CO3LauncherBridge = (jclass)(*env)->NewGlobalRef(env, thiz);
}
//==============only for h2co3=============
ANativeWindow* h2co3LauncherGetNativeWindow() {
    return h2co3Launcher->window;
}

JNIEXPORT void JNICALL Java_org_koishi_launcher_h2co3launcher_bridge_H2CO3LauncherBridge_setH2CO3LauncherNativeWindow(JNIEnv* env, jclass clazz, jobject surface) {
    h2co3Launcher->window = ANativeWindow_fromSurface(env, surface);
    H2CO3LAUNCHER_INTERNAL_LOG("setH2CO3LauncherNativeWindow : %p, size : %dx%d", h2co3Launcher->window, ANativeWindow_getWidth(h2co3Launcher->window), ANativeWindow_getHeight(h2co3Launcher->window));
}
//==============only for h2co3=============
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (h2co3Launcher->android_jvm == NULL) {
        h2co3Launcher->android_jvm = vm;
        JNIEnv* env = 0;
        jint result = (*h2co3Launcher->android_jvm)->AttachCurrentThread(h2co3Launcher->android_jvm, &env, 0);
        if (result != JNI_OK || env == 0) {
            H2CO3LAUNCHER_INTERNAL_LOG("Failed to attach thread to JavaVM.");
            abort();
        }
        jclass class_H2CO3LauncherBridge = (*env)->FindClass(env, "org/koishi/launcher/h2co3launcher/bridge/H2CO3LauncherBridge");
        if (class_H2CO3LauncherBridge == 0) {
            H2CO3LAUNCHER_INTERNAL_LOG("Failed to find class: org/koishi/launcher/h2co3launcher/bridge/H2CO3LauncherBridge.");
            abort();
        }
        h2co3Launcher->class_H2CO3LauncherBridge = (jclass)(*env)->NewGlobalRef(env, class_H2CO3LauncherBridge);
    }
    return JNI_VERSION_1_2;
}

JNIEXPORT jint JNICALL
Java_org_koishi_launcher_h2co3launcher_bridge_H2CO3LauncherBridge_nativeGetFps(JNIEnv *env, jclass clazz) {
    int f = h2co3Launcher->fps;
    h2co3Launcher->fps = 0;
    return f;
}