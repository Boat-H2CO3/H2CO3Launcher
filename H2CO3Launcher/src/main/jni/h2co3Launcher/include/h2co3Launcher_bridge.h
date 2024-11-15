//
// Created by Tungsten on 2022/10/11.
//

#ifndef H2CO3_LAUNCHER_H2CO3Launcher_BRIDGE_H
#define H2CO3_LAUNCHER_H2CO3Launcher_BRIDGE_H

#include <android/native_window.h>
#include "h2co3Launcher_event.h"

typedef void (* H2CO3LauncherInjectorfun)();

ANativeWindow* h2co3LauncherGetNativeWindow(void);
int h2co3LauncherWaitForEvent(int timeout);
int h2co3LauncherPollEvent(H2CO3LauncherEvent* event);
int h2co3LauncherGetEventFd(void);
void h2co3LauncherSetCursorMode(int mode);

void h2co3LauncherSetInjectorCallback(H2CO3LauncherInjectorfun callback);
void h2co3LauncherSetHitResultType(int type);
void h2co3LauncherSetPrimaryClipString(const char* string);
const char* h2co3LauncherGetPrimaryClipString(void);

#endif //H2CO3_LAUNCHER_H2CO3Launcher_BRIDGE_H
