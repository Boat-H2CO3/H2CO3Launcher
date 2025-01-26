package org.koishi.launcher.h2co3.game;

import android.content.Context;

import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3launcher.H2CO3LauncherConfig;
import org.koishi.launcher.h2co3launcher.H2CO3Launcher;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.launch.Launcher;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.platform.CommandBuilder;
import org.koishi.launcher.h2co3core.util.platform.MemoryUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class JarExecutorLauncher extends Launcher {

    private String destJarPath;
    private int javaVersion;

    public JarExecutorLauncher(Context context) {
        super(context);
    }

    public static void getCacioJavaArgs(CommandBuilder res, boolean isJava8, boolean isJava11) {
        res.addDefault("-Djava.awt.headless=", "false");
        res.addDefault("-Dcacio.managed.screensize=", H2CO3LauncherBridge.DEFAULT_WIDTH + "x" + H2CO3LauncherBridge.DEFAULT_HEIGHT);
        res.addDefault("-Dcacio.font.fontmanager=", "sun.awt.X11FontManager");
        res.addDefault("-Dcacio.font.fontscaler=", "sun.font.FreetypeFontScaler");
        res.addDefault("-Dswing.defaultlaf=", "javax.swing.plaf.metal.MetalLookAndFeel");
        if (isJava8) {
            res.addDefault("-Dawt.toolkit=", "net.java.openjdk.cacio.ctc.CTCToolkit");
            res.addDefault("-Djava.awt.graphicsenv=", "net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");
        } else {
            res.addDefault("-Dawt.toolkit=", "com.github.caciocavallosilano.cacio.ctc.CTCToolkit");
            res.addDefault("-Djava.awt.graphicsenv=", "com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment");
            res.addDefault("-Djava.system.class.loader=", "com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");

            res.add("--add-exports=java.desktop/java.awt=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt.image=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.java2d=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/java.awt.dnd.peer=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt.event=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.awt.datatransfer=ALL-UNNAMED");
            res.add("--add-exports=java.desktop/sun.font=ALL-UNNAMED");
            res.add("--add-exports=java.base/sun.security.action=ALL-UNNAMED");
            res.add("--add-opens=java.base/java.util=ALL-UNNAMED");
            res.add("--add-opens=java.desktop/java.awt=ALL-UNNAMED");
            res.add("--add-opens=java.desktop/sun.font=ALL-UNNAMED");
            res.add("--add-opens=java.desktop/sun.java2d=ALL-UNNAMED");
            res.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");
            res.add("--add-opens=java.base/java.net=ALL-UNNAMED");
        }

        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/").append(isJava8 ? "p" : "a");
        File cacioDir = new File(isJava8 ? H2CO3LauncherTools.CACIOCAVALLO_8_DIR : isJava11 ? H2CO3LauncherTools.CACIOCAVALLO_11_DIR : H2CO3LauncherTools.CACIOCAVALLO_17_DIR);
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : Objects.requireNonNull(cacioDir.listFiles())) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":").append(file.getAbsolutePath());
                }
            }
        }
        res.add(cacioClasspath.toString());
    }

    public void setInfo(String destJarPath, int javaVersion) {
        this.destJarPath = destJarPath;
        this.javaVersion = javaVersion;
    }

    private CommandBuilder generateCommandLine(String args) {
        CommandBuilder res = new CommandBuilder();

        getCacioJavaArgs(res, javaVersion == 8, javaVersion == 11);

        res.addDefault("-Xms", MemoryUtils.findBestRAMAllocation(context) + "m");
        res.addDefault("-Xmx", MemoryUtils.findBestRAMAllocation(context) + "m");

        res.addDefault("-Duser.home=", Profiles.getSelectedProfile().getGameDir().getParent());
        res.addDefault("-Djava.io.tmpdir=", H2CO3LauncherTools.CACHE_DIR);
        res.addDefault("-Dorg.lwjgl.opengl.libname=", "${gl_lib_name}");

        if (args != null) {
            for (String s : args.split(" ")) {
                res.add(s);
            }
        } else {
            res.add("-jar");
            res.add(destJarPath);
        }
        return res;
    }

    @Override
    public H2CO3LauncherBridge launch() throws IOException, InterruptedException {
        return null;
    }

    public H2CO3LauncherBridge launch(String args) throws IOException, InterruptedException {
        final CommandBuilder command = generateCommandLine(args);

        List<String> rawCommandLine = command.asList();

        if (rawCommandLine.stream().anyMatch(StringUtils::isBlank)) {
            throw new IllegalStateException("Illegal command line " + rawCommandLine);
        }

        String[] finalArgs = rawCommandLine.toArray(new String[0]);

        H2CO3LauncherConfig config = new H2CO3LauncherConfig(
                context,
                H2CO3LauncherTools.LOG_DIR,
                javaVersion == 8 ? H2CO3LauncherTools.JAVA_8_PATH : javaVersion == 11 ? H2CO3LauncherTools.JAVA_11_PATH : javaVersion == 17 ? H2CO3LauncherTools.JAVA_17_PATH : H2CO3LauncherTools.JAVA_21_PATH,
                Profiles.getSelectedProfile().getGameDir().getAbsolutePath(),
                H2CO3LauncherConfig.Renderer.RENDERER_GL4ES,
                finalArgs
        );
        return H2CO3Launcher.launchJarExecutor(config);
    }
}
