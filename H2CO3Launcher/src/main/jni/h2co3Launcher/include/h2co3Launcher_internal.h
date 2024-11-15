//
// Created by Tungsten on 2022/10/11.
//

#ifndef H2CO3_LAUNCHER_H2CO3Launcher_INTERNAL_H
#define H2CO3_LAUNCHER_H2CO3Launcher_INTERNAL_H

#include <stdlib.h>
#include <pthread.h>
#include <jni.h>
#include <string.h>
#include <unistd.h>
#include <sys/epoll.h>
#include <errno.h>
#include <android/native_window.h>

#include "h2co3Launcher_bridge.h"
#include "h2co3Launcher_keycodes.h"
////==============only for boat=============
typedef struct _QueueElement {
    struct _QueueElement* next;
    H2CO3LauncherEvent event;
} QueueElement;

typedef struct {
    int count;
    int capacity;
    QueueElement* head;
    QueueElement* tail;
} EventQueue;
//==============only for boat=============

struct H2CO3LauncherInternal {
    JavaVM* android_jvm;
    jclass class_H2CO3LauncherBridge;
    jobject object_H2CO3LauncherBridge;
    ANativeWindow* window;
    char* clipboard_string;
    FILE* logFile;
    //only for boat
    EventQueue event_queue;
    pthread_mutex_t event_queue_mutex;
    int has_event_pipe;
    int event_pipe_fd[2];
    int epoll_fd;
    int fps;
};

extern struct H2CO3LauncherInternal *h2co3Launcher;

_Noreturn void nominal_exit(int code);

#define H2CO3Launcher_INTERNAL_LOG(x...) do { \
    fprintf(h2co3Launcher->logFile, "[H2CO3Launcher Internal] %s:%d\n", __FILE__, __LINE__); \
    fprintf(h2co3Launcher->logFile, x); \
    fprintf(h2co3Launcher->logFile, "\n"); \
    fflush(h2co3Launcher->logFile); \
    } while (0)

#define PrepareH2CO3LauncherBridgeJNI() \
    JavaVM* vm = h2co3Launcher->android_jvm; \
    JNIEnv* env = NULL; \
    jint attached = (*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_2); \
    if (attached == JNI_EDETACHED) { \
        attached = (*vm)->AttachCurrentThread(vm, &env, NULL); \
        if (attached != JNI_OK || env == NULL) { \
            H2CO3Launcher_INTERNAL_LOG("Failed to attach thread to Android JavaVM."); \
        } \
    } \
    do {} while(0)

#define CallH2CO3LauncherBridgeJNIFunc(return_exp, func_type, func_name, signature, args...) \
    jmethodID H2CO3LauncherBridge_##func_name = (*env)->GetMethodID(env, h2co3Launcher->class_H2CO3LauncherBridge, #func_name, signature); \
    if (H2CO3LauncherBridge_##func_name == NULL) { \
        H2CO3Launcher_INTERNAL_LOG("Failed to find method H2CO3LauncherBridge_"#func_name ); \
    } \
    return_exp (*env)->Call##func_type##Method(env, h2co3Launcher->object_H2CO3LauncherBridge, H2CO3LauncherBridge_##func_name, ##args); \
    do {} while(0)

#endif //H2CO3_LAUNCHER_H2CO3Launcher_INTERNAL_H
