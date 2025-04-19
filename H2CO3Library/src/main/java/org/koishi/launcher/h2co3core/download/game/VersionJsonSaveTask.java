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
package org.koishi.launcher.h2co3core.download.game;

import org.koishi.launcher.h2co3core.game.DefaultGameRepository;
import org.koishi.launcher.h2co3core.game.Version;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.gson.JsonUtils;
import org.koishi.launcher.h2co3core.util.io.FileUtils;

import java.io.File;

/**
 * This task is to save the version json.
 */
public final class VersionJsonSaveTask extends Task<Version> {

    private final DefaultGameRepository repository;
    private final Version version;

    /**
     * Constructor.
     *
     * @param repository the game repository
     * @param version the game version
     */
    public VersionJsonSaveTask(DefaultGameRepository repository, Version version) {
        this.repository = repository;
        this.version = version;

        setSignificance(TaskSignificance.MODERATE);
        setResult(version);
    }

    @Override
    public void execute() throws Exception {
        File json = repository.getVersionJson(version.getId()).getAbsoluteFile();
        FileUtils.writeText(json, JsonUtils.GSON.toJson(version));
    }
}
