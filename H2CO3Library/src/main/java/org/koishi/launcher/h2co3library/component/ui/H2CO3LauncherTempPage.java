package org.koishi.launcher.h2co3library.component.ui;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.anim.DisplayAnimUtils;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public abstract class H2CO3LauncherTempPage extends H2CO3LauncherBasePage {

    private final H2CO3LauncherUILayout parent;

    public H2CO3LauncherTempPage(Context context, int id, H2CO3LauncherUILayout parent, @LayoutRes int resId) {
        super(context, id);
        this.parent = parent;
        setContentView(resId, null);
        onCreate();
    }

    public H2CO3LauncherUILayout getParent() {
        return parent;
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

    public void restart() {
        DisplayAnimUtils.showViewWithAnim(getContentView(), R.anim.page_show);
    }

    public abstract void onRestart();

    public void dismiss() {
        onStop();
        Handler handler = new Handler();
        handler.postDelayed(this::onDestroy, 800);
    }
}
