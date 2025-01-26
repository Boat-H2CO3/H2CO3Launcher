package org.koishi.launcher.h2co3.control;

public enum GestureMode {
    BUILD(0),
    FIGHT(1);

    private final int id;

    GestureMode(int id) {
        this.id = id;
    }

    public static GestureMode getById(int id) {
        return id == 0 ? BUILD : FIGHT;
    }

    public int getId() {
        return id;
    }
}
