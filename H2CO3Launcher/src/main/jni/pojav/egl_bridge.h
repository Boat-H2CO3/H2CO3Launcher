//
// Created by mio on 2024/8/20.
//

#ifndef H2CO3_LAUNCHER_EGL_BRIDGE_H
#define H2CO3_LAUNCHER_EGL_BRIDGE_H

#include <EGL/egl.h>

struct PotatoBridge {

    /* EGLContext */ void* eglContext;
    /* EGLDisplay */ void* eglDisplay;
    /* EGLSurface */ void* eglSurface;
/*
    void* eglSurfaceRead;
    void* eglSurfaceDraw;
*/
};
extern EGLConfig config;
extern struct PotatoBridge potatoBridge;

#endif //H2CO3_LAUNCHER_EGL_BRIDGE_H
