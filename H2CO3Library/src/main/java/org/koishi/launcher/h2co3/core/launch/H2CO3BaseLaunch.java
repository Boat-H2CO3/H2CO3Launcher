package org.koishi.launcher.h2co3.core.launch;

import static org.koishi.launcher.h2co3.core.launch.utils.H2CO3LaunchUtils.getLibraryPath;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.launch.plugins.RendererPlugin;
import org.koishi.launcher.h2co3.core.launch.utils.H2CO3LaunchUtils;
import org.koishi.launcher.h2co3.core.utils.Architecture;
import org.koishi.launcher.h2co3.core.utils.CommandBuilder;
import org.koishi.launcher.h2co3.core.utils.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class H2CO3BaseLaunch {

    private static final String TAG = H2CO3BaseLaunch.class.getSimpleName();

    public static H2CO3LauncherBridge launchGame(Context context, H2CO3Settings gameHelper, int width, int height, String task, String logFilePath) {
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge(gameHelper);
        bridge.setLogPath(logFilePath);
        bridge.setGameDir(gameHelper.getGameDirectory());
        bridge.setRenderer(gameHelper.getRenderer().toString());
        Thread gameThread = new Thread(() -> {
            try {
                logStartInfo(bridge, task);
                setEnv(context, gameHelper, bridge, task.equals("Minecraft"));
                H2CO3LaunchUtils.setUpJavaRuntime(context, gameHelper, bridge);
                H2CO3LaunchUtils.setupGraphicAndSoundEngine(context, gameHelper, bridge);
                logWorkingDirectory(bridge, gameHelper);
                bridge.chdir(gameHelper.getGameDirectory());
                launch(context, bridge, gameHelper, width, height, task);
            } catch (Exception e) {
                Log.e(TAG, "Error launching " + task, e);
            }
        });

        if (task.equals("Minecraft")) {
            gameThread.setPriority(Thread.MAX_PRIORITY);
        }
        bridge.setThread(gameThread);
        return bridge;
    }

    public static H2CO3LauncherBridge launchMinecraft(Context context, H2CO3Settings gameHelper, int width, int height) throws IOException {
        return launchGame(context, gameHelper, width, height, "Minecraft", H2CO3Tools.LOG_DIR + "/latest_game.txt");
    }

    public static H2CO3LauncherBridge launchJarExecutor(Context context, H2CO3Settings gameHelper, int width, int height) {
        return launchGame(context, gameHelper, width, height, "Jar Executor", H2CO3Tools.LOG_FILE_PATH + "/latest_jar_executor.log");
    }

    public static H2CO3LauncherBridge launchAPIInstaller(Context context, H2CO3Settings gameHelper, String[] command, String jre) {
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge(gameHelper);
        bridge.setLogPath(H2CO3Tools.LOG_DIR + "/latest_api_installer.log");
        Thread apiInstallerThread = new Thread(() -> {
            try {
                logStartInfo(bridge, "API Installer");
                setEnv(context, gameHelper, bridge, false);
                H2CO3LaunchUtils.setUpJavaRuntime(context, gameHelper, bridge);
                logWorkingDirectory(bridge, gameHelper);
                bridge.chdir(gameHelper.getGameDirectory());
                apiLaunch(context, bridge, command, jre, "API Installer");
            } catch (Exception e) {
                Log.e(TAG, "Error launching API Installer", e);
            }
        });

        bridge.setThread(apiInstallerThread);
        return bridge;
    }

    public static void apiLaunch(Context context, H2CO3LauncherBridge bridge, String[] command, String jre, String task) throws IOException {
        printTaskTitle(bridge, task + " Arguments");
        String[] args = rebaseArgs(command, jre);
        logArguments(bridge, task, args);
        bridge.setLdLibraryPath(getLibraryPath(context, null));
        printTaskTitle(bridge, task + " Logs");
        bridge.setupExitTrap(bridge);
        bridge.getCallback().onLog("Hook success");
        int exitCode = H2CO3JVMLauncher.launchJVM(args);
        bridge.onExit(exitCode);
        printTaskTitle(bridge, task + " Logs");
    }

    public static void setEnv(Context context, H2CO3Settings gameHelper, H2CO3LauncherBridge bridge, boolean isRender) throws Exception {
        HashMap<String, String> envMap = new HashMap<>(8);
        H2CO3LaunchUtils.addCommonEnv(context, gameHelper, envMap);
        if (isRender) {
            H2CO3LaunchUtils.addRendererEnv(context, gameHelper, envMap);
        }
        printTaskTitle(bridge, "Env Map");
        envMap.forEach((key, value) -> {
            try {
                bridge.getCallback().onLog("Env: " + key + "=" + value + "\n");
                bridge.setenv(key, value);
            } catch (IOException e) {
                Log.e(TAG, "Error setting environment variable", e);
            }
        });
        printTaskTitle(bridge, "Env Map");
    }

    public static void launch(Context context, H2CO3LauncherBridge bridge, H2CO3Settings gameHelper, int width, int height, String task) throws Exception {
        printTaskTitle(bridge, task + " Arguments");
        String[] args = rebaseArgs(context, gameHelper, width, height);
        logArguments(bridge, task, args);
        String javaPath = H2CO3LaunchUtils.getJavaPath(gameHelper);
        bridge.setLdLibraryPath(getLibraryPath(context, javaPath, gameHelper.getRenderer() == H2CO3Settings.Renderer.RENDERER_CUSTOM ? RendererPlugin.getSelected().getPath() : null));
        printTaskTitle(bridge, task + " Logs");
        bridge.setupExitTrap(bridge);
        bridge.getCallback().onLog("Hook success");
        int exitCode = H2CO3JVMLauncher.launchJVM(args);
        bridge.onExit(exitCode);
        printTaskTitle(bridge, task + " Logs");
    }

    public static void printTaskTitle(H2CO3LauncherBridge bridge, String task) throws IOException {
        if (bridge != null && bridge.getCallback() != null) {
            bridge.getCallback().onLog("==================== " + task + " ====================\n");
        }
    }

    public static void logWorkingDirectory(H2CO3LauncherBridge bridge, H2CO3Settings gameHelper) throws IOException {
        bridge.getCallback().onLog("Working directory: " + gameHelper.getGameDirectory() + "\n");
    }

    public static void logArguments(H2CO3LauncherBridge bridge, String task, String[] args) throws IOException {
        for (String arg : args) {
            bridge.getCallback().onLog(task + " argument: " + arg + "\n");
        }
    }

    public static void logStartInfo(H2CO3LauncherBridge bridge, String task) throws IOException {
        printTaskTitle(bridge, "Start " + task);
        bridge.getCallback().onLog("Architecture: " + Architecture.archAsString(Architecture.getDeviceArchitecture()) + "\n");
        bridge.getCallback().onLog("CPU:" + Build.HARDWARE + "\n");
        bridge.getCallback().onLog("Device info:" + Build.DEVICE + "\n " + Build.MODEL + "\n " + Build.PRODUCT + "\n " + Build.BOARD + "\n " + Build.BRAND + "\n " + Build.FINGERPRINT + "\n");
    }

    public static String[] rebaseArgs(Context context, H2CO3Settings gameHelper, int width, int height) throws Exception {
        final CommandBuilder command = H2CO3LaunchUtils.getMcArgs(context, gameHelper, width, height);
        List<String> rawCommandLine = command.asList();

        Log.d(TAG, "Raw command line: " + rawCommandLine);

        if (rawCommandLine.stream().anyMatch(StringUtils::isBlank)) {
            Log.e(TAG, "Illegal command line: " + rawCommandLine);
            throw new IllegalStateException("Illegal command line " + rawCommandLine);
        }

        String javaPath = H2CO3LaunchUtils.getJavaPath(gameHelper);

        return Stream.concat(Stream.of(javaPath + "/bin/java"), rawCommandLine.stream())
                .toArray(String[]::new);
    }

    public static String[] rebaseArgs(String[] command, String jre) {
        String javaPath = H2CO3Tools.JAVA_PATH + "/" + jre + "/bin/java";
        return Stream.concat(Stream.of(javaPath), Arrays.stream(command))
                .toArray(String[]::new);
    }
}