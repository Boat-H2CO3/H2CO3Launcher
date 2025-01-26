package org.koishi.launcher.h2co3library.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;

public class ThemeUtils {

    public static ThemeUtils instance;
    public boolean initialized;
    public Handler handler;
    public HashMap<View, Runnable> runnables;

    public ThemeUtils() {

    }

    public static ThemeUtils getInstance() {
        if (instance == null) {
            instance = new ThemeUtils();
        }
        return instance;
    }

    public static boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void applyFullscreen(Window window, boolean fullscreen) {
        if (window != null) {
            if (fullscreen) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    window.getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                }
            }
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void registerEvent(View view, Runnable runnable) {
        runnables.put(view, runnable);
        handler.post(runnable);
    }

    public void unregisterEvent(View view) {
        runnables.remove(view);
    }

}
