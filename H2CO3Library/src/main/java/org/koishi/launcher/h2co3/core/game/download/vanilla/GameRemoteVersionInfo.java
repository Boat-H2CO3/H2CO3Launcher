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
package org.koishi.launcher.h2co3.core.game.download.vanilla;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;


import org.apache.commons.lang3.StringUtils;
import org.koishi.launcher.h2co3.core.game.download.ReleaseType;
import org.koishi.launcher.h2co3.core.utils.Constants;
import org.koishi.launcher.h2co3.core.utils.gson.tools.Validation;

import java.time.Instant;

public final class GameRemoteVersionInfo implements Validation {

    @SerializedName("id")
    private final String gameVersion;

    @SerializedName("time")
    private final Instant time;

    @SerializedName("releaseTime")
    private final Instant releaseTime;

    @SerializedName("type")
    private final ReleaseType type;

    @SerializedName("url")
    private final String url;

    public GameRemoteVersionInfo() {
        this("", Instant.now(), Instant.now(), ReleaseType.UNKNOWN);
    }

    public GameRemoteVersionInfo(String gameVersion, Instant time, Instant releaseTime, ReleaseType type) {
        this(gameVersion, time, releaseTime, type, Constants.DEFAULT_LIBRARY_URL + gameVersion + "/" + gameVersion + ".json");
    }

    public GameRemoteVersionInfo(String gameVersion, Instant time, Instant releaseTime, ReleaseType type, String url) {
        this.gameVersion = gameVersion;
        this.time = time;
        this.releaseTime = releaseTime;
        this.type = type;
        this.url = url;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public Instant getTime() {
        return time;
    }

    public Instant getReleaseTime() {
        return releaseTime;
    }

    public ReleaseType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
    
    @Override
    public void validate() throws JsonParseException {
        if (StringUtils.isBlank(gameVersion))
            throw new JsonParseException("GameRemoteVersion id cannot be blank");
        if (StringUtils.isBlank(url))
            throw new JsonParseException("GameRemoteVersion url cannot be blank");
    }
}
