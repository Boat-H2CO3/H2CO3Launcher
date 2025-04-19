//
// Created by mio on 2024/8/20.
//

#ifndef H2CO3_LAUNCHER_VIRGL_H
#define H2CO3_LAUNCHER_VIRGL_H

#define RENDERER_VIRGL 3

void* virglGetCurrentContext();
void loadSymbolsVirGL();
int virglInit();
void virglSwapBuffers();
void virglMakeCurrent(void* window);
void* virglCreateContext(void* contextSrc);
void virglSwapInterval(int interval);

#endif //H2CO3_LAUNCHER_VIRGL_H
