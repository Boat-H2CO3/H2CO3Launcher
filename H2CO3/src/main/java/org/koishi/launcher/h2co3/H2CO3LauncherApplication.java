package org.koishi.launcher.h2co3;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class H2CO3LauncherApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static WeakReference<Activity> currentActivity;

    public static Activity getCurrentActivity() {
        return currentActivity != null ? currentActivity.get() : null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        enableStrictMode();
    }

    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        updateCurrentActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        updateCurrentActivity(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        // No-op
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        // No-op
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        // No-op
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        // No-op
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (currentActivity != null && currentActivity.get() == activity) {
            currentActivity.clear();
        }
    }

    private void updateCurrentActivity(Activity activity) {
        currentActivity = new WeakReference<>(activity);
    }
}
