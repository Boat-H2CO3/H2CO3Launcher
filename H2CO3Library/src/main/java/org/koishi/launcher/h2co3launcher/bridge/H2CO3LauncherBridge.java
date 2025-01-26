package org.koishi.launcher.h2co3launcher.bridge;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import org.koishi.launcher.h2co3launcher.keycodes.H2CO3LauncherKeycodes;
import org.koishi.launcher.h2co3launcher.keycodes.LwjglGlfwKeycode;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;

import org.lwjgl.glfw.CallbackBridge;

import java.io.File;
import java.io.Serializable;

public class H2CO3LauncherBridge implements Serializable {

    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;

    public static final int HIT_RESULT_TYPE_UNKNOWN = 0;
    public static final int HIT_RESULT_TYPE_MISS = 1;
    public static final int HIT_RESULT_TYPE_BLOCK = 2;
    public static final int HIT_RESULT_TYPE_ENTITY = 3;

    public static final int INJECTOR_MODE_ENABLE = 1;
    public static final int INJECTOR_MODE_DISABLE = 0;

    public static final int KeyPress = 2;
    public static final int KeyRelease = 3;
    public static final int ButtonPress = 4;
    public static final int ButtonRelease = 5;
    public static final int MotionNotify = 6;
    public static final int KeyChar = 7;
    public static final int ConfigureNotify = 22;
    public static final int H2CO3LauncherMessage = 37;

    public static final int Button1 = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_1;
    public static final int Button2 = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_2;
    public static final int Button3 = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_3;
    public static final int Button4 = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_4;
    public static final int Button5 = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_5;
    public static final int Button6 = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_6;
    public static final int Button7 = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_7;

    public static final int CursorEnabled = 1;
    public static final int CursorDisabled = 0;

    public static final int ShiftMask = 1 << 0;
    public static final int LockMask = 1 << 1;
    public static final int ControlMask = 1 << 2;
    public static final int Mod1Mask = 1 << 3;
    public static final int Mod2Mask = 1 << 4;
    public static final int Mod3Mask = 1 << 5;
    public static final int Mod4Mask = 1 << 6;
    public static final int Mod5Mask = 1 << 7;

    public static final int CloseRequest = 0;

    public static boolean BACKEND_IS_H2CO3 = false;

    static {
        System.loadLibrary("h2co3Launcher");
        System.loadLibrary("pojavexec_awt");
    }

    private H2CO3LauncherBridgeCallback callback;
    private double scaleFactor = 1f;
    private String controller = "Default";
    private String gameDir;
    private String logPath;
    private String renderer;
    private String java;
    private Surface surface;
    private boolean surfaceDestroyed;
    private Handler handler;
    private Thread thread;
    private SurfaceTexture surfaceTexture;
    private String modSummary;
    private boolean hasTouchController = false;

    public H2CO3LauncherBridge() {
    }

    public static native void nativeClipboardReceived(String data, String mimeTypeSub);

    public static native int nativeGetFps();

    public static void openLink(final String link) {
        Context context = H2CO3LauncherTools.CONTEXT;
        ((Activity) context).runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String targetLink = link;
                if (targetLink.startsWith("file://")) {
                    targetLink = targetLink.replace("file://", "");
                } else if (targetLink.startsWith("file:")) {
                    targetLink = targetLink.replace("file:", "");
                }
                Uri uri;
                if (targetLink.startsWith("http")) {
                    uri = Uri.parse(targetLink);
                } else {
                    //can`t get authority by R.string.file_browser_provider
                    uri = FileProvider.getUriForFile(context, "org.koishi.launcher.h2co3launcher.provider", new File(targetLink));
                }
                intent.setDataAndType(uri, "*/*");
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e("openLink error", "link:" + link + " err:" + e.toString());
            }
        });
    }

    public static void querySystemClipboard() {
        Context context = H2CO3LauncherTools.CONTEXT;
        ClipboardManager clipboard = (ClipboardManager) H2CO3LauncherTools.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        ((Activity) context).runOnUiThread(() -> {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData == null) {
                nativeClipboardReceived(null, null);
                return;
            }
            ClipData.Item firstClipItem = clipData.getItemAt(0);
            //TODO: coerce to HTML if the clip item is styled
            CharSequence clipItemText = firstClipItem.getText();
            if (clipItemText == null) {
                nativeClipboardReceived(null, null);
                return;
            }
            nativeClipboardReceived(clipItemText.toString(), "plain");
        });
    }

    public static void putClipboardData(String data, String mimeType) {
        Context context = H2CO3LauncherTools.CONTEXT;
        ClipboardManager clipboard = (ClipboardManager) H2CO3LauncherTools.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        ((Activity) context).runOnUiThread(() -> {
            ClipData clipData = null;
            switch (mimeType) {
                case "text/plain":
                    clipData = ClipData.newPlainText("AWT Paste", data);
                    break;
                case "text/html":
                    clipData = ClipData.newHtmlText("AWT Paste", data, data);
            }
            if (clipData != null) clipboard.setPrimaryClip(clipData);
        });
    }

    public static int getFps() {
        if (BACKEND_IS_H2CO3) {
            return nativeGetFps();
        } else {
            return CallbackBridge.getFps();
        }
    }

    public native int[] renderAWTScreenFrame();

    public native void nativeSendData(int type, int i1, int i2, int i3, int i4);

    public native void nativeMoveWindow(int x, int y);

    public native int redirectStdio(String path);

    public native int chdir(String path);

    public native void setenv(String key, String value);

    public native int dlopen(String path);

    public native void setLdLibraryPath(String path);

    public native void setupExitTrap(H2CO3LauncherBridge bridge);

    public native void refreshHitResultType();

    public native void setH2CO3LauncherBridge(H2CO3LauncherBridge h2co3LauncherBridge);

    //h2co3 backend
    public native void setH2CO3LauncherNativeWindow(Surface surface);

    public native void setEventPipe();

    public native void pushEvent(long time, int type, int keycode, int keyChar);

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
    }

    public H2CO3LauncherBridgeCallback getCallback() {
        return callback;
    }

    public void execute(Surface surface, H2CO3LauncherBridgeCallback callback) {
        this.handler = new Handler();
        this.callback = callback;
        this.surface = surface;
        setH2CO3LauncherBridge(this);
        CallbackBridge.setH2CO3LauncherBridge(this);
        receiveLog("invoke redirectStdio" + "\n");
        int errorCode = redirectStdio(getLogPath());
        if (errorCode != 0) {
            receiveLog("Can't exec redirectStdio! Error code: " + errorCode + "\n");
        }
        receiveLog("invoke setLogPipeReady" + "\n");
        // set graphic output and event pipe
        if (surface != null) {
            handleWindow();
        }
        receiveLog("invoke setEventPipe" + "\n");
        if (BACKEND_IS_H2CO3) {
            setEventPipe();
        }

        // start
        if (thread != null) {
            thread.start();
        }
    }

    public void pushEventMouseButton(int button, boolean press) {
        if (BACKEND_IS_H2CO3) {
            if (button == Button2) {
                button = Button3;
            } else if (button == Button3) {
                button = Button2;
            }
            pushEvent(System.nanoTime(), press ? ButtonPress : ButtonRelease, button + 1, 0);
        } else {
            switch (button) {
                case Button4:
                    if (press) {
                        CallbackBridge.sendScroll(0, 1d);
                    }
                    break;
                case Button5:
                    if (press) {
                        CallbackBridge.sendScroll(0, -1d);
                    }
                    break;
                default:
                    CallbackBridge.sendMouseButton(button, press);
            }
        }
    }

    public void pushEventPointer(int x, int y) {
        if (BACKEND_IS_H2CO3) {
            pushEvent(System.nanoTime(), MotionNotify, x, y);
        } else {
            CallbackBridge.sendCursorPos(x, y);
        }
    }

    public void pushEventPointer(float x, float y) {
        if (BACKEND_IS_H2CO3) {
            pushEventPointer((int) x, (int) y);
        } else {
            CallbackBridge.sendCursorPos(x, y);
        }
    }

    public void pushEventKey(int keyCode, int keyChar, boolean press) {
        if (BACKEND_IS_H2CO3) {
            pushEvent(System.nanoTime(), press ? KeyPress : KeyRelease, keyCode, keyChar);
        } else {
            CallbackBridge.sendKeycode(keyCode, (char) keyChar, 0, 0, press);
        }
    }

    public void pushEventChar(char keyChar) {
        if (BACKEND_IS_H2CO3) {
            pushEvent(System.nanoTime(), KeyChar, H2CO3LauncherKeycodes.KEY_RESERVED, keyChar);
        } else {
            CallbackBridge.sendChar(keyChar, 0);
        }
    }

    public void pushEventWindow(int width, int height) {
        if (BACKEND_IS_H2CO3) {
            pushEvent(System.nanoTime(), ConfigureNotify, width, height);
        } else {
            CallbackBridge.sendUpdateWindowSize(width, height);
        }
    }

    public void pushEventMessage(int msg) {
        pushEvent(System.nanoTime(), H2CO3LauncherMessage, msg, 0);
    }

    // H2CO3LauncherBridge callbacks
    public void onExit(int code) {
        if (callback != null) {
            callback.onLog("OpenJDK exited with code : " + code + "\n");
            callback.onExit(code);
        }
    }

    public void setHitResultType(int type) {
        if (callback != null) {
            callback.onHitResultTypeChange(type);
        }
    }

    public void setCursorMode(int mode) {
        if (callback != null) {
            callback.onCursorModeChange(mode);
        }
    }

    public String getPrimaryClipString() {
        ClipboardManager clipboard = (ClipboardManager) H2CO3LauncherTools.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        if (!clipboard.hasPrimaryClip()) {
            return null;
        }
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        return item.getText().toString();
    }

    public void setPrimaryClipString(String string) {
        ClipboardManager clipboard = (ClipboardManager) H2CO3LauncherTools.CONTEXT.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("H2CO3Launcher Clipboard", string);
        clipboard.setPrimaryClip(clip);
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    @Nullable
    public String getGameDir() {
        return gameDir;
    }

    public void setGameDir(String gameDir) {
        this.gameDir = gameDir;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public String getJava() {
        return java;
    }

    public void setJava(String java) {
        this.java = java;
    }

    public boolean isSurfaceDestroyed() {
        return surfaceDestroyed;
    }

    public void setSurfaceDestroyed(boolean surfaceDestroyed) {
        this.surfaceDestroyed = surfaceDestroyed;
    }

    @NonNull
    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void receiveLog(String log) {
        if (callback != null) {
            callback.onLog(log);
        }
    }

    private void handleWindow() {
        if (gameDir != null) {
            receiveLog("invoke setH2CO3LauncherNativeWindow" + "\n");
            if (BACKEND_IS_H2CO3) {
                setH2CO3LauncherNativeWindow(surface);
            } else {
                CallbackBridge.setupBridgeWindow(surface);
            }
        } else {
            receiveLog("start Android AWT Renderer thread" + "\n");
            Thread canvasThread = new Thread(() -> {
                Canvas canvas;
                Bitmap rgbArrayBitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
                Paint paint = new Paint();
                try {
                    while (!surfaceDestroyed && surface.isValid()) {
                        canvas = surface.lockCanvas(null);
                        canvas.drawRGB(0, 0, 0);
                        int[] rgbArray = renderAWTScreenFrame();
                        if (rgbArray != null) {
                            canvas.save();
                            rgbArrayBitmap.setPixels(rgbArray, 0, DEFAULT_WIDTH, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                            canvas.drawBitmap(rgbArrayBitmap, 0, 0, paint);
                            canvas.restore();
                        }
                        surface.unlockCanvasAndPost(canvas);
                    }
                } catch (Throwable throwable) {
                    handler.post(() -> receiveLog(throwable + "\n"));
                }
                rgbArrayBitmap.recycle();
                surface.release();
            }, "AndroidAWTRenderer");
            canvasThread.start();
        }
    }

    public String getModSummary() {
        return modSummary;
    }

    public void setModSummary(String modSummary) {
        this.modSummary = modSummary;
    }

    public boolean hasTouchController() {
        return hasTouchController;
    }

    public void setHasTouchController(boolean hasTouchController) {
        this.hasTouchController = hasTouchController;
    }
}
