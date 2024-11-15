package org.koishi.launcher.h2co3.core.launch.keycodes;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mio
 */
public class LwjglKeycodeMap {
    private static final Map<Integer, Integer> KEY_MAP = new HashMap<>();

    private static void add(int lwjglKeycode, int h2co3LauncherKeycode) {
        KEY_MAP.put(h2co3LauncherKeycode, lwjglKeycode);
    }

    public static int convertKeycode(int h2co3LauncherKeycode) {
        Integer key = KEY_MAP.get(h2co3LauncherKeycode);
        if (key != null) {
            return key;
        }
        return LwjglGlfwKeycode.KEY_UNKNOWN;
    }

    static {
        add(LwjglGlfwKeycode.KEY_HOME, H2CO3LauncherKeycodes.KEY_HOME);
        add(LwjglGlfwKeycode.KEY_ESCAPE, H2CO3LauncherKeycodes.KEY_ESC);
        add(LwjglGlfwKeycode.KEY_0, H2CO3LauncherKeycodes.KEY_0);
        add(LwjglGlfwKeycode.KEY_1, H2CO3LauncherKeycodes.KEY_1);
        add(LwjglGlfwKeycode.KEY_2, H2CO3LauncherKeycodes.KEY_2);
        add(LwjglGlfwKeycode.KEY_3, H2CO3LauncherKeycodes.KEY_3);
        add(LwjglGlfwKeycode.KEY_4, H2CO3LauncherKeycodes.KEY_4);
        add(LwjglGlfwKeycode.KEY_5, H2CO3LauncherKeycodes.KEY_5);
        add(LwjglGlfwKeycode.KEY_6, H2CO3LauncherKeycodes.KEY_6);
        add(LwjglGlfwKeycode.KEY_7, H2CO3LauncherKeycodes.KEY_7);
        add(LwjglGlfwKeycode.KEY_8, H2CO3LauncherKeycodes.KEY_8);
        add(LwjglGlfwKeycode.KEY_9, H2CO3LauncherKeycodes.KEY_9);
        add(LwjglGlfwKeycode.KEY_3, H2CO3LauncherKeycodes.KEY_3);
        add(LwjglGlfwKeycode.KEY_UP, H2CO3LauncherKeycodes.KEY_UP);
        add(LwjglGlfwKeycode.KEY_DOWN, H2CO3LauncherKeycodes.KEY_DOWN);
        add(LwjglGlfwKeycode.KEY_LEFT, H2CO3LauncherKeycodes.KEY_LEFT);
        add(LwjglGlfwKeycode.KEY_RIGHT, H2CO3LauncherKeycodes.KEY_RIGHT);
        add(LwjglGlfwKeycode.KEY_A, H2CO3LauncherKeycodes.KEY_A);
        add(LwjglGlfwKeycode.KEY_B, H2CO3LauncherKeycodes.KEY_B);
        add(LwjglGlfwKeycode.KEY_C, H2CO3LauncherKeycodes.KEY_C);
        add(LwjglGlfwKeycode.KEY_D, H2CO3LauncherKeycodes.KEY_D);
        add(LwjglGlfwKeycode.KEY_E, H2CO3LauncherKeycodes.KEY_E);
        add(LwjglGlfwKeycode.KEY_F, H2CO3LauncherKeycodes.KEY_F);
        add(LwjglGlfwKeycode.KEY_G, H2CO3LauncherKeycodes.KEY_G);
        add(LwjglGlfwKeycode.KEY_H, H2CO3LauncherKeycodes.KEY_H);
        add(LwjglGlfwKeycode.KEY_I, H2CO3LauncherKeycodes.KEY_I);
        add(LwjglGlfwKeycode.KEY_J, H2CO3LauncherKeycodes.KEY_J);
        add(LwjglGlfwKeycode.KEY_K, H2CO3LauncherKeycodes.KEY_K);
        add(LwjglGlfwKeycode.KEY_L, H2CO3LauncherKeycodes.KEY_L);
        add(LwjglGlfwKeycode.KEY_M, H2CO3LauncherKeycodes.KEY_M);
        add(LwjglGlfwKeycode.KEY_N, H2CO3LauncherKeycodes.KEY_N);
        add(LwjglGlfwKeycode.KEY_O, H2CO3LauncherKeycodes.KEY_O);
        add(LwjglGlfwKeycode.KEY_P, H2CO3LauncherKeycodes.KEY_P);
        add(LwjglGlfwKeycode.KEY_Q, H2CO3LauncherKeycodes.KEY_Q);
        add(LwjglGlfwKeycode.KEY_R, H2CO3LauncherKeycodes.KEY_R);
        add(LwjglGlfwKeycode.KEY_S, H2CO3LauncherKeycodes.KEY_S);
        add(LwjglGlfwKeycode.KEY_T, H2CO3LauncherKeycodes.KEY_T);
        add(LwjglGlfwKeycode.KEY_U, H2CO3LauncherKeycodes.KEY_U);
        add(LwjglGlfwKeycode.KEY_V, H2CO3LauncherKeycodes.KEY_V);
        add(LwjglGlfwKeycode.KEY_W, H2CO3LauncherKeycodes.KEY_W);
        add(LwjglGlfwKeycode.KEY_X, H2CO3LauncherKeycodes.KEY_X);
        add(LwjglGlfwKeycode.KEY_Y, H2CO3LauncherKeycodes.KEY_Y);
        add(LwjglGlfwKeycode.KEY_Z, H2CO3LauncherKeycodes.KEY_Z);
        add(LwjglGlfwKeycode.KEY_COMMA, H2CO3LauncherKeycodes.KEY_COMMA);
        add(LwjglGlfwKeycode.KEY_PERIOD, H2CO3LauncherKeycodes.KEY_DOT);
        add(LwjglGlfwKeycode.KEY_LEFT_ALT, H2CO3LauncherKeycodes.KEY_LEFTALT);
        add(LwjglGlfwKeycode.KEY_RIGHT_ALT, H2CO3LauncherKeycodes.KEY_RIGHTALT);
        add(LwjglGlfwKeycode.KEY_LEFT_SHIFT, H2CO3LauncherKeycodes.KEY_LEFTSHIFT);
        add(LwjglGlfwKeycode.KEY_RIGHT_SHIFT, H2CO3LauncherKeycodes.KEY_RIGHTSHIFT);
        add(LwjglGlfwKeycode.KEY_TAB, H2CO3LauncherKeycodes.KEY_TAB);
        add(LwjglGlfwKeycode.KEY_SPACE, H2CO3LauncherKeycodes.KEY_SPACE);
        add(LwjglGlfwKeycode.KEY_ENTER, H2CO3LauncherKeycodes.KEY_ENTER);
        add(LwjglGlfwKeycode.KEY_BACKSPACE, H2CO3LauncherKeycodes.KEY_BACKSPACE);
        add(LwjglGlfwKeycode.KEY_GRAVE_ACCENT, H2CO3LauncherKeycodes.KEY_GRAVE);
        add(LwjglGlfwKeycode.KEY_MINUS, H2CO3LauncherKeycodes.KEY_MINUS);
        add(LwjglGlfwKeycode.KEY_EQUAL, H2CO3LauncherKeycodes.KEY_EQUAL);
        add(LwjglGlfwKeycode.KEY_LEFT_BRACKET, H2CO3LauncherKeycodes.KEY_LEFTBRACE);
        add(LwjglGlfwKeycode.KEY_RIGHT_BRACKET, H2CO3LauncherKeycodes.KEY_RIGHTBRACE);
        add(LwjglGlfwKeycode.KEY_BACKSLASH, H2CO3LauncherKeycodes.KEY_BACKSLASH);
        add(LwjglGlfwKeycode.KEY_SEMICOLON, H2CO3LauncherKeycodes.KEY_SEMICOLON);
        add(LwjglGlfwKeycode.KEY_APOSTROPHE, H2CO3LauncherKeycodes.KEY_APOSTROPHE);
        add(LwjglGlfwKeycode.KEY_SLASH, H2CO3LauncherKeycodes.KEY_SLASH);
        add(LwjglGlfwKeycode.KEY_PAGE_UP, H2CO3LauncherKeycodes.KEY_PAGEUP);
        add(LwjglGlfwKeycode.KEY_PAGE_DOWN, H2CO3LauncherKeycodes.KEY_PAGEDOWN);
        add(LwjglGlfwKeycode.KEY_ESCAPE, H2CO3LauncherKeycodes.KEY_ESC);
        add(LwjglGlfwKeycode.KEY_LEFT_CONTROL, H2CO3LauncherKeycodes.KEY_LEFTCTRL);
        add(LwjglGlfwKeycode.KEY_RIGHT_CONTROL, H2CO3LauncherKeycodes.KEY_RIGHTCTRL);
        add(LwjglGlfwKeycode.KEY_CAPS_LOCK, H2CO3LauncherKeycodes.KEY_CAPSLOCK);
        add(LwjglGlfwKeycode.KEY_PAUSE, H2CO3LauncherKeycodes.KEY_PAUSE);
        add(LwjglGlfwKeycode.KEY_END, H2CO3LauncherKeycodes.KEY_END);
        add(LwjglGlfwKeycode.KEY_INSERT, H2CO3LauncherKeycodes.KEY_INSERT);
        add(LwjglGlfwKeycode.KEY_F1, H2CO3LauncherKeycodes.KEY_F1);
        add(LwjglGlfwKeycode.KEY_F2, H2CO3LauncherKeycodes.KEY_F2);
        add(LwjglGlfwKeycode.KEY_F3, H2CO3LauncherKeycodes.KEY_F3);
        add(LwjglGlfwKeycode.KEY_F4, H2CO3LauncherKeycodes.KEY_F4);
        add(LwjglGlfwKeycode.KEY_F5, H2CO3LauncherKeycodes.KEY_F5);
        add(LwjglGlfwKeycode.KEY_F6, H2CO3LauncherKeycodes.KEY_F6);
        add(LwjglGlfwKeycode.KEY_F7, H2CO3LauncherKeycodes.KEY_F7);
        add(LwjglGlfwKeycode.KEY_F8, H2CO3LauncherKeycodes.KEY_F8);
        add(LwjglGlfwKeycode.KEY_F9, H2CO3LauncherKeycodes.KEY_F9);
        add(LwjglGlfwKeycode.KEY_F10, H2CO3LauncherKeycodes.KEY_F10);
        add(LwjglGlfwKeycode.KEY_F11, H2CO3LauncherKeycodes.KEY_F11);
        add(LwjglGlfwKeycode.KEY_F12, H2CO3LauncherKeycodes.KEY_F12);
        add(LwjglGlfwKeycode.KEY_NUM_LOCK, H2CO3LauncherKeycodes.KEY_NUMLOCK);
        add(LwjglGlfwKeycode.KEY_KP_0, H2CO3LauncherKeycodes.KEY_KP0);
        add(LwjglGlfwKeycode.KEY_KP_1, H2CO3LauncherKeycodes.KEY_KP1);
        add(LwjglGlfwKeycode.KEY_KP_2, H2CO3LauncherKeycodes.KEY_KP2);
        add(LwjglGlfwKeycode.KEY_KP_3, H2CO3LauncherKeycodes.KEY_KP3);
        add(LwjglGlfwKeycode.KEY_KP_4, H2CO3LauncherKeycodes.KEY_KP4);
        add(LwjglGlfwKeycode.KEY_KP_5, H2CO3LauncherKeycodes.KEY_KP5);
        add(LwjglGlfwKeycode.KEY_KP_6, H2CO3LauncherKeycodes.KEY_KP6);
        add(LwjglGlfwKeycode.KEY_KP_7, H2CO3LauncherKeycodes.KEY_KP7);
        add(LwjglGlfwKeycode.KEY_KP_8, H2CO3LauncherKeycodes.KEY_KP8);
        add(LwjglGlfwKeycode.KEY_KP_9, H2CO3LauncherKeycodes.KEY_KP9);
        add(LwjglGlfwKeycode.KEY_KP_DECIMAL, H2CO3LauncherKeycodes.KEY_KPDOT);
        add(LwjglGlfwKeycode.KEY_KP_SUBTRACT, H2CO3LauncherKeycodes.KEY_KPMINUS);
        add(LwjglGlfwKeycode.KEY_KP_MULTIPLY, H2CO3LauncherKeycodes.KEY_KPASTERISK);
        add(LwjglGlfwKeycode.KEY_KP_ADD, H2CO3LauncherKeycodes.KEY_KPPLUS);
        add(LwjglGlfwKeycode.KEY_KP_DIVIDE, H2CO3LauncherKeycodes.KEY_KPSLASH);
        add(LwjglGlfwKeycode.KEY_KP_ENTER, H2CO3LauncherKeycodes.KEY_KPENTER);
        add(LwjglGlfwKeycode.KEY_KP_EQUAL, H2CO3LauncherKeycodes.KEY_KPEQUAL);
    }
}