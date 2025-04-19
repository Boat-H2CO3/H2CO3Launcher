/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.game;

import android.content.Context;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.util.RuntimeUtils;
import org.koishi.launcher.h2co3launcher.H2CO3LauncherConfig;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3core.auth.AuthInfo;
import org.koishi.launcher.h2co3core.game.GameRepository;
import org.koishi.launcher.h2co3core.game.LaunchOptions;
import org.koishi.launcher.h2co3core.game.Version;
import org.koishi.launcher.h2co3core.launch.DefaultLauncher;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3core.util.versioning.VersionNumber;
import org.koishi.launcher.h2co3core.util.LocaleUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

public final class H2CO3LauncherGameLauncher extends DefaultLauncher {

    public H2CO3LauncherGameLauncher(Context context, GameRepository repository, Version version, AuthInfo authInfo, LaunchOptions options) {
        super(context, repository, version, authInfo, options);
    }

    @Override
    protected Map<String, String> getConfigurations() {
        Map<String, String> res = super.getConfigurations();
        res.put("${launcher_name}", H2CO3LauncherTools.CONTEXT.getString(R.string.app_name));
        res.put("${launcher_version}", H2CO3LauncherTools.CONTEXT.getString(R.string.app_version));
        return res;
    }

    private void generateOptionsTxt() {
        File optionsFile = new File(repository.getRunDirectory(version.getId()), "options.txt");
        File configFolder = new File(repository.getRunDirectory(version.getId()), "config");

        if (!configFolder.exists() && !configFolder.isDirectory()) {
            configFolder.mkdirs();
        }
        File splashFile = new File(configFolder, "splash.properties");
        try {
            FileUtils.writeText(splashFile, "enabled=false");
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Unable to disable forge animation", e);
        }

        if (optionsFile.exists()) {
            modifyOptions(optionsFile, false);
            return;
        }
        try {
            RuntimeUtils.copyAssets(context, "options.txt", optionsFile.getAbsolutePath());
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Unable to generate options.txt", e);
        }

        modifyOptions(optionsFile, true);
    }

    private void modifyOptions(File optionsFile, boolean overwrite) {
        StringBuilder str = new StringBuilder();
        String lang;
        VersionNumber gameVersion = VersionNumber.asVersion(repository.getGameVersion(version).orElse("0.0"));
        if (gameVersion.compareTo("1.1") < 0) {
            lang = null;
        } else if (gameVersion.compareTo("1.11") < 0) {
            lang = "zh_CN";
        } else {
            lang = "zh_cn";
        }
        try (BufferedReader bfr = new BufferedReader(new FileReader(optionsFile))) {
            String line;
            while ((line = bfr.readLine()) != null) {
                if (line.contains("lang:") && LocaleUtils.isChinese(context) && overwrite && lang != null) {
                    str.append("lang:").append(lang).append("\n");
                } else {
                    str.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            Logging.LOG.log(Level.WARNING, "Unable to read options.txt.", e);
        }
        if (!"".equals(str.toString())) {
            try (FileWriter fw = new FileWriter(optionsFile)) {
                fw.write(str.toString());
            } catch (IOException e) {
                Logging.LOG.log(Level.WARNING, "Unable to write options.txt.", e);
            }
        }
    }

    private void modifyIfConfigDetected(String config, String option, String replacement, boolean overwrite, H2CO3LauncherConfig.Renderer... renderers) {
        boolean patch = false;
        if (renderers.length == 0) {
            patch = true;
        } else {
            for (H2CO3LauncherConfig.Renderer renderer : renderers) {
                if (renderer == options.getRenderer()) {
                    patch = true;
                    break;
                }
            }
        }
        File configFolder = new File(repository.getRunDirectory(version.getId()), "config");
        if (patch && configFolder.exists() && new File(configFolder, config).exists()) {
            File configFile = new File(configFolder, config);
            StringBuilder str = new StringBuilder();
            try (BufferedReader bfr = new BufferedReader(new FileReader(configFile))) {
                String line;
                while ((line = bfr.readLine()) != null) {
                    if (overwrite && line.contains(option)) {
                        str.append(replacement).append("\n");
                    } else {
                        str.append(line).append("\n");
                    }
                }
                if (!overwrite && !str.toString().contains(replacement)) {
                    str.append(replacement);
                }
            } catch (Exception e) {
                Logging.LOG.log(Level.WARNING, "Unable to read " + config + ".", e);
            }
            if (!"".equals(str.toString())) {
                try (FileWriter fw = new FileWriter(configFile)) {
                    fw.write(str.toString());
                } catch (IOException e) {
                    Logging.LOG.log(Level.WARNING, "Unable to write " + config + ".", e);
                }
            }
        }
    }

    private boolean findFiles(File folder, String fileName) {
        File[] fs = folder.listFiles();
        if (fs != null) {
            for (File f : fs) {
                if (f.isDirectory())
                    if (f.listFiles((dir, name) -> name.equals(fileName)) != null)
                        return true;
                if (f.getName().equals(fileName))
                    return true;
            }
        }
        return false;
    }

    @Override
    public H2CO3LauncherBridge launch() throws IOException, InterruptedException {
        generateOptionsTxt();
        // Sodium
        modifyIfConfigDetected("sodium-mixins.properties", "", "mixin.features.chunk_rendering=false", false, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        // Rubidium
        modifyIfConfigDetected("rubidium-mixins.properties", "", "mixin.features.chunk_rendering=false", false, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        // DraconicEvolution
        String config = "brandon3055/DraconicEvolution.cfg";
        modifyIfConfigDetected(config, "B:useShaders=", "B:useShaders=false", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        modifyIfConfigDetected(config, "B:\"crystalShaders\"=", "B:\"crystalShaders\"=false", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        modifyIfConfigDetected(config, "B:\"reactorShaders\"=", "B:\"reactorShaders\"=false", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        modifyIfConfigDetected(config, "B:\"guardianShaders\"=", "B:\"guardianShaders\"=false", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        modifyIfConfigDetected(config, "B:\"otherShaders\"=", "B:\"otherShaders\"=false", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        // Pixelmon
        modifyIfConfigDetected("pixelmon/config.yml", "use-discord-rich-presence:", "use-discord-rich-presence: false", true);
        // ImmersiveEngineering
        modifyIfConfigDetected("immersiveengineering-client.toml", "stencilBufferEnabled", "stencilBufferEnabled = false", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES, H2CO3LauncherConfig.Renderer.RENDERER_VGPU);
        // Create
        modifyIfConfigDetected("flywheel-client.toml", "enabled", "enabled = false", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES);
        modifyIfConfigDetected("flywheel-client.toml", "backend =", "backend = \"OFF\"", true, H2CO3LauncherConfig.Renderer.RENDERER_GL4ES);
        return super.launch();
    }
}
