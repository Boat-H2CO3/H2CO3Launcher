package org.koishi.launcher.h2co3.core.launch;

import dalvik.annotation.optimization.CriticalNative;

public class CriticalNativeTest {
    @CriticalNative
    public static native void testCriticalNative(int arg0, int arg1);
    public static void invokeTest() {
        testCriticalNative(0, 0);
    }
}