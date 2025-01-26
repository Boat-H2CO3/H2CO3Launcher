package org.koishi.launcher.h2co3launcher;

import static org.koishi.launcher.h2co3launcher.utils.Architecture.ARCH_X86;
import static org.koishi.launcher.h2co3launcher.utils.Architecture.is64BitsDevice;

import android.content.Context;
import android.os.Build;
import android.system.Os;
import android.util.ArrayMap;

import com.jaredrummler.android.device.DeviceName;
import com.oracle.dalvik.VMLauncher;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.plugins.DriverPlugin;
import org.koishi.launcher.h2co3launcher.plugins.FFmpegPlugin;
import org.koishi.launcher.h2co3launcher.plugins.RendererPlugin;
import org.koishi.launcher.h2co3launcher.utils.Architecture;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class H2CO3Launcher {

    private static void log(H2CO3LauncherBridge bridge, String log) {
        bridge.getCallback().onLog(log + "\n");
    }

    private static void printTaskTitle(H2CO3LauncherBridge bridge, String task) {
        log(bridge, "==================== " + task + " ====================");
    }

    private static void logStartInfo(H2CO3LauncherBridge bridge, String task) {
        printTaskTitle(bridge, "Start " + task);
        log(bridge, "Device: " + DeviceName.getDeviceName());
        log(bridge, "Architecture: " + Architecture.archAsString(Architecture.getDeviceArchitecture()));
        log(bridge, "CPU: " + getSocName());
        log(bridge, "Android SDK: " + Build.VERSION.SDK_INT);
        log(bridge, "Language: " + Locale.getDefault());
    }

    private static void logModList(H2CO3LauncherBridge bridge) {
        printTaskTitle(bridge, "Mods");
        log(bridge, bridge.getModSummary());
        bridge.setModSummary(null);
    }

    private static Map<String, String> readJREReleaseProperties(String javaPath) throws IOException {
        Map<String, String> jreReleaseMap = new ArrayMap<>();
        BufferedReader jreReleaseReader = new BufferedReader(new FileReader(javaPath + "/release"));
        String currLine;
        while ((currLine = jreReleaseReader.readLine()) != null) {
            if (currLine.contains("=")) {
                String[] keyValue = currLine.split("=");
                jreReleaseMap.put(keyValue[0], keyValue[1].replace("\"", ""));
            }
        }
        jreReleaseReader.close();
        return jreReleaseMap;
    }

    public static String getJreLibDir(String javaPath) throws IOException {
        String jreArchitecture = readJREReleaseProperties(javaPath).get("OS_ARCH");
        if (Architecture.archAsInt(jreArchitecture) == ARCH_X86) {
            jreArchitecture = "i386/i486/i586";
        }
        String jreLibDir = "/lib";
        if (jreArchitecture == null) {
            throw new IOException("Unsupported architecture!");
        }
        for (String arch : jreArchitecture.split("/")) {
            File file = new File(javaPath, "lib/" + arch);
            if (file.exists() && file.isDirectory()) {
                jreLibDir = "/lib/" + arch;
            }
        }
        return jreLibDir;
    }

    private static String getJvmLibDir(String javaPath) throws IOException {
        String jvmLibDir;
        File jvmFile = new File(javaPath + getJreLibDir(javaPath) + "/server/libjvm.so");
        jvmLibDir = jvmFile.exists() ? "/server" : "/client";
        return jvmLibDir;
    }

    private static String getLibraryPath(Context context, String javaPath, String pluginLibPath) throws IOException {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        String libDirName = is64BitsDevice() ? "lib64" : "lib";
        String jreLibDir = getJreLibDir(javaPath);
        String jvmLibDir = getJvmLibDir(javaPath);
        String jliLibDir = "/jli";
        String split = ":";
        return javaPath +
                jreLibDir +
                split +

                javaPath +
                jreLibDir +
                jliLibDir +
                split +

                javaPath +
                jreLibDir +
                jvmLibDir +
                split +

                "/system/" +
                libDirName +
                split +

                "/vendor/" +
                libDirName +
                split +

                "/vendor/" +
                libDirName +
                "/hw" +
                split +

                H2CO3LauncherTools.RUNTIME_DIR + "/jna" +
                split +

                ((pluginLibPath != null) ? pluginLibPath + split : "") +

                nativeDir;
    }

    private static String getLibraryPath(Context context, String pluginLibPath) {
        String nativeDir = context.getApplicationInfo().nativeLibraryDir;
        String libDirName = is64BitsDevice() ? "lib64" : "lib";
        String split = ":";
        return "/system/" +
                libDirName +
                split +

                "/vendor/" +
                libDirName +
                split +

                "/vendor/" +
                libDirName +
                "/hw" +
                split +

                ((pluginLibPath != null) ? pluginLibPath + split : "") +

                nativeDir;
    }

    private static String[] rebaseArgs(H2CO3LauncherConfig config) throws IOException {
        ArrayList<String> argList = new ArrayList<>(Arrays.asList(config.getArgs()));
        argList.add(0, config.getJavaPath() + "/bin/java");
        String[] args = new String[argList.size()];
        for (int i = 0; i < argList.size(); i++) {
            String a = argList.get(i).replace("${natives_directory}", getLibraryPath(config.getContext(), config.getJavaPath(), config.getRenderer() == H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM ? RendererPlugin.getSelected().getPath() : null));
            args[i] = config.getRenderer() == null ? a : a.replace("${gl_lib_name}", config.getRenderer() == H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM ? RendererPlugin.getSelected().getGlName() : config.getRenderer().getGlLibName());
        }
        return args;
    }

    private static void addCommonEnv(H2CO3LauncherConfig config, HashMap<String, String> envMap) {
        envMap.put("HOME", config.getLogDir());
        envMap.put("JAVA_HOME", config.getJavaPath());
        envMap.put("H2CO3LAUNCHER_NATIVEDIR", config.getContext().getApplicationInfo().nativeLibraryDir);
        envMap.put("POJAV_NATIVEDIR", config.getContext().getApplicationInfo().nativeLibraryDir);
        envMap.put("DRIVER_PATH", DriverPlugin.getSelected().getPath());
        envMap.put("TMPDIR", config.getContext().getCacheDir().getAbsolutePath());
        envMap.put("PATH", config.getJavaPath() + "/bin:" + Os.getenv("PATH"));
        envMap.put("LD_LIBRARY_PATH", getLibraryPath(config.getContext(), config.getRenderer() == H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM ? RendererPlugin.getSelected().getPath() : null));
        envMap.put("FORCE_VSYNC", "false");
        FFmpegPlugin.discover(config.getContext());
        if (FFmpegPlugin.isAvailable) {
            envMap.put("PATH", FFmpegPlugin.libraryPath + ":" + envMap.get("PATH"));
            envMap.put("LD_LIBRARY_PATH", FFmpegPlugin.libraryPath + ":" + envMap.get("LD_LIBRARY_PATH"));
        }
        if (config.isUseVKDriverSystem()) {
            envMap.put("VULKAN_DRIVER_SYSTEM", "1");
        }
        if (config.isPojavBigCore()) {
            envMap.put("POJAV_BIG_CORE_AFFINITY", "1");
        }
    }

    private static void addRendererEnv(H2CO3LauncherConfig config, HashMap<String, String> envMap) {
        H2CO3LauncherConfig.Renderer renderer = config.getRenderer() == null ? H2CO3LauncherConfig.Renderer.RENDERER_GL4ES : config.getRenderer();
        if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM) {
            String eglName = RendererPlugin.getSelected().getEglName();
            if (eglName.startsWith("/")) {
                eglName = RendererPlugin.getSelected().getPath() + "/" + eglName;
            }
            List<String> envList;
            if (H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
                envMap.put("LIBGL_STRING", RendererPlugin.getSelected().getName());
                envMap.put("LIBGL_NAME", RendererPlugin.getSelected().getGlName());
                envMap.put("LIBEGL_NAME", eglName);
                envList = RendererPlugin.getSelected().getH2co3Env();
            } else {
                envMap.put("POJAVEXEC_EGL", eglName);
                envList = RendererPlugin.getSelected().getPojavEnv();
            }
            envList.forEach(env -> {
                String[] split = env.split("=");
                if (split[0].equals("DLOPEN")){
                    return;
                }
                if (split[0].equals("LIB_MESA_NAME")) {
                    envMap.put(split[0], RendererPlugin.getSelected().getPath() + "/" + split[1]);
                } else {
                    envMap.put(split[0], split[1]);
                }
            });
            return;
        }
        boolean useAngle = false;
        if (H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
            envMap.put("LIBGL_STRING", renderer.toString());
            envMap.put("LIBGL_NAME", renderer.getGlLibName());
            if (useAngle && renderer == H2CO3LauncherConfig.Renderer.RENDERER_GL4ESPLUS) {
                envMap.put("LIBEGL_NAME", "libEGL_angle.so");
                envMap.put("LIBGL_BACKEND_ANGLE", "1");
            } else {
                envMap.put("LIBEGL_NAME", renderer.getEglLibName());
                envMap.put("LIBGL_BACKEND_ANGLE", "0");
            }
        }
        if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_GL4ES || renderer == H2CO3LauncherConfig.Renderer.RENDERER_VGPU) {
            envMap.put("LIBGL_ES", "2");
            envMap.put("LIBGL_MIPMAP", "3");
            envMap.put("LIBGL_NORMALIZE", "1");
            envMap.put("LIBGL_NOINTOVLHACK", "1");
            envMap.put("LIBGL_NOERROR", "1");
            if (!H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
                if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_GL4ES) {
                    envMap.put("POJAV_RENDERER", "opengles2");
                } else {
                    envMap.put("POJAV_RENDERER", "opengles2_vgpu");
                }
            }
        } else if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_GL4ESPLUS) {
            envMap.put("LIBGL_ES", "3");
            envMap.put("LIBGL_MIPMAP", "3");
            envMap.put("LIBGL_NORMALIZE", "1");
            envMap.put("LIBGL_NOINTOVLHACK", "1");
            envMap.put("LIBGL_SHADERCONVERTER", "1");
            envMap.put("LIBGL_GL", "21");
            envMap.put("LIBGL_USEVBO", "1");
            if (!H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
                envMap.put("POJAV_RENDERER", "opengles3");
                envMap.put("POJAVEXEC_EGL", useAngle ? "libEGL_angle.so" : renderer.getEglLibName());
            }
        } else {
            envMap.put("MESA_GLSL_CACHE_DIR", config.getContext().getCacheDir().getAbsolutePath());
            envMap.put("MESA_GL_VERSION_OVERRIDE", renderer == H2CO3LauncherConfig.Renderer.RENDERER_VIRGL ? "4.3" : "4.6");
            envMap.put("MESA_GLSL_VERSION_OVERRIDE", renderer == H2CO3LauncherConfig.Renderer.RENDERER_VIRGL ? "430" : "460");
            envMap.put("force_glsl_extensions_warn", "true");
            envMap.put("allow_higher_compat_version", "true");
            envMap.put("allow_glsl_extension_directive_midshader", "true");
            envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");
            envMap.put("VTEST_SOCKET_NAME", new File(config.getContext().getCacheDir().getAbsolutePath(), ".virgl_test").getAbsolutePath());
            if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_VIRGL) {
                if (H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
                    envMap.put("GALLIUM_DRIVER", "virpipe");
                } else {
                    envMap.put("POJAV_RENDERER", "gallium_virgl");
                }
                envMap.put("OSMESA_NO_FLUSH_FRONTBUFFER", "1");
            } else if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_ZINK) {
                if (H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
                    envMap.put("GALLIUM_DRIVER", "zink");
                } else {
                    envMap.put("POJAV_RENDERER", "vulkan_zink");
                }
            } else if (renderer == H2CO3LauncherConfig.Renderer.RENDERER_FREEDRENO) {
                if (H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
                    envMap.put("GALLIUM_DRIVER", "freedreno");
                    envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "kgsl");
                } else {
                    envMap.put("POJAV_RENDERER", "gallium_freedreno");
                }
            }
        }
    }

    private static void setEnv(H2CO3LauncherConfig config, H2CO3LauncherBridge bridge, boolean render) {
        HashMap<String, String> envMap = new HashMap<>();
        addCommonEnv(config, envMap);
        if (render) {
            addRendererEnv(config, envMap);
        }
        printTaskTitle(bridge, "Env Map");
        for (String key : envMap.keySet()) {
            log(bridge, "Env: " + key + "=" + envMap.get(key));
            bridge.setenv(key, envMap.get(key));
        }
        printTaskTitle(bridge, "Env Map");
    }

    private static void setUpJavaRuntime(H2CO3LauncherConfig config, H2CO3LauncherBridge bridge) throws IOException {
        String jreLibDir = config.getJavaPath() + getJreLibDir(config.getJavaPath());
        String jliLibDir = new File(jreLibDir + "/jli/libjli.so").exists() ? jreLibDir + "/jli" : jreLibDir;
        String jvmLibDir = jreLibDir + getJvmLibDir(config.getJavaPath());
        // dlopen jre
        bridge.dlopen(jliLibDir + "/libjli.so");
        bridge.dlopen(jvmLibDir + "/libjvm.so");
        bridge.dlopen(jreLibDir + "/libfreetype.so");
        bridge.dlopen(jreLibDir + "/libverify.so");
        bridge.dlopen(jreLibDir + "/libjava.so");
        bridge.dlopen(jreLibDir + "/libnet.so");
        bridge.dlopen(jreLibDir + "/libnio.so");
        bridge.dlopen(jreLibDir + "/libawt.so");
        bridge.dlopen(jreLibDir + "/libawt_headless.so");
        bridge.dlopen(jreLibDir + "/libfontmanager.so");
        bridge.dlopen(jreLibDir + "/libtinyiconv.so");
        bridge.dlopen(jreLibDir + "/libinstrument.so");
        for (File file : locateLibs(new File(config.getJavaPath()))) {
            bridge.dlopen(file.getAbsolutePath());
        }
    }

    public static ArrayList<File> locateLibs(File path) {
        ArrayList<File> returnValue = new ArrayList<>();
        File[] list = path.listFiles();
        if (list != null) {
            for (File f : list) {
                if (f.isFile() && f.getName().endsWith(".so")) {
                    returnValue.add(f);
                } else if (f.isDirectory()) {
                    returnValue.addAll(locateLibs(f));
                }
            }
        }
        return returnValue;
    }

    private static void setupGraphicAndSoundEngine(H2CO3LauncherConfig config, H2CO3LauncherBridge bridge) {
        String nativeDir = config.getContext().getApplicationInfo().nativeLibraryDir;

        bridge.dlopen(nativeDir + "/libopenal.so");
        if (config.getRenderer() == H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM) {
            List<String> envList;
            if (H2CO3LauncherBridge.BACKEND_IS_H2CO3) {
                envList = RendererPlugin.getSelected().getH2co3Env();
            } else {
                envList = RendererPlugin.getSelected().getPojavEnv();
            }
            envList.forEach(env -> {
                String[] split = env.split("=");
                if (split[0].equals("DLOPEN")) {
                    String[] libs = split[1].split(",");
                    for (String lib : libs) {
                        bridge.dlopen(RendererPlugin.getSelected().getPath() + "/" + lib);
                    }
                }
            });
            bridge.dlopen(RendererPlugin.getSelected().getPath() + "/" + RendererPlugin.getSelected().getGlName());
//            bridge.dlopen(RendererPlugin.getSelected().getPath() + "/" + RendererPlugin.getSelected().getEglName());
        } else {
            bridge.dlopen(nativeDir + "/" + config.getRenderer().getGlLibName());
        }
    }

    private static void launch(H2CO3LauncherConfig config, H2CO3LauncherBridge bridge, String task) throws IOException {
        printTaskTitle(bridge, task + " Arguments");
        String[] args = rebaseArgs(config);
        boolean javaArgs = true;
        int mainClass = 0;
        boolean isToken = false;
        for (String arg : args) {
            if (javaArgs)
                javaArgs = !arg.equals("mio.Wrapper");
            String title = task.equals("Minecraft") ? javaArgs ? "Java" : task : task;
            String prefix = title + " argument: ";
            if (task.equals("Minecraft") && !javaArgs && mainClass < 2) {
                mainClass++;
                prefix = "MainClass: ";
            }
            if (isToken) {
                isToken = false;
                log(bridge, prefix + "***");
                continue;
            }
            if (arg.equals("--accessToken"))
                isToken = true;
            log(bridge, prefix + arg);
        }
        bridge.setLdLibraryPath(getLibraryPath(config.getContext(), config.getJavaPath(), config.getRenderer() == H2CO3LauncherConfig.Renderer.RENDERER_CUSTOM ? RendererPlugin.getSelected().getPath() : null));
        bridge.setupExitTrap(bridge);
        log(bridge, "Hook success");
        int exitCode = VMLauncher.launchJVM(args);
        bridge.onExit(exitCode);
    }

    public static H2CO3LauncherBridge launchMinecraft(H2CO3LauncherConfig config) {

        // initialize H2CO3LauncherBridge
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(config.getLogDir() + "/latest_game.log");
        Thread gameThread = new Thread(() -> {
            try {
                logStartInfo(bridge, "Minecraft");
                logModList(bridge);

                // env
                setEnv(config, bridge, true);

                // setup java runtime
                setUpJavaRuntime(config, bridge);

                // setup graphic and sound engine
                setupGraphicAndSoundEngine(config, bridge);

                // set working directory
                log(bridge, "Working directory: " + config.getWorkingDir());
                bridge.chdir(config.getWorkingDir());

                // launch game
                launch(config, bridge, "Minecraft");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gameThread.setPriority(Thread.MAX_PRIORITY);
        bridge.setThread(gameThread);

        return bridge;
    }

    public static H2CO3LauncherBridge launchJarExecutor(H2CO3LauncherConfig config) {

        // initialize H2CO3LauncherBridge
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(config.getLogDir() + "/latest_jar_executor.log");
        Thread javaGUIThread = new Thread(() -> {
            try {

                logStartInfo(bridge, "Jar Executor");

                // env
                setEnv(config, bridge, true);

                // setup java runtime
                setUpJavaRuntime(config, bridge);

                // setup graphic and sound engine
                setupGraphicAndSoundEngine(config, bridge);

                // set working directory
                log(bridge, "Working directory: " + config.getWorkingDir());
                bridge.chdir(config.getWorkingDir());

                // launch jar executor
                launch(config, bridge, "Jar Executor");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bridge.setThread(javaGUIThread);

        return bridge;
    }

    public static H2CO3LauncherBridge launchAPIInstaller(H2CO3LauncherConfig config) {

        // initialize H2CO3LauncherBridge
        H2CO3LauncherBridge bridge = new H2CO3LauncherBridge();
        bridge.setLogPath(config.getLogDir() + "/latest_api_installer.log");
        Thread apiInstallerThread = new Thread(() -> {
            try {

                logStartInfo(bridge, "API Installer");

                // env
                setEnv(config, bridge, false);

                // setup java runtime
                setUpJavaRuntime(config, bridge);

                // set working directory
                log(bridge, "Working directory: " + config.getWorkingDir());
                bridge.chdir(config.getWorkingDir());

                // launch api installer
                launch(config, bridge, "API Installer");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        bridge.setThread(apiInstallerThread);

        return bridge;
    }

    private static String getSocName() {
        String name = null;
        try {
            Process process = Runtime.getRuntime().exec("getprop ro.soc.model");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            name = reader.readLine();
            reader.close();
        } catch (Exception ignore) {
        }
        return  (name == null || name.trim().isEmpty()) ? Build.HARDWARE : name;
    }

}
