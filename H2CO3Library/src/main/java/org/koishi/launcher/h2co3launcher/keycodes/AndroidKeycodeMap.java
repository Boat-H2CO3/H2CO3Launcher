package org.koishi.launcher.h2co3launcher.keycodes;

import android.view.KeyEvent;

import java.util.Arrays;

public class AndroidKeycodeMap {

    private static final int[] ANDROID_KEYCODES = new int[99];
    private static final int[] H2CO3LAUNCHER_KEYCODES = new int[99];

    private static int count = 0;

    static {
        add(KeyEvent.KEYCODE_HOME,                           H2CO3LauncherKeycodes.KEY_HOME);
        add(KeyEvent.KEYCODE_BACK,                           H2CO3LauncherKeycodes.KEY_ESC);

        add(KeyEvent.KEYCODE_0,                              H2CO3LauncherKeycodes.KEY_0);
        add(KeyEvent.KEYCODE_1,                              H2CO3LauncherKeycodes.KEY_1);
        add(KeyEvent.KEYCODE_2,                              H2CO3LauncherKeycodes.KEY_2);
        add(KeyEvent.KEYCODE_3,                              H2CO3LauncherKeycodes.KEY_3);
        add(KeyEvent.KEYCODE_4,                              H2CO3LauncherKeycodes.KEY_4);
        add(KeyEvent.KEYCODE_5,                              H2CO3LauncherKeycodes.KEY_5);
        add(KeyEvent.KEYCODE_6,                              H2CO3LauncherKeycodes.KEY_6);
        add(KeyEvent.KEYCODE_7,                              H2CO3LauncherKeycodes.KEY_7);
        add(KeyEvent.KEYCODE_8,                              H2CO3LauncherKeycodes.KEY_8);
        add(KeyEvent.KEYCODE_9,                              H2CO3LauncherKeycodes.KEY_9);

        add(KeyEvent.KEYCODE_POUND,                          H2CO3LauncherKeycodes.KEY_3);

        add(KeyEvent.KEYCODE_DPAD_UP,                        H2CO3LauncherKeycodes.KEY_UP);
        add(KeyEvent.KEYCODE_DPAD_DOWN,                      H2CO3LauncherKeycodes.KEY_DOWN);
        add(KeyEvent.KEYCODE_DPAD_LEFT,                      H2CO3LauncherKeycodes.KEY_LEFT);
        add(KeyEvent.KEYCODE_DPAD_RIGHT,                     H2CO3LauncherKeycodes.KEY_RIGHT);

        add(KeyEvent.KEYCODE_A,                              H2CO3LauncherKeycodes.KEY_A);
        add(KeyEvent.KEYCODE_B,                              H2CO3LauncherKeycodes.KEY_B);
        add(KeyEvent.KEYCODE_C,                              H2CO3LauncherKeycodes.KEY_C);
        add(KeyEvent.KEYCODE_D,                              H2CO3LauncherKeycodes.KEY_D);
        add(KeyEvent.KEYCODE_E,                              H2CO3LauncherKeycodes.KEY_E);
        add(KeyEvent.KEYCODE_F,                              H2CO3LauncherKeycodes.KEY_F);
        add(KeyEvent.KEYCODE_G,                              H2CO3LauncherKeycodes.KEY_G);
        add(KeyEvent.KEYCODE_H,                              H2CO3LauncherKeycodes.KEY_H);
        add(KeyEvent.KEYCODE_I,                              H2CO3LauncherKeycodes.KEY_I);
        add(KeyEvent.KEYCODE_J,                              H2CO3LauncherKeycodes.KEY_J);
        add(KeyEvent.KEYCODE_K,                              H2CO3LauncherKeycodes.KEY_K);
        add(KeyEvent.KEYCODE_L,                              H2CO3LauncherKeycodes.KEY_L);
        add(KeyEvent.KEYCODE_M,                              H2CO3LauncherKeycodes.KEY_M);
        add(KeyEvent.KEYCODE_N,                              H2CO3LauncherKeycodes.KEY_N);
        add(KeyEvent.KEYCODE_O,                              H2CO3LauncherKeycodes.KEY_O);
        add(KeyEvent.KEYCODE_P,                              H2CO3LauncherKeycodes.KEY_P);
        add(KeyEvent.KEYCODE_Q,                              H2CO3LauncherKeycodes.KEY_Q);
        add(KeyEvent.KEYCODE_R,                              H2CO3LauncherKeycodes.KEY_R);
        add(KeyEvent.KEYCODE_S,                              H2CO3LauncherKeycodes.KEY_S);
        add(KeyEvent.KEYCODE_T,                              H2CO3LauncherKeycodes.KEY_T);
        add(KeyEvent.KEYCODE_U,                              H2CO3LauncherKeycodes.KEY_U);
        add(KeyEvent.KEYCODE_V,                              H2CO3LauncherKeycodes.KEY_V);
        add(KeyEvent.KEYCODE_W,                              H2CO3LauncherKeycodes.KEY_W);
        add(KeyEvent.KEYCODE_X,                              H2CO3LauncherKeycodes.KEY_X);
        add(KeyEvent.KEYCODE_Y,                              H2CO3LauncherKeycodes.KEY_Y);
        add(KeyEvent.KEYCODE_Z,                              H2CO3LauncherKeycodes.KEY_Z);

        add(KeyEvent.KEYCODE_COMMA,                          H2CO3LauncherKeycodes.KEY_COMMA);
        add(KeyEvent.KEYCODE_PERIOD,                         H2CO3LauncherKeycodes.KEY_DOT);

        add(KeyEvent.KEYCODE_ALT_LEFT,                       H2CO3LauncherKeycodes.KEY_LEFTALT);
        add(KeyEvent.KEYCODE_ALT_RIGHT,                      H2CO3LauncherKeycodes.KEY_RIGHTALT);

        add(KeyEvent.KEYCODE_SHIFT_LEFT,                     H2CO3LauncherKeycodes.KEY_LEFTSHIFT);
        add(KeyEvent.KEYCODE_SHIFT_RIGHT,                    H2CO3LauncherKeycodes.KEY_RIGHTSHIFT);

        add(KeyEvent.KEYCODE_TAB,                            H2CO3LauncherKeycodes.KEY_TAB);
        add(KeyEvent.KEYCODE_SPACE,                          H2CO3LauncherKeycodes.KEY_SPACE);
        add(KeyEvent.KEYCODE_ENTER,                          H2CO3LauncherKeycodes.KEY_ENTER);
        add(KeyEvent.KEYCODE_DEL,                            H2CO3LauncherKeycodes.KEY_BACKSPACE);
        add(KeyEvent.KEYCODE_GRAVE,                          H2CO3LauncherKeycodes.KEY_GRAVE);
        add(KeyEvent.KEYCODE_MINUS,                          H2CO3LauncherKeycodes.KEY_MINUS);
        add(KeyEvent.KEYCODE_EQUALS,                         H2CO3LauncherKeycodes.KEY_EQUAL);
        add(KeyEvent.KEYCODE_LEFT_BRACKET,                   H2CO3LauncherKeycodes.KEY_LEFTBRACE);
        add(KeyEvent.KEYCODE_RIGHT_BRACKET,                  H2CO3LauncherKeycodes.KEY_RIGHTBRACE);
        add(KeyEvent.KEYCODE_BACKSLASH,                      H2CO3LauncherKeycodes.KEY_BACKSLASH);
        add(KeyEvent.KEYCODE_SEMICOLON,                      H2CO3LauncherKeycodes.KEY_SEMICOLON);
        add(KeyEvent.KEYCODE_APOSTROPHE,                     H2CO3LauncherKeycodes.KEY_APOSTROPHE);
        add(KeyEvent.KEYCODE_SLASH,                          H2CO3LauncherKeycodes.KEY_SLASH);
        add(KeyEvent.KEYCODE_AT,                             H2CO3LauncherKeycodes.KEY_2);

        add(KeyEvent.KEYCODE_PAGE_UP,                        H2CO3LauncherKeycodes.KEY_PAGEUP);
        add(KeyEvent.KEYCODE_PAGE_DOWN,                      H2CO3LauncherKeycodes.KEY_PAGEDOWN);

        add(KeyEvent.KEYCODE_ESCAPE,                         H2CO3LauncherKeycodes.KEY_ESC);

        add(KeyEvent.KEYCODE_CTRL_LEFT,                      H2CO3LauncherKeycodes.KEY_LEFTCTRL);
        add(KeyEvent.KEYCODE_CTRL_RIGHT,                     H2CO3LauncherKeycodes.KEY_RIGHTCTRL);

        add(KeyEvent.KEYCODE_CAPS_LOCK,                      H2CO3LauncherKeycodes.KEY_CAPSLOCK);
        add(KeyEvent.KEYCODE_BREAK,                          H2CO3LauncherKeycodes.KEY_PAUSE);
        add(KeyEvent.KEYCODE_MOVE_END,                       H2CO3LauncherKeycodes.KEY_END);
        add(KeyEvent.KEYCODE_INSERT,                         H2CO3LauncherKeycodes.KEY_INSERT);

        add(KeyEvent.KEYCODE_F1,                             H2CO3LauncherKeycodes.KEY_F1);
        add(KeyEvent.KEYCODE_F2,                             H2CO3LauncherKeycodes.KEY_F2);
        add(KeyEvent.KEYCODE_F3,                             H2CO3LauncherKeycodes.KEY_F3);
        add(KeyEvent.KEYCODE_F4,                             H2CO3LauncherKeycodes.KEY_F4);
        add(KeyEvent.KEYCODE_F5,                             H2CO3LauncherKeycodes.KEY_F5);
        add(KeyEvent.KEYCODE_F6,                             H2CO3LauncherKeycodes.KEY_F6);
        add(KeyEvent.KEYCODE_F7,                             H2CO3LauncherKeycodes.KEY_F7);
        add(KeyEvent.KEYCODE_F8,                             H2CO3LauncherKeycodes.KEY_F8);
        add(KeyEvent.KEYCODE_F9,                             H2CO3LauncherKeycodes.KEY_F9);
        add(KeyEvent.KEYCODE_F10,                            H2CO3LauncherKeycodes.KEY_F10);
        add(KeyEvent.KEYCODE_F11,                            H2CO3LauncherKeycodes.KEY_F11);
        add(KeyEvent.KEYCODE_F12,                            H2CO3LauncherKeycodes.KEY_F12);

        add(KeyEvent.KEYCODE_NUM_LOCK,                       H2CO3LauncherKeycodes.KEY_NUMLOCK);
        add(KeyEvent.KEYCODE_NUMPAD_0,                       H2CO3LauncherKeycodes.KEY_KP0);
        add(KeyEvent.KEYCODE_NUMPAD_1,                       H2CO3LauncherKeycodes.KEY_KP1);
        add(KeyEvent.KEYCODE_NUMPAD_2,                       H2CO3LauncherKeycodes.KEY_KP2);
        add(KeyEvent.KEYCODE_NUMPAD_3,                       H2CO3LauncherKeycodes.KEY_KP3);
        add(KeyEvent.KEYCODE_NUMPAD_4,                       H2CO3LauncherKeycodes.KEY_KP4);
        add(KeyEvent.KEYCODE_NUMPAD_5,                       H2CO3LauncherKeycodes.KEY_KP5);
        add(KeyEvent.KEYCODE_NUMPAD_6,                       H2CO3LauncherKeycodes.KEY_KP6);
        add(KeyEvent.KEYCODE_NUMPAD_7,                       H2CO3LauncherKeycodes.KEY_KP7);
        add(KeyEvent.KEYCODE_NUMPAD_8,                       H2CO3LauncherKeycodes.KEY_KP8);
        add(KeyEvent.KEYCODE_NUMPAD_9,                       H2CO3LauncherKeycodes.KEY_KP9);
        add(KeyEvent.KEYCODE_NUMPAD_DOT,                     H2CO3LauncherKeycodes.KEY_KPDOT);
        add(KeyEvent.KEYCODE_NUMPAD_COMMA,                   H2CO3LauncherKeycodes.KEY_KPCOMMA);
        add(KeyEvent.KEYCODE_NUMPAD_ENTER,                   H2CO3LauncherKeycodes.KEY_KPENTER);
        add(KeyEvent.KEYCODE_NUMPAD_EQUALS,                  H2CO3LauncherKeycodes.KEY_KPEQUAL);
    }

    private static void add(int androidKeycode, int h2co3LauncherKeycode) {
        ANDROID_KEYCODES[count] = androidKeycode;
        H2CO3LAUNCHER_KEYCODES[count] = h2co3LauncherKeycode;
        count++;
    }

    public static int convertKeycode(int androidKeycode) {
        int index = Arrays.binarySearch(ANDROID_KEYCODES, androidKeycode);
        if (index >= 0)
            return H2CO3LAUNCHER_KEYCODES[index];
        return H2CO3LauncherKeycodes.KEY_UNKNOWN;
    }

}
