package org.koishi.launcher.h2co3.upgrade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.gson.JsonUtils;
import org.koishi.launcher.h2co3core.util.io.NetworkUtils;
import org.koishi.launcher.h2co3core.util.LocaleUtils;

import java.util.ArrayList;

public class UpdateChecker {

    //TODO: 2022/12/18
    public static final String UPDATE_CHECK_URL = "https://raw.githubusercontent.com/Boat-H2CO3/H2CO3Launcher/main/version_map.json";
    public static final String UPDATE_CHECK_URL_CN = "http://101.43.66.4:1145/api/getupdate";

    private static UpdateChecker instance;
    private boolean isChecking = false;

    public UpdateChecker() {

    }

    public static UpdateChecker getInstance() {
        if (instance == null) {
            instance = new UpdateChecker();
        }
        return instance;
    }

    public static int getCurrentVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("Can't get current version code");
        }
    }

    public static boolean isIgnore(Context context, int code) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("launcher", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("ignore_update", -1) == code;
    }

    public static void setIgnore(Context context, int code) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("launcher", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("ignore_update", code);
        editor.apply();
    }

    public boolean isChecking() {
        return isChecking;
    }

    public Task<?> checkManually(Context context) {
        return check(context, true, true);
    }

    public Task<?> checkAuto(Context context) {
        return check(context, false, false);
    }

    public Task<?> check(Context context, boolean showBeta, boolean showAlert) {
        return Task.runAsync(() -> {
            isChecking = true;
            if (showAlert) {
                Schedulers.androidUIThread().execute(() -> Toast.makeText(context, context.getString(R.string.update_checking), Toast.LENGTH_SHORT).show());
            }
            String res = NetworkUtils.doGet(NetworkUtils.toURL(LocaleUtils.isChinese(context) ? UPDATE_CHECK_URL_CN : UPDATE_CHECK_URL));
            ArrayList<RemoteVersion> versions = JsonUtils.GSON.fromJson(res, new TypeToken<ArrayList<RemoteVersion>>(){}.getType());
            for (RemoteVersion version : versions) {
                if (version.getVersionCode() > getCurrentVersionCode(context)) {
                    if (showBeta || !version.isBeta()) {
                        if (showBeta || !isIgnore(context, version.getVersionCode())) {
                            showUpdateDialog(context, version);
                        }
                        isChecking = false;
                        return;
                    }
                }
            }
            if (showAlert) {
                Schedulers.androidUIThread().execute(() -> Toast.makeText(context, context.getString(R.string.update_not_exist), Toast.LENGTH_SHORT).show());
            }
            isChecking = false;
        });
    }

    private void showUpdateDialog(Context context, RemoteVersion version) {
        Schedulers.androidUIThread().execute(() -> {
            UpdateDialog dialog = new UpdateDialog(context, version);
            dialog.show();
        });
    }

}
