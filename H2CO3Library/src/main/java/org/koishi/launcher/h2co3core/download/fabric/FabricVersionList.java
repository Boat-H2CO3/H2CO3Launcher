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
package org.koishi.launcher.h2co3core.download.fabric;

import static org.koishi.launcher.h2co3core.util.Lang.wrap;

import com.google.gson.reflect.TypeToken;
import org.koishi.launcher.h2co3core.download.DownloadProvider;
import org.koishi.launcher.h2co3core.download.VersionList;
import org.koishi.launcher.h2co3core.util.gson.JsonUtils;
import org.koishi.launcher.h2co3core.util.io.NetworkUtils;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class FabricVersionList extends VersionList<FabricRemoteVersion> {
    private static final String LOADER_META_URL = "https://meta.fabricmc.net/v2/versions/loader";
    private static final String GAME_META_URL = "https://meta.fabricmc.net/v2/versions/game";
    private final DownloadProvider downloadProvider;

    public FabricVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    private static String getLaunchMetaUrl(String gameVersion, String loaderVersion) {
        return String.format("https://meta.fabricmc.net/v2/versions/loader/%s/%s", gameVersion, loaderVersion);
    }

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.runAsync(wrap(() -> {
            List<String> gameVersions = getGameVersions(GAME_META_URL);
            List<String> loaderVersions = getGameVersions(LOADER_META_URL);

            lock.writeLock().lock();

            try {
                for (String gameVersion : gameVersions)
                    for (String loaderVersion : loaderVersions)
                        versions.put(gameVersion, new FabricRemoteVersion(gameVersion, loaderVersion,
                                Collections.singletonList(getLaunchMetaUrl(gameVersion, loaderVersion))));
            } finally {
                lock.writeLock().unlock();
            }
        }));
    }

    private List<String> getGameVersions(String metaUrl) throws IOException {
        String json = NetworkUtils.doGet(NetworkUtils.toURL(downloadProvider.injectURL(metaUrl)));
        return JsonUtils.GSON.<ArrayList<GameVersion>>fromJson(json, new TypeToken<ArrayList<GameVersion>>() {
        }.getType()).stream().map(GameVersion::getVersion).collect(Collectors.toList());
    }

    private static class GameVersion {
        private final String version;
        private final String maven;
        private final boolean stable;

        public GameVersion() {
            this("", null, false);
        }

        public GameVersion(String version, String maven, boolean stable) {
            this.version = version;
            this.maven = maven;
            this.stable = stable;
        }

        public String getVersion() {
            return version;
        }

        @Nullable
        public String getMaven() {
            return maven;
        }

        public boolean isStable() {
            return stable;
        }
    }
}
