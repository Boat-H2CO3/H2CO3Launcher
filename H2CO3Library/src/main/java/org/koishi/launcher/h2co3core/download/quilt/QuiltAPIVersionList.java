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
package org.koishi.launcher.h2co3core.download.quilt;

import static org.koishi.launcher.h2co3core.util.Lang.wrap;

import org.koishi.launcher.h2co3core.download.DownloadProvider;
import org.koishi.launcher.h2co3core.download.VersionList;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.mod.modrinth.ModrinthRemoteModRepository;
import org.koishi.launcher.h2co3core.util.Lang;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class QuiltAPIVersionList extends VersionList<QuiltAPIRemoteVersion> {

    private final DownloadProvider downloadProvider;

    public QuiltAPIVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.runAsync(wrap(() -> {
            for (RemoteMod.Version modVersion : Lang.toIterable(ModrinthRemoteModRepository.MODS.getRemoteVersionsById("qsl"))) {
                for (String gameVersion : modVersion.getGameVersions()) {
                    versions.put(gameVersion, new QuiltAPIRemoteVersion(gameVersion, modVersion.getVersion(), modVersion.getName(), modVersion.getDatePublished(), modVersion,
                            Collections.singletonList(modVersion.getFile().getUrl())));
                }
            }
        }));
    }
}
