package org.koishi.launcher.h2co3library.component.ui;

import android.content.Context;

import androidx.annotation.LayoutRes;

import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.util.ArrayList;

public abstract class H2CO3LauncherMultiPageUI extends H2CO3LauncherCommonUI implements H2CO3LauncherMultiPageUICallback {

    private int defaultPageId;

    public H2CO3LauncherMultiPageUI(Context context, H2CO3LauncherUILayout parent, @LayoutRes int id) {
        super(context, parent, id);
    }

    public int getDefaultPageId() {
        return defaultPageId;
    }

    public void setDefaultPageId(int defaultPageId) {
        this.defaultPageId = defaultPageId;
    }

    @Override
    public boolean isShowing() {
        return super.isShowing();
    }

    @Override
    public abstract Task<?> refresh(Object... param);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public abstract void initPages();

    @Override
    public abstract ArrayList<H2CO3LauncherBasePage> getAllPages();

    @Override
    public abstract H2CO3LauncherBasePage getPage(int id);
}
