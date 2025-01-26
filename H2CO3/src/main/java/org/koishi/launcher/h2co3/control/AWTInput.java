package org.koishi.launcher.h2co3.control;

import org.koishi.launcher.h2co3.H2CO3LauncherApplication;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;

public class AWTInput {

    public static final int EVENT_TYPE_CHAR = 1000;
    public static final int EVENT_TYPE_CURSOR_POS = 1003;
    public static final int EVENT_TYPE_KEY = 1005;
    public static final int EVENT_TYPE_MOUSE_BUTTON = 1006;

    private final MenuCallback menu;

    private int cursorX;
    private int cursorY;

    public AWTInput(MenuCallback menu) {
        this.menu = menu;
    }

    public void sendKey(char keyChar, int keycode) {
        if (menu.getBridge() == null)
            return;
        menu.getBridge().nativeSendData(EVENT_TYPE_KEY, (int) keyChar, keycode, 1, 0);
        menu.getBridge().nativeSendData(EVENT_TYPE_KEY, (int) keyChar, keycode, 0, 0);
    }

    public void sendKey(char keyChar, int keycode, int state) {
        if (menu.getBridge() == null)
            return;
        menu.getBridge().nativeSendData(EVENT_TYPE_KEY, (int) keyChar, keycode, state, 0);
    }

    public void sendChar(char keyChar) {
        if (menu.getBridge() == null)
            return;
        menu.getBridge().nativeSendData(EVENT_TYPE_CHAR, (int) keyChar, 0, 0, 0);
    }

    public void sendMousePress(int awtButtons, boolean down) {
        if (menu.getBridge() == null)
            return;
        menu.getBridge().nativeSendData(EVENT_TYPE_MOUSE_BUTTON, awtButtons, down ? 1 : 0, 0, 0);
    }

    public void sendMousePress(int awtButtons) {
        sendMousePress(awtButtons, true);
        sendMousePress(awtButtons, false);
    }

    public void sendMousePos(int x, int y) {
        menu.getCursor().setX(x);
        menu.getCursor().setY(y);
        cursorX = x;
        cursorY = y;
        if (menu.getBridge() == null)
            return;
        float xScale = (float) H2CO3LauncherBridge.DEFAULT_WIDTH / (float) AndroidUtils.getScreenWidth(H2CO3LauncherApplication.getCurrentActivity());
        float yScale = (float) H2CO3LauncherBridge.DEFAULT_HEIGHT / (float) AndroidUtils.getScreenHeight(H2CO3LauncherApplication.getCurrentActivity());
        menu.getBridge().nativeSendData(EVENT_TYPE_CURSOR_POS, (int) (x * xScale), (int) (y * yScale), 0, 0);
    }

    public int getCursorX() {
        return cursorX;
    }

    public int getCursorY() {
        return cursorY;
    }
}
