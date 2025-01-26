package org.koishi.launcher.h2co3.control;

import android.view.View;

import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridgeCallback;
import org.koishi.launcher.h2co3library.component.BaseActivity;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;

public interface MenuCallback {

    void setup(BaseActivity activity, H2CO3LauncherBridge h2co3LauncherBridge);

    View getLayout();

    @Nullable
    H2CO3LauncherBridge getBridge();

    H2CO3LauncherBridgeCallback getCallbackBridge();

    H2CO3LauncherInput getInput();

    H2CO3LauncherImageView getCursor();

    int getCursorMode();

    void onPause();

    void onResume();

    void onGraphicOutput();

    void onCursorModeChange(int mode);

    void onLog(String log);

    void onExit(int exitCode);

}
