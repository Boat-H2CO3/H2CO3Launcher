package org.koishi.launcher.h2co3.control.keyboard;

import org.koishi.launcher.h2co3.control.GameMenu;
import org.koishi.launcher.h2co3launcher.keycodes.H2CO3LauncherKeycodes;

public class LwjglCharSender implements CharacterSenderStrategy {

    private final GameMenu gameMenu;

    public LwjglCharSender(GameMenu gameMenu) {
        this.gameMenu = gameMenu;
    }

    @Override
    public void sendBackspace() {
        if (gameMenu.getBridge() != null) {
            gameMenu.getBridge().pushEventKey(H2CO3LauncherKeycodes.KEY_BACKSPACE, '\u0008', true);
            gameMenu.getBridge().pushEventKey(H2CO3LauncherKeycodes.KEY_BACKSPACE, '\u0008', false);
        }
    }

    @Override
    public void sendEnter() {
        gameMenu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_ENTER, true);
        gameMenu.getInput().sendKeyEvent(H2CO3LauncherKeycodes.KEY_ENTER, false);
    }

    @Override
    public void sendChar(char character) {
        gameMenu.getInput().sendChar(character);
    }
}
