package org.koishi.launcher.h2co3.activity;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.GameMenu;
import org.koishi.launcher.h2co3.control.JarExecutorMenu;
import org.koishi.launcher.h2co3.control.MenuCallback;
import org.koishi.launcher.h2co3.control.MenuType;
import org.koishi.launcher.h2co3.setting.GameOption;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.keycodes.H2CO3LauncherKeycodes;
import org.koishi.launcher.h2co3launcher.keycodes.LwjglGlfwKeycode;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3library.component.BaseActivity;

import org.lwjgl.glfw.CallbackBridge;

import java.util.Objects;
import java.util.logging.Level;

public class JVMActivity extends BaseActivity implements TextureView.SurfaceTextureListener {

    private static MenuType menuType;
    private static H2CO3LauncherBridge h2co3LauncherBridge;
    private TextureView textureView;
    private MenuCallback menu;
    private boolean isTranslated = false;
    private int output = 0;

    public static void setH2CO3LauncherBridge(H2CO3LauncherBridge h2co3LauncherBridge, MenuType menuType) {
        JVMActivity.h2co3LauncherBridge = h2co3LauncherBridge;
        JVMActivity.menuType = menuType;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jvm);

        showMessageListView();

        if (menuType == null || h2co3LauncherBridge == null) {
            Logging.LOG.log(Level.WARNING, "Failed to get ControllerType or H2CO3LauncherBridge, task canceled.");
            return;
        }

        menu = menuType == MenuType.GAME ? new GameMenu() : new JarExecutorMenu();
        menu.setup(this, h2co3LauncherBridge);
        textureView = findViewById(R.id.texture_view);
        textureView.setSurfaceTextureListener(this);
        if (menuType == MenuType.GAME) {
            menu.getInput().initExternalController(textureView);
        }

        addContentView(menu.getLayout(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (menuType == MenuType.GAME && ((GameMenu) menu).getMenuSetting().isDisableSoftKeyAdjust()) {
                return;
            }
            int screenHeight = getWindow().getDecorView().getHeight();
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            if (screenHeight * 2 / 3 > rect.bottom) {
                textureView.setTranslationY(rect.bottom - screenHeight);
                isTranslated = true;
            } else if (isTranslated) {
                isTranslated = false;
                textureView.setTranslationY(0);
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        Logging.LOG.log(Level.INFO, "surface ready, start jvm now!");
        h2co3LauncherBridge.setSurfaceDestroyed(false);
        int width = menuType == MenuType.GAME ? (int) ((i + ((GameMenu) menu).getMenuSetting().getCursorOffset()) * h2co3LauncherBridge.getScaleFactor()) : H2CO3LauncherBridge.DEFAULT_WIDTH;
        int height = menuType == MenuType.GAME ? (int) (i1 * h2co3LauncherBridge.getScaleFactor()) : H2CO3LauncherBridge.DEFAULT_HEIGHT;
        if (menuType == MenuType.GAME) {
            GameOption gameOption = new GameOption(Objects.requireNonNull(menu.getBridge()).getGameDir());
            gameOption.set("fullscreen", "false");
            gameOption.set("overrideWidth", String.valueOf(width));
            gameOption.set("overrideHeight", String.valueOf(height));
            gameOption.save();
        }
        surfaceTexture.setDefaultBufferSize(width, height);
        h2co3LauncherBridge.execute(new Surface(surfaceTexture), menu.getCallbackBridge());
        h2co3LauncherBridge.setSurfaceTexture(surfaceTexture);
        h2co3LauncherBridge.pushEventWindow(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        int width = menuType == MenuType.GAME ? (int) ((i + ((GameMenu) menu).getMenuSetting().getCursorOffset()) * h2co3LauncherBridge.getScaleFactor()) : H2CO3LauncherBridge.DEFAULT_WIDTH;
        int height = menuType == MenuType.GAME ? (int) (i1 * h2co3LauncherBridge.getScaleFactor()) : H2CO3LauncherBridge.DEFAULT_HEIGHT;
        surfaceTexture.setDefaultBufferSize(width, height);
        h2co3LauncherBridge.pushEventWindow(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        h2co3LauncherBridge.setSurfaceDestroyed(true);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        if (output == 1) {
            menu.onGraphicOutput();
            output++;
        }
        if (output < 1) {
            output++;
        }
    }

    @Override
    protected void onPause() {
        if (menu != null) {
            menu.onPause();
        }
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_HOVERED, 0);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (menu != null) {
            menu.onResume();
        }
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handleEvent = true;
        if (menu != null && menuType == MenuType.GAME) {
            if (!(handleEvent = menu.getInput().handleKeyEvent(event))) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && !((GameMenu) menu).getTouchCharInput().isEnabled()) {
                    if (event.getAction() != KeyEvent.ACTION_UP)
                        return true;
                    menu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_ESC, true);
                    menu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_ESC, false);
                    return true;
                }
            }
        }
        return handleEvent;
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        boolean handleEvent = true;
        if (menu != null && menuType == MenuType.GAME) {
            handleEvent = menu.getInput().handleGenericMotionEvent(event);
        }
        return handleEvent;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (textureView != null && textureView.getSurfaceTexture() != null) {
            textureView.post(() -> onSurfaceTextureSizeChanged(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight()));
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (textureView != null && textureView.getSurfaceTexture() != null) {
            textureView.post(() -> onSurfaceTextureSizeChanged(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight()));
        }
    }
}
