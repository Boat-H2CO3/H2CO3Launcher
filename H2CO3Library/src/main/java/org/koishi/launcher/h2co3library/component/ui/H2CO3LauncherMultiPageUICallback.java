package org.koishi.launcher.h2co3library.component.ui;

import java.util.ArrayList;

public interface H2CO3LauncherMultiPageUICallback {
    void initPages();
    ArrayList<H2CO3LauncherBasePage> getAllPages();
    H2CO3LauncherBasePage getPage(int id);
}
