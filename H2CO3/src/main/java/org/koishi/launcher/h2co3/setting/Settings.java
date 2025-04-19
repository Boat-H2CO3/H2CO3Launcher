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

import org.koishi.launcher.h2co3.game.H2CO3LauncherCacheRepository;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.util.CacheRepository;

public final class Settings {

    private static Settings instance;

    private Settings() {
        DownloadProviders.init();
        Accounts.init();
        Profiles.init();
        Schedulers.defaultScheduler().execute(Controllers::init);
        AuthlibInjectorServers.init();

        CacheRepository.setInstance(H2CO3LauncherCacheRepository.REPOSITORY);
        H2CO3LauncherCacheRepository.REPOSITORY.setDirectory(H2CO3LauncherTools.CACHE_DIR);
    }

    public static Settings instance() {
        if (instance == null) {
            throw new IllegalStateException("Settings hasn't been initialized");
        }
        return instance;
    }

    /**
     * Should be called from {@link ConfigHolder#init()}.
     */
    static void init() {
        instance = new Settings();
    }

}
