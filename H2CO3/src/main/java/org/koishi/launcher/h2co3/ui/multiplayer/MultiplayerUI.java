package org.koishi.launcher.h2co3.ui.multiplayer;

import android.content.Context;

import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonUI;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public class MultiplayerUI extends H2CO3LauncherCommonUI {
    public MultiplayerUI(Context context, H2CO3LauncherUILayout parent, int id) {
        super(context, parent, id);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }
}
