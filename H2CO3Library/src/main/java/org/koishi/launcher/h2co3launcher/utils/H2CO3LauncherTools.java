package org.koishi.launcher.h2co3launcher.utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3core.message.H2CO3MessageManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class H2CO3LauncherTools {

    public static Context CONTEXT;

    public static String NATIVE_LIB_DIR;

    public static String LOG_DIR;
    public static String CACHE_DIR;

    public static String RUNTIME_DIR;
    public static String JAVA_8_PATH;
    public static String JAVA_11_PATH;
    public static String JAVA_17_PATH;
    public static String JAVA_21_PATH;
    public static String JNA_PATH;
    public static String LWJGL_DIR;
    public static String CACIOCAVALLO_8_DIR;
    public static String CACIOCAVALLO_11_DIR;
    public static String CACIOCAVALLO_17_DIR;

    public static String FILES_DIR;
    public static String PLUGIN_DIR;
    public static String CONTROLLER_DIR;

    public static String PRIVATE_COMMON_DIR;
    public static String SHARED_COMMON_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/H2CO3Launcher/.minecraft";

    public static String AUTHLIB_INJECTOR_PATH;
    public static String LIB_FIXER_PATH;
    public static String LAUNCH_WRAPPER;

    public static String H2CO3_CONFIG_NAME;
    public static String H2CO3_SETTING_DIR;
    private static H2CO3MessageManager h2co3MessageManager;

    public static void loadPaths(Context context) {
        CONTEXT = context;

        NATIVE_LIB_DIR = context.getApplicationInfo().nativeLibraryDir;

        LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/H2CO3Launcher/log";
        CACHE_DIR = context.getCacheDir() + "/H2CO3Launcher";

        RUNTIME_DIR = context.getDir("runtime", 0).getAbsolutePath();
        JAVA_8_PATH = RUNTIME_DIR + "/java/jre8";
        JAVA_11_PATH = RUNTIME_DIR + "/java/jre11";
        JAVA_17_PATH = RUNTIME_DIR + "/java/jre17";
        JAVA_21_PATH = RUNTIME_DIR + "/java/jre21";
        JNA_PATH = RUNTIME_DIR + "/jna";
        LWJGL_DIR = RUNTIME_DIR + "/lwjgl";
        CACIOCAVALLO_8_DIR = RUNTIME_DIR + "/caciocavallo";
        CACIOCAVALLO_11_DIR = RUNTIME_DIR + "/caciocavallo11";
        CACIOCAVALLO_17_DIR = RUNTIME_DIR + "/caciocavallo17";

        FILES_DIR = context.getFilesDir().getAbsolutePath();
        PLUGIN_DIR = FILES_DIR + "/plugins";
        CONTROLLER_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/H2CO3Launcher/control";

        PRIVATE_COMMON_DIR = context.getExternalFilesDir(".minecraft").getAbsolutePath();

        AUTHLIB_INJECTOR_PATH = PLUGIN_DIR + "/authlib-injector.jar";
        LIB_FIXER_PATH = PLUGIN_DIR + "/LibFixer.jar";
        LAUNCH_WRAPPER = PLUGIN_DIR + "/LaunchWrapper.jar";

        H2CO3_SETTING_DIR = "/org.koishi.launcher.h2co3/settings";
        H2CO3_CONFIG_NAME = "H2CO3Config.json";

        init(LOG_DIR);
        init(CACHE_DIR);
        init(RUNTIME_DIR);
        init(JAVA_8_PATH);
        init(JAVA_11_PATH);
        init(JAVA_17_PATH);
        init(JAVA_21_PATH);
        init(LWJGL_DIR);
        init(CACIOCAVALLO_8_DIR);
        init(CACIOCAVALLO_11_DIR);
        init(CACIOCAVALLO_17_DIR);
        init(FILES_DIR);
        init(PLUGIN_DIR);
        init(CONTROLLER_DIR);
        init(PRIVATE_COMMON_DIR);
        init(SHARED_COMMON_DIR);
        init(H2CO3_SETTING_DIR);
    }

    private static boolean init(String path) {
        if (!new File(path).exists()) {
            return new File(path).mkdirs();
        }
        return true;
    }

    public static H2CO3MessageManager getH2CO3MessageManager() {
        return h2co3MessageManager;
    }

    public static void setH2CO3MessageManager(H2CO3MessageManager h2co3MessageManager) {
        H2CO3LauncherTools.h2co3MessageManager = h2co3MessageManager;
    }

    public static void showMessage(H2CO3MessageManager.NotificationItem.Type type, String message) {
        H2CO3LauncherTools.h2co3MessageManager.addNotification(type, message);
    }

}
