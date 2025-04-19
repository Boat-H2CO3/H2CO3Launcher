package org.koishi.launcher.h2co3.control.data;

public interface CustomControl {

    ViewType getType();

    String getViewId();

    CustomControl cloneView();
    enum ViewType {
        CONTROL_BUTTON,
        CONTROL_DIRECTION
    }
}
