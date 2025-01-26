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
package org.koishi.launcher.h2co3.setting;

import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import com.google.gson.JsonParseException;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.util.InvocationDispatcher;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Level;

public final class ConfigHolder {

    public static final Path CONFIG_PATH = new File(H2CO3LauncherTools.FILES_DIR + "/config.json").toPath();
    public static final Path GLOBAL_CONFIG_PATH = new File(H2CO3LauncherTools.FILES_DIR + "/global_config.json").toPath();
    private static final InvocationDispatcher<String> configWriter = InvocationDispatcher.runOn(Lang::thread, content -> {
        try {
            writeToConfig(content);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to save config", e);
        }
    });
    private static final InvocationDispatcher<String> globalConfigWriter = InvocationDispatcher.runOn(Lang::thread, content -> {
        try {
            writeToGlobalConfig(content);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to save config", e);
        }
    });
    private static Config configInstance;
    private static GlobalConfig globalConfigInstance;
    private static boolean newlyCreated;

    private ConfigHolder() {
    }

    public static Config config() {
        if (configInstance == null) {
            throw new IllegalStateException("Configuration hasn't been loaded");
        }
        return configInstance;
    }

    public static GlobalConfig globalConfig() {
        if (globalConfigInstance == null) {
            throw new IllegalStateException("Configuration hasn't been loaded");
        }
        return globalConfigInstance;
    }

    public static boolean isNewlyCreated() {
        return newlyCreated;
    }

    public synchronized static void init() throws IOException {
        if (configInstance != null) {
            return;
        }

        configInstance = loadConfig();
        configInstance.addListener(source -> markConfigDirty());

        globalConfigInstance = loadGlobalConfig();
        globalConfigInstance.addListener(source -> markGlobalConfigDirty());

        Settings.init();

        if (newlyCreated) {
            saveConfigSync();
        }
    }

    private static Config loadConfig() throws IOException {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String content = FileUtils.readText(CONFIG_PATH);
                Config deserialized = Config.fromJson(content);
                if (deserialized == null) {
                    LOG.info("Config is empty");
                } else {
                    return deserialized;
                }
            } catch (JsonParseException e) {
                LOG.log(Level.WARNING, "Malformed config.", e);
            }
        }

        LOG.info("Creating an empty config");
        newlyCreated = true;
        return new Config();
    }

    private static void writeToConfig(String content) throws IOException {
        LOG.info("Saving config");
        synchronized (CONFIG_PATH) {
            FileUtils.saveSafely(CONFIG_PATH, content);
        }
    }

    static void markConfigDirty() {
        configWriter.accept(configInstance.toJson());
    }

    // Global Config

    private static void saveConfigSync() throws IOException {
        writeToConfig(configInstance.toJson());
    }

    private static GlobalConfig loadGlobalConfig() throws IOException {
        if (Files.exists(GLOBAL_CONFIG_PATH)) {
            try {
                String content = FileUtils.readText(GLOBAL_CONFIG_PATH);
                GlobalConfig deserialized = GlobalConfig.fromJson(content);
                if (deserialized == null) {
                    LOG.info("Config is empty");
                } else {
                    return deserialized;
                }
            } catch (JsonParseException e) {
                LOG.log(Level.WARNING, "Malformed config.", e);
            }
        }

        LOG.info("Creating an empty global config");
        return new GlobalConfig();
    }

    private static void writeToGlobalConfig(String content) throws IOException {
        LOG.info("Saving global config");
        synchronized (GLOBAL_CONFIG_PATH) {
            FileUtils.saveSafely(GLOBAL_CONFIG_PATH, content);
        }
    }

    static void markGlobalConfigDirty() {
        globalConfigWriter.accept(globalConfigInstance.toJson());
    }

    private static void saveGlobalConfigSync() throws IOException {
        writeToConfig(globalConfigInstance.toJson());
    }
}
