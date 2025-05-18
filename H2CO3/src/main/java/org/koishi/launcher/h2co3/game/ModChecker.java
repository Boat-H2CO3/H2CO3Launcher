package org.koishi.launcher.h2co3.game;

import android.content.Context;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3core.mod.LocalModFile;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3launcher.plugins.FFmpegPlugin;
import org.koishi.launcher.h2co3launcher.utils.Architecture;

public class ModChecker {
    private final Context context;

    public ModChecker(Context context) {
        this.context = context;
    }

    public void check(H2CO3LauncherBridge bridge, LocalModFile mod) throws ModCheckException {
        try {
            switch (mod.getId()) {
                case "touchcontroller":
                    bridge.setHasTouchController(true);
                    break;

                case "physicsmod":
                    String arch = AndroidUtils.getElfArchFromZip(
                            mod.getFile().toFile(),
                            "de/fabmax/physxjni/linux/libPhysXJniBindings_64.so"
                    );
                    if (arch.isBlank() || (!Architecture.isx86Device() && arch.contains("x86"))) {
                        throw new ModCheckException(context.getString(R.string.mod_check_physics, mod.getFile().toFile().getName()));
                    }
                    break;

                case "mcef":
                    throw new ModCheckException(context.getString(R.string.mod_check_mcef, mod.getFile().toFile().getName()));

                case "valkyrienskies":
                    throw new ModCheckException(context.getString(R.string.mod_check_valkyrienskies, mod.getFile().toFile().getName()));

                case "yes_steve_model":
                    arch = AndroidUtils.getElfArchFromZip(
                            mod.getFile().toFile(),
                            "META-INF/native/libysm-core.so"
                    );
                    if (!arch.isBlank()) {
                        throw new ModCheckException(context.getString(R.string.mod_check_yes_steve_model, mod.getFile().toFile().getName()));
                    }
                    break;

                case "imblocker":
                case "ingameime":
                    throw new ModCheckException(context.getString(R.string.mod_check_imblocker, mod.getFile().toFile().getName()));

                case "replaymod":
                    FFmpegPlugin.discover(context);
                    if (!FFmpegPlugin.isAvailable) {
                        throw new ModCheckException(context.getString(R.string.mod_check_replay, mod.getFile().toFile().getName(),
                                "https://github.com/FCL-Team/FoldCraftLauncher/releases/download/ffmpeg/Pojav.FFmpeg.Plugin.1.1.APK",
                                "https://pan.quark.cn/s/6201574edb62"));
                    }
                    break;

                case "borderlesswindow":
                    throw new ModCheckException(context.getString(R.string.mod_check_borderlesswindow, mod.getFile().toFile().getName()));
            }
        } catch (Exception e) {
            throw new ModCheckException(e.getMessage());
        }
    }
}

class ModCheckException extends Exception {
    public ModCheckException(String reason) {
        super(reason);
    }
}


