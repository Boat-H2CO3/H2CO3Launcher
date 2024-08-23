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
package org.koishi.launcher.h2co3.core.utils.event;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

public class RenameVersionEvent extends Event {

    private final String from, to;

    public RenameVersionEvent(Object source, String from, String to) {
        super(source);
        this.from = from;
        this.to = to;
    }

    public String getFromVersion() {
        return from;
    }

    public String getToVersion() {
        return to;
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public @NotNull String toString() {
        return new ToStringBuilder(this)
                .append("source", source)
                .append("from", from)
                .append("to", to)
                .toString();
    }
}