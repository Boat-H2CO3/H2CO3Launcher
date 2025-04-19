package org.koishi.launcher.h2co3.control.view;

import org.koishi.launcher.h2co3.control.data.CustomControl;

public interface CustomView {
    CustomControl.ViewType getType();
    String getViewId();
    void switchParentVisibility();
    void removeListener();
}
