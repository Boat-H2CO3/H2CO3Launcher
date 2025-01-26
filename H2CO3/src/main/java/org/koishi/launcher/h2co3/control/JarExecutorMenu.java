package org.koishi.launcher.h2co3.control;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.BuildConfig;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.JVMCrashActivity;
import org.koishi.launcher.h2co3.control.keyboard.AwtCharSender;
import org.koishi.launcher.h2co3.control.keyboard.TouchCharInput;
import org.koishi.launcher.h2co3.control.view.LogWindow;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridgeCallback;
import org.koishi.launcher.h2co3launcher.keycodes.AWTInputEvent;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3library.component.BaseActivity;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageView;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class JarExecutorMenu implements MenuCallback, View.OnClickListener, View.OnTouchListener {

    private BaseActivity activity;
    private H2CO3LauncherBridge h2co3LauncherBridge;
    private AWTInput awtInput;

    private View layout;
    private View touchPad;
    private LogWindow logWindow;
    private H2CO3LauncherImageView cursorView;
    private TouchCharInput touchCharInput;
    private H2CO3LauncherButton forceExit;
    private H2CO3LauncherButton showLog;
    private H2CO3LauncherButton mouseMode;
    private H2CO3LauncherButton input;
    private H2CO3LauncherButton copy;
    private H2CO3LauncherButton paste;
    private H2CO3LauncherButton mouseLeft;
    private H2CO3LauncherButton mouseRight;
    private H2CO3LauncherButton moveUp;
    private H2CO3LauncherButton moveDown;
    private H2CO3LauncherButton moveLeft;
    private H2CO3LauncherButton moveRight;

    private boolean clickMode = true;
    private int downX;
    private int downY;
    private long downTime;
    private int initialX;
    private int initialY;
    private boolean firstLog = true;

    @Override
    public void setup(BaseActivity activity, H2CO3LauncherBridge h2co3LauncherBridge) {
        this.activity = activity;
        this.h2co3LauncherBridge = h2co3LauncherBridge;

        this.awtInput = new AWTInput(this);

        touchPad = findViewById(R.id.touch_pad);
        logWindow = findViewById(R.id.log_window);
        cursorView = findViewById(R.id.cursor);
        touchCharInput = findViewById(R.id.input_scanner);
        touchPad.setOnTouchListener(this);
        logWindow.setVisibilityValue(true);
        touchCharInput.setCharacterSender(null, new AwtCharSender(awtInput));

        forceExit = findViewById(R.id.force_exit);
        showLog = findViewById(R.id.show_log);
        mouseMode = findViewById(R.id.mouse_mode);
        input = findViewById(R.id.input);
        copy = findViewById(R.id.copy);
        paste = findViewById(R.id.paste);
        mouseLeft = findViewById(R.id.mouse_left);
        mouseRight = findViewById(R.id.mouse_right);
        moveUp = findViewById(R.id.move_up);
        moveDown = findViewById(R.id.move_down);
        moveLeft = findViewById(R.id.move_left);
        moveRight = findViewById(R.id.move_right);
        forceExit.setOnClickListener(this);
        showLog.setOnClickListener(this);
        mouseMode.setOnClickListener(this);
        input.setOnClickListener(this);
        copy.setOnClickListener(this);
        paste.setOnClickListener(this);
        mouseLeft.setOnClickListener(this);
        mouseRight.setOnClickListener(this);
        moveUp.setOnClickListener(this);
        moveDown.setOnClickListener(this);
        moveLeft.setOnClickListener(this);
        moveRight.setOnClickListener(this);
    }

    @Override
    public View getLayout() {
        if (layout == null) {
            layout = LayoutInflater.from(activity).inflate(R.layout.view_jar_executor_menu, null);
        }
        return layout;
    }

    @Override
    @Nullable
    public H2CO3LauncherBridge getBridge() {
        return h2co3LauncherBridge;
    }

    @Override
    public H2CO3LauncherBridgeCallback getCallbackBridge() {
        return new JarExecutorProcessListener(this);
    }

    @Override
    public H2CO3LauncherInput getInput() {
        // Ignore
        return null;
    }

    @Override
    public H2CO3LauncherImageView getCursor() {
        return cursorView;
    }

    @Override
    public int getCursorMode() {
        // Ignore
        return 0;
    }

    @Override
    public void onPause() {
        // Ignore
    }

    @Override
    public void onResume() {
        // Ignore
    }

    @Override
    public void onGraphicOutput() {
        // Ignore
    }

    @Override
    public void onCursorModeChange(int mode) {
        // Ignore
    }

    @Override
    public void onLog(String log) {
        if (log.contains("OR:") || log.contains("ERROR:") || log.contains("INTERNAL ERROR:")) {
            return;
        }
        logWindow.appendLog(log + "\n");
        if (BuildConfig.DEBUG) {
            Log.d("H2CO3Launcher Debug", log);
        }
        try {
            if (firstLog) {
                FileUtils.writeText(new File(h2co3LauncherBridge.getLogPath()), log + "\n");
                firstLog = false;
            } else {
                FileUtils.writeTextWithAppendMode(new File(h2co3LauncherBridge.getLogPath()), log + "\n");
            }
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Can't log jar executor log to target file", e.getMessage());
        }
    }

    @Override
    public void onExit(int exitCode) {
        if (exitCode != 0) {
            JVMCrashActivity.startCrashActivity(false, activity, exitCode, h2co3LauncherBridge.getLogPath(), h2co3LauncherBridge.getRenderer(), h2co3LauncherBridge.getJava());
            Logging.LOG.log(Level.INFO, "JVM crashed, start jvm crash activity to show errors now!");
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onClick(View view) {
        if (view == forceExit) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(activity);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setMessage(activity.getString(R.string.menu_settings_force_exit_msg));
            builder.setPositiveButton(() -> android.os.Process.killProcess(android.os.Process.myPid()));
            builder.setNegativeButton(null);
            builder.setCancelable(false);
            builder.create().show();
        }
        if (view == showLog) {
            logWindow.setVisibilityValue(!logWindow.getVisibilityValue());
        }
        if (view == mouseMode) {
            clickMode = !clickMode;
            Toast.makeText(activity, clickMode ? activity.getString(R.string.menu_settings_mouse_mode_click) : activity.getString(R.string.menu_settings_mouse_mode_slide), Toast.LENGTH_SHORT).show();
        }
        if (view == input) {
            touchCharInput.switchKeyboardState();
        }
        if (view == copy) {
            awtInput.sendKey(' ', AWTInputEvent.VK_CONTROL, 1);
            awtInput.sendKey(' ', AWTInputEvent.VK_C);
            awtInput.sendKey(' ', AWTInputEvent.VK_CONTROL, 0);
        }
        if (view == paste) {
            awtInput.sendKey(' ', AWTInputEvent.VK_CONTROL, 1);
            awtInput.sendKey(' ', AWTInputEvent.VK_V);
            awtInput.sendKey(' ', AWTInputEvent.VK_CONTROL, 0);
        }
        if (view == mouseLeft) {
            awtInput.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
        }
        if (view == mouseRight) {
            awtInput.sendMousePress(AWTInputEvent.BUTTON3_DOWN_MASK);
        }
        if (view == moveUp) {
            h2co3LauncherBridge.nativeMoveWindow(0, -10);
        }
        if (view == moveDown) {
            h2co3LauncherBridge.nativeMoveWindow(0, 10);
        }
        if (view == moveLeft) {
            h2co3LauncherBridge.nativeMoveWindow(-10, 0);
        }
        if (view == moveRight) {
            h2co3LauncherBridge.nativeMoveWindow(10, 0);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view == touchPad) {
            if (clickMode) {
                awtInput.sendMousePos((int) motionEvent.getX(), (int) motionEvent.getY());
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        awtInput.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK, true);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        awtInput.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK, false);
                        break;
                    default:
                        break;
                }
            } else {
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = (int) motionEvent.getX();
                        downY = (int) motionEvent.getY();
                        downTime = System.currentTimeMillis();
                        initialX = awtInput.getCursorX();
                        initialY = awtInput.getCursorY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int deltaX = (int) (motionEvent.getX() - downX);
                        int deltaY = (int) (motionEvent.getY() - downY);
                        int targetX = Math.max(0, Math.min(touchPad.getMeasuredWidth(), initialX + deltaX));
                        int targetY = Math.max(0, Math.min(touchPad.getMeasuredHeight(), initialY + deltaY));
                        awtInput.sendMousePos(targetX, targetY);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - downTime <= 100
                                && Math.abs(motionEvent.getX() - downX) <= 10
                                && Math.abs(motionEvent.getY() - downY) <= 10) {
                            awtInput.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }

    @NonNull
    public final <T extends View> T findViewById(int id) {
        return getLayout().findViewById(id);
    }

    static class JarExecutorProcessListener implements H2CO3LauncherBridgeCallback {

        private final JarExecutorMenu menu;

        public JarExecutorProcessListener(JarExecutorMenu menu) {
            this.menu = menu;
        }

        @Override
        public void onCursorModeChange(int mode) {
            menu.onCursorModeChange(mode);
        }

        @Override
        public void onHitResultTypeChange(int type) {
            // Ignore
        }

        @Override
        public void onLog(String log) {
            menu.onLog(log);
        }

        @Override
        public void onExit(int code) {
            menu.onExit(code);
        }
    }
}
