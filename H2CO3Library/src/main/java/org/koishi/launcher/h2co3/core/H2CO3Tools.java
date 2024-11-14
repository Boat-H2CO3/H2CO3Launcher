package org.koishi.launcher.h2co3.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.Architecture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class H2CO3Tools {


    public static final String DOWNLOAD_SOURCE = "Source";
    public static String H2CO3_CONTROL_DIR;
    public static String CACIOCAVALLO_8_DIR;
    public static String CACIOCAVALLO_11_DIR;
    public static String CACIOCAVALLO_17_DIR;

    @SuppressLint("StaticFieldLeak")
    public static Context CONTEXT;

    public static String NATIVE_LIB_DIR;

    public static String LOG_DIR;
    public static String CACHE_DIR;

    public static String RUNTIME_DIR;
    public static String JAVA_PATH;
    public static String JAVA_8_PATH;
    public static String JAVA_11_PATH;
    public static String JAVA_17_PATH;
    public static String JAVA_21_PATH;

    public static File DIRS_CONFIG_FILE;

    public static String FILES_DIR;
    public static String PLUGIN_DIR;
    public static String H2CO3_LIBRARY_DIR;
    public static String H2CO3_SETTING_DIR;

    public static String MINECRAFT_DIR;
    public static String SHARED_COMMON_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/org.koishi.launcher/h2co3/.minecraft";

    public static String AUTHLIB_INJECTOR_PATH;
    public static String MULTIPLAYER_FIX_PATH;
    public static String APP_DATA_PATH;
    public static String PUBLIC_FILE_PATH;


    public static String H2CO3_CONFIG_NAME;
    public static String H2CO3_LAUNCHER_CONFIG_NAME;

    public static String LOGIN_AUTH_PLAYER_NAME;
    public static String LOGIN_USER_EMAIL;
    public static String LOGIN_USER_PASSWORD;
    public static String LOGIN_USER_TYPE;
    public static String LOGIN_API_URL;
    public static String LOGIN_AUTH_SESSION;
    public static String LOGIN_UUID;
    public static String LOGIN_USER_SKINTEXTURE;
    public static String LOGIN_TOKEN;
    public static String LOGIN_REFRESH_TOKEN;
    public static String LOGIN_CLIENT_TOKEN;
    public static String LOGIN_IS_OFFLINE;
    public static String LOGIN_IS_SELECTED;
    public static String LOGIN_ERROR;
    public static String LOGIN_INFO;
    public static String GL_GL114;
    public static String GL_ANGLE;
    public static String GL_VGPU;
    public static String GL_ZINK;
    public static String GL_VIRGL;
    private static final String H2CO3_LIB_DIR = H2CO3Tools.RUNTIME_DIR + "/h2co3_launcher";
    public static final String LOG_FILE_PATH = H2CO3Tools.LOG_DIR + "/minecraft_log.log";
    public static String H2CO3LAUNCHER_LIBRARY_DIR;
    private static final String SERVICE_LOG_FILE_PATH = H2CO3Tools.LOG_DIR + "/h2co3_service_log.txt";
    private static final String API_INSTALLER_LOG_FILE_PATH = H2CO3Tools.LOG_DIR + "/h2co3_api_installer_log.txt";
    private static final String VIRGL_TEST_SOCKET_NAME = ".virgl_test";
    private static final String VIRGL_TEST_SOCKET_PATH = H2CO3Tools.CACHE_DIR + "/" + VIRGL_TEST_SOCKET_NAME;

    private static H2CO3MessageManager h2co3MessageManager;
    @SuppressLint("SdCardPath")
    public static void loadPaths(Context context) {
        CONTEXT = context;

        NATIVE_LIB_DIR = context.getApplicationInfo().nativeLibraryDir;

        PUBLIC_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/org.koishi.launcher/h2co3";

        LOG_DIR = PUBLIC_FILE_PATH + "/log";
        CACHE_DIR = context.getCacheDir() + "/H2CO3Launcher/";

        APP_DATA_PATH = "/data/data/org.koishi.launcher.h2co3";
        RUNTIME_DIR = APP_DATA_PATH + "/app_runtime";
        JAVA_PATH = RUNTIME_DIR + "/java";
        JAVA_8_PATH = JAVA_PATH + "/jre8";
        JAVA_11_PATH = JAVA_PATH + "/jre11";
        JAVA_17_PATH = JAVA_PATH + "/jre17";
        JAVA_21_PATH = JAVA_PATH + "/jre21";
        H2CO3LAUNCHER_LIBRARY_DIR = RUNTIME_DIR + "/h2co3Launcher";
        PLUGIN_DIR = RUNTIME_DIR + "/h2co3Launcher/plugin";
        CACIOCAVALLO_8_DIR = PLUGIN_DIR + "/caciocavallo";
        CACIOCAVALLO_11_DIR = PLUGIN_DIR + "/caciocavallo11";
        CACIOCAVALLO_17_DIR = PLUGIN_DIR + "/caciocavallo17";
        H2CO3_LIBRARY_DIR = APP_DATA_PATH + "/h2co3";
        H2CO3_SETTING_DIR = APP_DATA_PATH + "/h2co3_setting";
        DIRS_CONFIG_FILE = new File(H2CO3_SETTING_DIR, "h2co3_dirs.json");

        H2CO3_CONTROL_DIR = APP_DATA_PATH + "/control";

        FILES_DIR = context.getFilesDir().getAbsolutePath();

        MINECRAFT_DIR = PUBLIC_FILE_PATH + "/.minecraft";

        AUTHLIB_INJECTOR_PATH = PLUGIN_DIR + "/authlib-injector.jar";
        MULTIPLAYER_FIX_PATH = PLUGIN_DIR + "/MultiplayerFix.jar";

        H2CO3_CONFIG_NAME = "H2CO3Config.json";
        H2CO3_LAUNCHER_CONFIG_NAME = "H2CO3LauncherConfig.json";

        LOGIN_AUTH_PLAYER_NAME = "h2co3_users_auth_player_name";
        LOGIN_USER_EMAIL = "h2co3_users_email";
        LOGIN_USER_PASSWORD = "h2co3_users_auth_player_password";
        LOGIN_USER_TYPE = "h2co3_users_type";
        LOGIN_API_URL = "h2co3_users_api_url";
        LOGIN_AUTH_SESSION = "h2co3_users_auth_session";
        LOGIN_UUID = "h2co3_users_uuid";
        LOGIN_USER_SKINTEXTURE = "h2co3_skin_texture";
        LOGIN_TOKEN = "h2co3_users_token";
        LOGIN_REFRESH_TOKEN = "h2co3_users_refresh_token";
        LOGIN_CLIENT_TOKEN = "h2co3_users_client_token";
        LOGIN_IS_OFFLINE = "h2co3_users_isOffline";
        LOGIN_IS_SELECTED = "h2co3_users_isSelected";
        LOGIN_ERROR = "Login Filed";
        LOGIN_INFO = "h2co3_users_info";

        GL_GL114 = "gl4es";
        GL_ANGLE = "angle";
        GL_VGPU = "vgpu";
        GL_ZINK = "zink";
        GL_VIRGL = "virgl";

        init(LOG_DIR);
        init(CACHE_DIR);
        init(RUNTIME_DIR);
        init(JAVA_8_PATH);
        init(JAVA_11_PATH);
        init(JAVA_17_PATH);
        init(JAVA_21_PATH);
        init(H2CO3LAUNCHER_LIBRARY_DIR);
        init(FILES_DIR);
        init(PLUGIN_DIR);
        init(H2CO3_LIBRARY_DIR);
        init(H2CO3_SETTING_DIR);
        init(MINECRAFT_DIR);
        init(APP_DATA_PATH);
        init(SHARED_COMMON_DIR);
        init(PUBLIC_FILE_PATH);
        init(H2CO3_CONFIG_NAME);
        init(H2CO3_LAUNCHER_CONFIG_NAME);
    }

    private static void init(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void setH2CO3MessageManager(H2CO3MessageManager h2co3MessageManager) {
        H2CO3Tools.h2co3MessageManager = h2co3MessageManager;
    }

    public static H2CO3MessageManager getH2CO3MessageManager() {
        return h2co3MessageManager;
    }

    public static <T> T getH2CO3LauncherValue(String key, T defaultValue, Class<T> type) {
        return getValue(H2CO3_SETTING_DIR + "/" + H2CO3_LAUNCHER_CONFIG_NAME, key, defaultValue, type);
    }

    public static void setH2CO3LauncherValue(String key, java.io.Serializable value) {
        String configFile = getH2CO3Value("usesGlobal", false, Boolean.class) ? getH2CO3Value("currentVersion", H2CO3_SETTING_DIR, String.class) + "/" + H2CO3_LAUNCHER_CONFIG_NAME : H2CO3_SETTING_DIR + "/" + H2CO3_LAUNCHER_CONFIG_NAME;
        setValue(configFile, key, value);
    }

    public static <T> T getH2CO3Value(String key, T defaultValue, Class<T> type) {
        return getValue(H2CO3_SETTING_DIR + "/" + H2CO3_CONFIG_NAME, key, defaultValue, type);
    }

    public static void setH2CO3Value(String key, java.io.Serializable value) {
        setValue(H2CO3_SETTING_DIR + "/" + H2CO3_CONFIG_NAME, key, value);
    }

    private static <T> T getValue(String configFile, String key, T defaultValue, Class<T> type) {
        try {
            Path configPath = Paths.get(configFile);
            if (!Files.exists(configPath)) {
                return defaultValue;
            }

            List<String> configLines = Files.readAllLines(configPath);
            String configContent = String.join("", configLines);
            JSONObject configJson = new JSONObject(configContent);

            if (configJson.has(key)) {
                return type.cast(configJson.get(key));
            } else {
                return defaultValue;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file: " + configFile, e);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse config file: " + configFile, e);
        }
    }

    public static void setValue(String configFile, String key, java.io.Serializable value) {
        try {
            Path configPath = Paths.get(configFile);
            if (!Files.exists(configPath)) {
                createConfigFile(configPath, key, value);
                return;
            }

            List<String> configLines = Files.readAllLines(configPath);
            String configContent = String.join("", configLines);
            JSONObject configJson = new JSONObject(configContent);

            configJson.put(key, value);
            Files.write(configPath, configJson.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or create config file: " + configFile, e);
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse config file: " + configFile, e);
        }
    }

    private static void createConfigFile(Path configPath, String key, Object value) throws IOException {
        Files.createDirectories(configPath.getParent());
        Files.createFile(configPath);
        JSONObject defaultConfigJson = new JSONObject();
        try {
            defaultConfigJson.put(key, value);
            Files.write(configPath, defaultConfigJson.toString().getBytes());
        } catch (JSONException e) {
            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
        }
    }

    public static String read(InputStream is) throws IOException {
        List<String> lines = IOUtils.readLines(is, "UTF-8");
        return String.join("", lines);
    }

    public static String read(String path) throws IOException {
        return read(new FileInputStream(path));
    }

    public static void write(String path, String content) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) throw new IOException("Failed to create parent directory");
        }
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            IOUtils.write(content, outStream, "UTF-8");
        }
    }

    public static String getArchitectureString(int architecture) {
        return switch (architecture) {
            case Architecture.ARCH_ARM -> "aarch32";
            case Architecture.ARCH_X86 -> "i386";
            case Architecture.ARCH_X86_64 -> "amd64";
            default -> "aarch64";
        };
    }

    public static void showMessage(H2CO3MessageManager.NotificationItem.Type type, String message) {
        H2CO3Tools.h2co3MessageManager.addNotification(type, message);
    }
}