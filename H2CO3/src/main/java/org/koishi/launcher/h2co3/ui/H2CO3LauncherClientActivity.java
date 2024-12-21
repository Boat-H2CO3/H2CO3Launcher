/*
 * //
 * // Created by cainiaohh on 2024-04-04.
 * //
 */

/*
 * //
 * // Created by cainiaohh on 2024-03-31.
 * //
 */

package org.koishi.launcher.h2co3.ui;

import static org.koishi.launcher.h2co3.core.launch.H2CO3BaseLaunch.launchMinecraft;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.controller.H2CO3VirtualController;
import org.koishi.launcher.h2co3.controller.HardwareController;
import org.koishi.launcher.h2co3.controller.client.H2CO3ControlClient;
import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3.core.launch.H2CO3LauncherBridgeCallback;
import org.koishi.launcher.h2co3.core.launch.keycodes.LwjglGlfwKeycode;
import org.koishi.launcher.h2co3.core.launch.utils.MCOptionUtils;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.DisplayUtils;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.launcher.H2CO3LauncherActivity;
import org.koishi.launcher.h2co3.launcher.R;
import org.koishi.launcher.h2co3.resources.component.activity.H2CO3Activity;
import org.lwjgl.glfw.CallbackBridge;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.logging.Level;

/**
 * @author caini
 */
public class H2CO3LauncherClientActivity extends H2CO3LauncherActivity implements H2CO3ControlClient, TextureView.SurfaceTextureListener {

    private static final int CURSOR_SIZE = 16;
    private static final int[] GRABBED_POINTER = new int[]{0, 0};
    public static WeakReference<H2CO3LauncherBridge.LogReceiver> logReceiver;
    private boolean grabbed = false;
    private ImageView cursorIcon;
    private int screenWidth;
    private int screenHeight;
    private int output = 0;

    public static void receiveLog(String str) throws IOException {
        H2CO3LauncherBridge.LogReceiver receiver = logReceiver != null ? logReceiver.get() : null;
        if (receiver == null) {
            receiver = new H2CO3LauncherBridge.LogReceiver() {
                final StringBuilder builder = new StringBuilder();

                @Override
                public void pushLog(String log) {
                    builder.append(log);
                }

                @Override
                public String getLogs() {
                    return builder.toString();
                }
            };
            logReceiver = new WeakReference<>(receiver);
        }
        receiver.pushLog(str);
    }

    public static void attachControllerInterface() {
        H2CO3LauncherClientActivity.h2co3LauncherInterface = new IH2CO3Launcher() {
            private H2CO3VirtualController virtualController;
            private HardwareController hardwareController;

            @Override
            public void onActivityCreate(H2CO3LauncherActivity activity) {
                virtualController = new H2CO3VirtualController((H2CO3ControlClient) activity, activity.launcherLib, KEYMAP_TO_X);
                hardwareController = new HardwareController((H2CO3ControlClient) activity, activity.launcherLib, KEYMAP_TO_X);
                CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 1);
            }

            @Override
            public void setGrabCursor(boolean isGrabbed) {
                virtualController.setGrabCursor(isGrabbed);
                hardwareController.setGrabCursor(isGrabbed);
            }

            @Override
            public void onStop() {
                virtualController.onStop();
                hardwareController.onStop();
                CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 0);
            }

            @Override
            public void onResume() {
                virtualController.onResumed();
                hardwareController.onResumed();
                CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_HOVERED, 1);
            }

            @Override
            public void onPause() {
                virtualController.onPaused();
                hardwareController.onPaused();
                CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_HOVERED, 0);
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                return hardwareController.dispatchKeyEvent(event);
            }

            @Override
            public boolean dispatchGenericMotionEvent(MotionEvent event) {
                return hardwareController.dispatchMotionKeyEvent(event);
            }
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay);
        showMessageListView();
        gameHelper = new H2CO3Settings();
        mainTextureView = findViewById(R.id.main_game_render_view);
        mainTextureView.setSurfaceTextureListener(this);
        baseLayout = findViewById(R.id.main_base);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        cursorIcon = new ImageView(this);
        cursorIcon.setLayoutParams(new ViewGroup.LayoutParams(DisplayUtils.getPxFromDp(this, CURSOR_SIZE), DisplayUtils.getPxFromDp(this, CURSOR_SIZE)));
        cursorIcon.setImageResource(org.koishi.launcher.h2co3.library.R.drawable.cursor5);
        this.addView(cursorIcon);
        try {
            launcherLib = launchMinecraft(this, gameHelper, screenWidth, screenHeight);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        h2co3LauncherCallback = launcherLib.getCallback();
        init();
    }

    private void init() {
        h2co3LauncherInterface.onActivityCreate(this);
        h2co3LauncherCallback = new H2CO3LauncherBridgeCallback() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                configureSurfaceTexture(surface, width, height);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // No operation
            }

            @Override
            public void onCursorModeChange(int mode) {
                runOnUiThread(() -> setGrabCursor(mode == H2CO3LauncherBridge.CursorEnabled));
            }

            @Override
            public void onLog(String log) throws IOException {
                if (!log.contains("OR:") && !log.contains("ERROR:") && !log.contains("INTERNAL ERROR:")) {
                    receiveLog(log);
                }
            }

            @Override
            public void onStart() {
                CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 1);
            }

            @Override
            public void onPicOutput() {
                // No operation
            }

            @Override
            public void onError(Exception e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
            }

            @Override
            public void onExit(int code) {
                ExitActivity.showExitMessage(H2CO3LauncherClientActivity.this, code);
            }

            @Override
            public void onHitResultTypeChange(int type) {
                // No operation
            }
        };
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
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 1);
    }

    @Override
    protected void onStop() {
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 0);
        super.onStop();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        surfaceTexture.setDefaultBufferSize(width, height);
        launcherLib.pushEventWindow(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        launcherLib.setSurfaceDestroyed(true);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        if (mainTextureView != null && mainTextureView.getSurfaceTexture() != null) {
            mainTextureView.post(() -> onSurfaceTextureSizeChanged(mainTextureView.getSurfaceTexture(), mainTextureView.getWidth(), mainTextureView.getHeight()));
        }
        if (output < 1) {
            output++;
        }
    }

    private void configureSurfaceTexture(SurfaceTexture surface, int width, int height) {
        int scaleFactor = 1;
        surface.setDefaultBufferSize(width * scaleFactor, height * scaleFactor);
        MCOptionUtils.load(gameHelper.getGameDirectory());
        MCOptionUtils.set("overrideWidth", String.valueOf(width * scaleFactor));
        MCOptionUtils.set("overrideHeight", String.valueOf(height * scaleFactor));
        MCOptionUtils.set("fullscreen", "true");
        MCOptionUtils.save(gameHelper.getGameDirectory());
    }

    @Override
    public void onClick(View p1) {
        // No operation
    }

    @Override
    public void exit(Context context, int code) {
        super.exit(context, code);
        ExitActivity.showExitMessage(context, code);
    }

    @Override
    public void setKey(int keyCode, boolean pressed) {
        this.setKey(keyCode, 0, pressed);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        Logging.LOG.log(Level.INFO, "surface ready, start jvm now!");
        launcherLib.setSurfaceDestroyed(false);
        configureSurfaceTexture(surfaceTexture, width, height);
        try {
            launcherLib.execute(new Surface(surfaceTexture), h2co3LauncherCallback);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        launcherLib.pushEventWindow(width, height);
    }

    @Override
    public void setPointerInc(int xInc, int yInc) {
        int x = grabbed ? getPointer()[0] + xInc : Math.min(Math.max(GRABBED_POINTER[0] + xInc, 0), screenWidth);
        int y = grabbed ? getPointer()[1] + yInc : Math.min(Math.max(GRABBED_POINTER[1] + yInc, 0), screenHeight);
        setPointer(x, y);
    }

    @Override
    public void setPointer(int x, int y) {
        super.setPointer(x, y);
        if (!grabbed) {
            cursorIcon.setX(x);
            cursorIcon.setY(y);
            GRABBED_POINTER[0] = x;
            GRABBED_POINTER[1] = y;
        }
    }

    @Override
    public void addView(View v) {
        this.addContentView(v, v.getLayoutParams());
    }

    @Override
    public H2CO3Activity getActivity() {
        return this;
    }

    @Override
    public void typeWords(String str) {
        if (str != null && !str.isEmpty()) {
            for (char c : str.toCharArray()) {
                setKey(0, c, true);
                setKey(0, c, false);
            }
        }
    }

    @Override
    public int[] getLoosenPointer() {
        return getPointer().clone();
    }

    @Override
    public ViewGroup getViewsParent() {
        return (ViewGroup) findViewById(android.R.id.content).getRootView();
    }

    @Override
    public View getSurfaceLayerView() {
        return mainTextureView;
    }

    @Override
    public boolean isGrabbed() {
        return grabbed;
    }

    @Override
    public int[] getGrabbedPointer() {
        return GRABBED_POINTER.clone();
    }

    @Override
    public void setGrabCursor(boolean isGrabbed) {
        super.setGrabCursor(isGrabbed);
        grabbed = isGrabbed;
        cursorIcon.setVisibility(isGrabbed ? View.INVISIBLE : View.VISIBLE);
        if (!isGrabbed) {
            setPointer(GRABBED_POINTER[0], GRABBED_POINTER[1]);
        }
    }
}