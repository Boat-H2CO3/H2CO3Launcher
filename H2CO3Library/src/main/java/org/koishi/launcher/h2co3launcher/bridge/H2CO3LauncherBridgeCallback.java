package org.koishi.launcher.h2co3launcher.bridge;

public interface H2CO3LauncherBridgeCallback {

    void onCursorModeChange(int mode);
    void onHitResultTypeChange(int type);
    void onLog(String log);
    void onExit(int code);

}
