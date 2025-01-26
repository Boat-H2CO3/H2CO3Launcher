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

import static org.koishi.launcher.h2co3.setting.ConfigHolder.config;
import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import com.google.gson.JsonParseException;
import org.koishi.launcher.h2co3core.auth.authlibinjector.AuthlibInjectorServer;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.gson.JsonUtils;
import org.koishi.launcher.h2co3core.util.gson.TolerableValidationException;
import org.koishi.launcher.h2co3core.util.gson.Validation;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;

public final class AuthlibInjectorServers implements Validation {

    public static final String CONFIG_FILENAME = "authlib-injectors.json";

    private static final Set<AuthlibInjectorServer> servers = new CopyOnWriteArraySet<>();
    private static final Path configLocation = new File(H2CO3LauncherTools.FILES_DIR + "/" + CONFIG_FILENAME).toPath();
    private final List<String> urls;

    private AuthlibInjectorServers(List<String> urls) {
        this.urls = urls;
    }

    public static Set<AuthlibInjectorServer> getServers() {
        return servers;
    }

    public static void init() {
        if (ConfigHolder.isNewlyCreated() && Files.exists(configLocation)) {
            AuthlibInjectorServers configInstance;
            try {
                String content = FileUtils.readText(configLocation);
                configInstance = JsonUtils.GSON.fromJson(content, AuthlibInjectorServers.class);
            } catch (IOException | JsonParseException e) {
                LOG.log(Level.WARNING, "Malformed authlib-injectors.json", e);
                return;
            }

            if (!configInstance.urls.isEmpty()) {
                config().setPreferredLoginType(Accounts.getLoginType(Accounts.FACTORY_AUTHLIB_INJECTOR));
                for (String url : configInstance.urls) {
                    Task.supplyAsync(Schedulers.io(), () -> AuthlibInjectorServer.locateServer(url))
                            .thenAcceptAsync(Schedulers.androidUIThread(), server -> {
                                config().getAuthlibInjectorServers().add(server);
                                servers.add(server);
                            })
                            .start();
                }
            }
        }
    }

    @Override
    public void validate() throws JsonParseException, TolerableValidationException {
        if (this.urls == null) {
            throw new JsonParseException("authlib-injectors.json -> urls cannot be null.");
        }
    }
}
