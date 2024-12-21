package org.koishi.launcher.h2co3.launcher;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridgeCallback;
import org.koishi.launcher.h2co3.core.launch.keycodes.LwjglGlfwKeycode;
import org.koishi.launcher.h2co3.resources.component.H2CO3TextureView;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.lwjgl.glfw.CallbackBridge;

import java.util.TimerTask;

public abstract class H2CO3LauncherActivity extends H2CO3Activity implements TextureView.SurfaceTextureListener {

    protected static final String TAG = "H2CO3LauncherActivity";
    public H2CO3TextureView mainTextureView;
    public RelativeLayout baseLayout;
    public H2CO3LauncherBridgeCallback h2co3LauncherCallback;
    private int output = 0;
    public H2CO3LauncherBridge launcherLib;
    private TimerTask systemUiTimerTask;
    public static IH2CO3Launcher h2co3LauncherInterface;

    public H2CO3Settings gameHelper;

    private final View.OnSystemUiVisibilityChangeListener onSystemUiVisibilityChangeListener = visibility -> {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            if (systemUiTimerTask != null) {
                systemUiTimerTask.cancel();
            }
            systemUiTimerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> hideSystemUI(getWindow().getDecorView()));
                }
            };
        }
    };

    static {
        System.loadLibrary("h2co3Launcher");
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "SurfaceTexture is available!");
        if (h2co3LauncherCallback != null) {
            h2co3LauncherCallback.onSurfaceTextureAvailable(surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        if (h2co3LauncherCallback != null) {
            h2co3LauncherCallback.onSurfaceTextureSizeChanged(surface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        launcherLib.setSurfaceDestroyed(true);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        if (output < 2) {
            if (output == 1 && h2co3LauncherCallback != null) {
                h2co3LauncherCallback.onPicOutput();
            }
            output++;
        }
    }

    public abstract void onClick(View view);

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            h2co3LauncherCallback.onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight());
        }
    }

    public void setGrabCursor(boolean isGrabbed) {
        runOnUiThread(() -> {
            if (h2co3LauncherInterface != null) {
                h2co3LauncherInterface.setGrabCursor(isGrabbed);
            }
        });
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        RelativeLayout.LayoutParams newParams = (params instanceof RelativeLayout.LayoutParams)
                ? (RelativeLayout.LayoutParams) params
                : new RelativeLayout.LayoutParams(params.width, params.height);
        baseLayout.addView(view, newParams);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return h2co3LauncherInterface != null && h2co3LauncherInterface.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return h2co3LauncherInterface != null && h2co3LauncherInterface.dispatchGenericMotionEvent(event) || super.dispatchGenericMotionEvent(event);
    }

    public int[] getPointer() {
        return launcherLib.getPointer();
    }

    public void setKey(int keyCode, int keyChar, boolean isPressed) {
        launcherLib.pushEventKey(keyCode, keyChar, isPressed);
    }

    public void setMouseButton(int button, boolean isPressed) {
        launcherLib.pushEventMouseButton(button, isPressed);
    }

    public void setPointer(int x, int y) {
        launcherLib.pushEventPointer(x, y);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(hasFocus ? onSystemUiVisibilityChangeListener : null);
        if (hasFocus) {
            hideSystemUI(decorView);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else if (systemUiTimerTask != null) {
            systemUiTimerTask.cancel();
        }
    }

    private void hideSystemUI(View decorView) {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void exit(Context context, int code) {
        // Implement exit logic here
    }

    @Override
    protected void onPause() {
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_HOVERED, 0);
        super.onPause();
    }

    @Override
    protected void onResume() {
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_HOVERED, 1);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 0);
        super.onStop();
    }

    public interface IH2CO3Launcher {
        void onActivityCreate(H2CO3LauncherActivity H2CO3LauncherActivity);
        void setGrabCursor(boolean isGrabbed);
        void onStop();
        void onResume();
        void onPause();
        boolean dispatchKeyEvent(KeyEvent event);
        boolean dispatchGenericMotionEvent(MotionEvent event);
    }
}