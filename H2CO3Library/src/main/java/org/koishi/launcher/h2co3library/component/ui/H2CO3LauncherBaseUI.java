package org.koishi.launcher.h2co3library.component.ui;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.BaseActivity;

public abstract class H2CO3LauncherBaseUI implements H2CO3LauncherUILifecycleCallbacks {

    private static Runnable defaultBackEvent;

    private final Context context;
    private final BaseActivity activity;

    private View contentView;

    public H2CO3LauncherBaseUI(Context context) {
        this.context = context;
        this.activity = (BaseActivity) context;
    }

    public static void setDefaultBackEvent(Runnable defaultBackEvent) {
        H2CO3LauncherBaseUI.defaultBackEvent = defaultBackEvent;
    }

    public Context getContext() {
        return context;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    public void setContentView(@LayoutRes int id, OnInflateFinishedListener listener) {
        new AsyncLayoutInflater(context).inflate(id, null, (view, resid, parent) -> {
            contentView = view;
            listener.onFinish();
        });
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
        if (defaultBackEvent != null && isShowing()) {
            Schedulers.androidUIThread().execute(defaultBackEvent);
        }
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
