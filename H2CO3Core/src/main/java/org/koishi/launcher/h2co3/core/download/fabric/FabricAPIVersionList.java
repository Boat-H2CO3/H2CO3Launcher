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
package org.koishi.launcher.h2co3.core.download.fabric;


import static org.koishi.launcher.h2co3.core.utils.Lang.wrap;

import org.koishi.launcher.h2co3.core.download.DownloadProvider;
import org.koishi.launcher.h2co3.core.download.VersionList;
import org.koishi.launcher.h2co3.core.game.mod.RemoteMod;
import org.koishi.launcher.h2co3.core.game.mod.modrinth.ModrinthRemoteModRepository;
import org.koishi.launcher.h2co3.core.utils.Lang;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class FabricAPIVersionList extends VersionList<FabricAPIRemoteVersion> {

    public FabricAPIVersionList(DownloadProvider downloadProvider) {
    }

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.runAsync(wrap(() -> {
            for (RemoteMod.VersionMod modVersionMod : Lang.toIterable(ModrinthRemoteModRepository.MODS.getRemoteVersionsById("P7dR8mSH"))) {
                for (String gameVersion : modVersionMod.gameVersions()) {
                    versions.put(gameVersion, new FabricAPIRemoteVersion(gameVersion, modVersionMod.version(), modVersionMod.name(), modVersionMod.datePublished(), modVersionMod,
                            Collections.singletonList(modVersionMod.file().getUrl())));
                }
            }
        }));
    }
}
