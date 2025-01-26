package org.koishi.launcher.h2co3.control;

public enum MouseMoveMode {
    CLICK(0),
    SLIDE(1);

    private final int id;

    MouseMoveMode(int id) {
        this.id = id;
    }

    public static MouseMoveMode getById(int id) {
        return id == 0 ? CLICK : SLIDE;
    }

    public int getId() {
        return id;
    }
}
