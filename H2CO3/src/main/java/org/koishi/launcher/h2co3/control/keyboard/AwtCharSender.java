package org.koishi.launcher.h2co3.control.keyboard;

import org.koishi.launcher.h2co3.control.AWTInput;
import org.koishi.launcher.h2co3launcher.keycodes.AWTInputEvent;

public class AwtCharSender implements CharacterSenderStrategy {

    private final AWTInput input;

    public AwtCharSender(AWTInput input) {
        this.input = input;
    }

    @Override
    public void sendBackspace() {
        input.sendKey(' ', AWTInputEvent.VK_BACK_SPACE);
    }

    @Override
    public void sendEnter() {
        input.sendKey(' ', AWTInputEvent.VK_ENTER);
    }

    @Override
    public void sendChar(char character) {
        input.sendChar(character);
    }

}
