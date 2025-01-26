package org.koishi.launcher.h2co3library.component.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.anim.DisplayAnimUtils;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.util.ArrayList;

public abstract class H2CO3LauncherCommonPage extends H2CO3LauncherBasePage {

    private final H2CO3LauncherUILayout parent;

    private final ArrayList<H2CO3LauncherTempPage> allTempPages = new ArrayList<>();
    private H2CO3LauncherTempPage currentTempPage;

    public H2CO3LauncherCommonPage(Context context, int id, H2CO3LauncherUILayout parent, @LayoutRes int resId) {
        super(context, id);
        this.parent = parent;
        setContentView(resId, null);
        onCreate();
    }

    public H2CO3LauncherUILayout getParent() {
        return parent;
    }

    public ArrayList<H2CO3LauncherTempPage> getAllTempPages() {
        return allTempPages;
    }

    public H2CO3LauncherTempPage getCurrentTempPage() {
        return currentTempPage;
    }

    public void setCurrentTempPage(H2CO3LauncherTempPage currentTempPage) {
        this.currentTempPage = currentTempPage;
    }

    @Override
    public boolean isShowing() {
        return getContentView().getVisibility() == View.VISIBLE;
    }

    @Override
    public abstract Task<?> refresh(Object... param);

    @Override
    public void onCreate() {
        super.onCreate();
        getContentView().setVisibility(View.GONE);
        parent.addView(getContentView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayAnimUtils.showViewWithAnim(getContentView(), R.anim.page_show);
    }

    @Override
    public void onStop() {
        super.onStop();
        DisplayAnimUtils.hideViewWithAnim(getContentView(), R.anim.page_hide);
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
        parent.removeView(getContentView());
    }

}
