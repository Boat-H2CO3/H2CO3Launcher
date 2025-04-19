package org.koishi.launcher.h2co3core.util.png;

public enum PNGFilterType {
    NONE(0),
    SUB(1),
    UP(2),
    AVERAGE(3),
    PAETH(4)
    ;

    final int id;

    PNGFilterType(int id) {
        this.id = id;
    }
}