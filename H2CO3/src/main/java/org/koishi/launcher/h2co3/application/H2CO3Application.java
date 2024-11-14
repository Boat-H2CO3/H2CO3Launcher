package org.koishi.launcher.h2co3.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import com.orhanobut.logger.Logger;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.library.R;
import org.koishi.launcher.h2co3.ui.CrashActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class H2CO3Application extends Application implements ActivityLifecycleCallbacks {

    @SuppressLint("StaticFieldLeak")
    private static H2CO3Application instance = null;
    public static ExecutorService sExecutorService = new ThreadPoolExecutor(4, 4, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    @SuppressLint("StaticFieldLeak")
    public static Activity currentActivity;
    private static final Handler MainHandler = new Handler(Looper.getMainLooper());

    public static Handler getMainHandler() {
        return MainHandler;
    }

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService getExecutorService() {
        return executorService;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.registerActivityLifecycleCallbacks(this);
        H2CO3Tools.loadPaths(this);
        CaocConfig.Builder.create()
            .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
            .enabled(true)
            .showErrorDetails(false)
            .showRestartButton(false)
            .trackActivities(true)
            .minTimeBetweenCrashesMs(2000)
            .errorDrawable(R.drawable.ic_boat)
            .errorActivity(CrashActivity.class)
                .eventListener(new CustomEventListener())
                .apply();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        currentActivity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private static class CustomEventListener implements CustomActivityOnCrash.EventListener {
        private static final String TAG = "H2CO3Launcher";

        @Override
        public void onLaunchErrorActivity() {
            Logger.e(TAG, "onLaunchErrorActivity()");
        }

        @Override
        public void onRestartAppFromErrorActivity() {
            Logger.e(TAG, "onRestartAppFromErrorActivity()");
        }

        @Override
        public void onCloseAppFromErrorActivity() {
            Logger.e(TAG, "onCloseAppFromErrorActivity()");
        }
    }

    public static H2CO3Application getInstance() {
        return instance;
    }
}