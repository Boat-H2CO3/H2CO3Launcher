#include "h2co3Launcher_hook.h"
#include <dlfcn.h>
#include <stdio.h>
#include <string.h>
#include <libgen.h>
#include "h2co3Launcher/include/h2co3Launcher_internal.h"

jint (*orig_ProcessImpl_forkAndExec)(JNIEnv *env, jobject process, jint mode, jbyteArray helperpath,
                                     jbyteArray prog, jbyteArray argBlock, jint argc,
                                     jbyteArray envBlock, jint envc, jbyteArray dir,
                                     jintArray std_fds, jboolean redirectErrorStream);

jint
hooked_ProcessImpl_forkAndExec(JNIEnv *env, jobject process, jint mode, jbyteArray helperpath,
                               jbyteArray prog, jbyteArray argBlock, jint argc, jbyteArray envBlock,
                               jint envc, jbyteArray dir, jintArray std_fds,
                               jboolean redirectErrorStream) {
    char *pProg = (char *) ((*env)->GetByteArrayElements(env, prog, NULL));

    if (strcmp(basename(pProg), "xdg-open") != 0) {
        (*env)->ReleaseByteArrayElements(env, prog, (jbyte *) pProg, 0);
        return orig_ProcessImpl_forkAndExec(env, process, mode, helperpath, prog, argBlock, argc,
                                            envBlock, envc, dir, std_fds, redirectErrorStream);
    }
    (*env)->ReleaseByteArrayElements(env, prog, (jbyte *) pProg, 0);

    long len = (*env)->GetArrayLength(env, argBlock);
    const char *cs[len];
    (*env)->GetByteArrayRegion(env, argBlock, 0, len, (jbyte *) cs);
    (*env)->DeleteLocalRef(env, argBlock);

    H2CO3_INTERNAL_LOG("forkAndExec:%s", cs);
    JavaVM *androidVm = h2co3Launcher->android_jvm;
    JNIEnv *androidEnv = NULL;
    char detachable = 0;
    if ((*androidVm)->GetEnv(androidVm, (void **) &androidEnv, JNI_VERSION_1_2) == JNI_EDETACHED) {
        (*androidVm)->AttachCurrentThread(androidVm, &androidEnv, NULL);
        detachable = 1;
    }
    if (!androidEnv) {
        H2CO3_INTERNAL_LOG("forkAndExec error:androidEnv in null");
    }
    jmethodID method_OpenLink = (*androidEnv)->GetStaticMethodID(androidEnv,
                                                                 h2co3Launcher->class_H2CO3LauncherBridge,
                                                                 "openLink",
                                                                 "(Ljava/lang/String;)V");
    (*androidEnv)->CallStaticVoidMethod(androidEnv, h2co3Launcher->class_H2CO3LauncherBridge,
                                        method_OpenLink,
                                        (*androidEnv)->NewStringUTF(androidEnv, cs));
    if (detachable) (*androidVm)->DetachCurrentThread(androidVm);

    return 0;
}

void hookExec(JNIEnv *env) {
    jclass cls;
    orig_ProcessImpl_forkAndExec = dlsym(RTLD_DEFAULT, "Java_java_lang_UNIXProcess_forkAndExec");
    if (!orig_ProcessImpl_forkAndExec) {
        orig_ProcessImpl_forkAndExec = dlsym(RTLD_DEFAULT,
                                             "Java_java_lang_ProcessImpl_forkAndExec");
        cls = (*env)->FindClass(env, "java/lang/ProcessImpl");
    } else {
        cls = (*env)->FindClass(env, "java/lang/UNIXProcess");
    }
    JNINativeMethod methods[] = {
            {"forkAndExec", "(I[B[B[BI[BI[B[IZ)I", (void *) &hooked_ProcessImpl_forkAndExec}
    };
    (*env)->RegisterNatives(env, cls, methods, 1);
    H2CO3_INTERNAL_LOG("Registered forkAndExec");
}
