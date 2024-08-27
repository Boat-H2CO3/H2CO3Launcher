/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.controller.input;

import android.content.Context;

import org.koishi.launcher.h2co3.controller.Controller;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;

public interface Input {
    boolean load(Context context, Controller controller, H2CO3LauncherBridge bridge);

    boolean unload();

    void setGrabCursor(boolean isGrabbed); // 赋值 MARK_INPUT_MODE

    void runConfigure();

    void saveConfig();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void onPaused();

    void onResumed();

    Controller getController();
}
