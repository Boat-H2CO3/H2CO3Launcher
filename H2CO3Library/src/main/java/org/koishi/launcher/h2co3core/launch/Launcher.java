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
package org.koishi.launcher.h2co3core.launch;

import android.content.Context;

import org.koishi.launcher.h2co3launcher.bridge.H2CO3LauncherBridge;
import org.koishi.launcher.h2co3core.auth.AuthInfo;
import org.koishi.launcher.h2co3core.game.GameRepository;
import org.koishi.launcher.h2co3core.game.LaunchOptions;
import org.koishi.launcher.h2co3core.game.Version;

import java.io.IOException;

public abstract class Launcher {

    protected final Context context;
    protected final GameRepository repository;
    protected final Version version;
    protected final AuthInfo authInfo;
    protected final LaunchOptions options;

    public Launcher(Context context) {
        this(context, null, null, null, null);
    }

    public Launcher(Context context, GameRepository repository, Version version, AuthInfo authInfo, LaunchOptions options) {
        this.context = context;
        this.repository = repository;
        this.version = version;
        this.authInfo = authInfo;
        this.options = options;
    }

    public abstract H2CO3LauncherBridge launch() throws IOException, InterruptedException;

}
