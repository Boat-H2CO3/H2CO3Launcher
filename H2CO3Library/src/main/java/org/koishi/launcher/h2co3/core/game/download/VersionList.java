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
package org.koishi.launcher.h2co3.core.game.download;

import org.koishi.launcher.h2co3.core.utils.SimpleMultimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The remote version list.
 *
 * @param <T> The subclass of {@code RemoteVersion}, the type of RemoteVersion.
 */
public abstract class VersionList<T extends RemoteVersion> {

    /**
     * the remote version list.
     * key: game version.
     * values: corresponding remote versions.
     */
    protected final SimpleMultimap<String, T, TreeSet<T>> versions = new SimpleMultimap<>(HashMap::new, TreeSet::new);
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * True if the version list has been loaded.
     */
    public boolean isLoaded() {
        return !versions.isEmpty();
    }

    /**
     * True if the version list that contains the remote versions which depends on the specific game version has been loaded.
     *
     * @param gameVersion the remote version depends on
     */
    public boolean isLoaded(String gameVersion) {
        return !versions.get(gameVersion).isEmpty();
    }

    public abstract boolean hasType();

    /**
     * @return the task to reload the remote version list.
     */
    public abstract CompletableFuture<?> refreshAsync();

    /**
     * @param gameVersion the remote version depends on
     * @return the task to reload the remote version list.
     */
    public CompletableFuture<?> refreshAsync(String gameVersion) {
        return refreshAsync();
    }

    public CompletableFuture<?> loadAsync() {
        return CompletableFuture.completedFuture(null)
                .thenComposeAsync(unused -> {
                    lock.readLock().lock();
                    boolean loaded;

                    try {
                        loaded = isLoaded();
                    } finally {
                        lock.readLock().unlock();
                    }
                    return loaded ? CompletableFuture.completedFuture(null) : refreshAsync();
                });
    }

    public CompletableFuture<?> loadAsync(String gameVersion) {
        return CompletableFuture.completedFuture(null)
                .thenComposeAsync(unused -> {
                    lock.readLock().lock();
                    boolean loaded;

                    try {
                        loaded = isLoaded(gameVersion);
                    } finally {
                        lock.readLock().unlock();
                    }
                    return loaded ? CompletableFuture.completedFuture(null) : refreshAsync(gameVersion);
                });
    }

    protected Collection<T> getVersionsImpl(String gameVersion) {
        return versions.get(gameVersion);
    }

    /**
     * Get the remote versions that specifics Minecraft version.
     *
     * @param gameVersion the Minecraft version that remote versions belong to
     * @return the collection of specific remote versions
     */
    public final Collection<T> getVersions(String gameVersion) {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableCollection(getVersionsImpl(gameVersion));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get the specific remote version.
     *
     * @param gameVersion   the Minecraft version that remote versions belong to
     * @param remoteVersion the version of the remote version.
     * @return the specific remote version, null if it is not found.
     */
    public Optional<T> getVersion(String gameVersion, String remoteVersion) {
        lock.readLock().lock();
        try {
            T result = versions.get(gameVersion).stream()
                    .filter(it -> remoteVersion.equals(it.getSelfVersion()))
                    .findFirst()
                    .orElse(null);
            return Optional.ofNullable(result);
        } finally {
            lock.readLock().unlock();
        }
    }
}
