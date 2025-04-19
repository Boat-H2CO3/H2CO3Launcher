package org.koishi.launcher.h2co3library.component.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.BaseActivity;

public abstract class H2CO3LauncherBasePage implements H2CO3LauncherUILifecycleCallbacks {

    private final Context context;
    private final BaseActivity activity;
    private final int id;

    private View contentView;

    public H2CO3LauncherBasePage(Context context, int id) {
        this.context = context;
        this.activity = (BaseActivity) context;
        this.id = id;
    }

    public Context getContext() {
        return context;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public int getId() {
        return id;
    }

    public void setContentView(@LayoutRes int resId, OnInflateFinishedListener listener) {
        if (listener != null) {
            new AsyncLayoutInflater(context).inflate(resId, null, (view, resid, parent) -> {
                contentView = view;
                listener.onFinish();
            });
        } else {
            contentView = LayoutInflater.from(context).inflate(resId, null);
        }
    }

    public View getContentView() {
        return contentView;
    }

    @NonNull
    public final <T extends View> T findViewById(int id) {
        return contentView.findViewById(id);
    }

    public abstract boolean isShowing();

    public abstract Task<?> refresh(Object... param);

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }
}
