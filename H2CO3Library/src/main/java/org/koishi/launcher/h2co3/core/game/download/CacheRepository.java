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

import static org.koishi.launcher.h2co3.core.utils.Logging.LOG;

import android.util.Log;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.koishi.launcher.h2co3.core.utils.DigestUtils;
import org.koishi.launcher.h2co3.core.utils.Lang;
import org.koishi.launcher.h2co3.core.utils.StringUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.function.ExceptionalSupplier;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.stream.Stream;

public class CacheRepository {
    public static final String SHA1 = "SHA-1";
    private static CacheRepository instance = new CacheRepository();
    private final Map<String, Storage> storages = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    public Path cacheDirectory;
    private Path commonDirectory;
    private Path indexFile;
    private Map<String, ETagItem> index;

    public static CacheRepository getInstance() {
        return instance;
    }

    public static void setInstance(CacheRepository instance) {
        CacheRepository.instance = instance;
    }

    public void changeDirectory(Path commonDir) {
        commonDirectory = commonDir;
        cacheDirectory = commonDir.resolve("cache");
        indexFile = cacheDirectory.resolve("etag.json");

        lock.writeLock().lock();
        try {
            for (Storage storage : storages.values()) {
                storage.changeDirectory(cacheDirectory);
            }

            if (Files.isRegularFile(indexFile)) {
                ETagIndex raw = JsonUtils.GSON.fromJson(FileTools.readText(indexFile), ETagIndex.class);
                if (raw == null)
                    index = new HashMap<>();
                else
                    index = joinETagIndexes(raw.eTag);
            } else
                index = new HashMap<>();
        } catch (IOException | JsonParseException e) {
            LOG.log(Level.WARNING, "Unable to read index file", e);
            index = new HashMap<>();
        } finally {
            lock.writeLock().unlock();
        }

    }

    public Path getCommonDirectory() {
        return commonDirectory;
    }

    public Path getCacheDirectory() {
        return cacheDirectory;
    }

    public Storage getStorage(String key) {
        lock.readLock().lock();
        try {
            return storages.computeIfAbsent(key, Storage::new);
        } finally {
            lock.readLock().unlock();
        }
    }

    protected Path getFile(String algorithm, String hash) {
        return getCacheDirectory().resolve(algorithm).resolve(hash.substring(0, 2)).resolve(hash);
    }

    protected boolean fileExists(String algorithm, String hash) {
        if (hash == null) return false;
        Path file = getFile(algorithm, hash);
        if (Files.exists(file)) {
            try {
                return DigestUtils.digestToString(algorithm, file).equalsIgnoreCase(hash);
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void tryCacheFile(Path path, String algorithm, String hash) throws IOException {
        Path cache = getFile(algorithm, hash);
        if (Files.isRegularFile(cache)) return;
        FileTools.copyFile(path, cache);
    }

    public Path cacheFile(Path path, String algorithm, String hash) throws IOException {
        Path cache = getFile(algorithm, hash);
        FileTools.copyFile(path, cache);
        return cache;
    }

    public Optional<Path> checkExistentFile(Path original, String algorithm, String hash) {
        if (fileExists(algorithm, hash))
            return Optional.of(getFile(algorithm, hash));

        if (original != null && Files.exists(original)) {
            if (hash != null) {
                try {
                    String checksum = DigestUtils.digestToString(algorithm, original);
                    if (checksum.equalsIgnoreCase(hash))
                        return Optional.of(restore(original, () -> cacheFile(original, algorithm, hash)));
                } catch (IOException e) {
                    // we cannot check the hashcode.
                }
            } else {
                return Optional.of(original);
            }
        }

        return Optional.empty();
    }

    protected Path restore(Path original, ExceptionalSupplier<Path, ? extends IOException> cacheSupplier) throws IOException {
        Path cache = cacheSupplier.get();
        Files.delete(original);
        Files.createLink(original, cache);
        return cache;
    }

    public Path getCachedRemoteFile(URLConnection conn) throws IOException {
        String url = conn.getURL().toString();
        lock.readLock().lock();
        ETagItem eTagItem;
        try {
            eTagItem = index.get(url);
        } finally {
            lock.readLock().unlock();
        }
        if (eTagItem == null) throw new IOException("Cannot find the URL");
        if (StringUtils.isBlank(eTagItem.hash) || !fileExists(SHA1, eTagItem.hash))
            throw new FileNotFoundException();
        Path file = getFile(SHA1, eTagItem.hash);
        if (Files.getLastModifiedTime(file).toMillis() != eTagItem.localLastModified) {
            String hash = DigestUtils.digestToString(SHA1, file);
            if (!Objects.equals(hash, eTagItem.hash))
                throw new IOException("This file is modified");
        }
        return file;
    }

    public void removeRemoteEntry(URLConnection conn) {
        String url = conn.getURL().toString();
        lock.readLock().lock();
        try {
            index.remove(url);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void injectConnection(URLConnection conn) {
        if (conn == null || conn.getURL() == null) {
            return;
        }

        String url = conn.getURL().toString();
        Log.d("CacheRepository", "Injecting connection for URL: " + url + " " + index);

        lock.readLock().lock();
        try {
            ETagItem eTagItem = index.get(url);
            if (eTagItem != null && eTagItem.eTag != null) {
                conn.setRequestProperty("If-None-Match", eTagItem.eTag);
            }
            // if (eTagItem != null && eTagItem.getRemoteLastModified() != null) {
            //     conn.setRequestProperty("If-Modified-Since", eTagItem.getRemoteLastModified());
            // }
        } finally {
            lock.readLock().unlock();
        }
    }

    public void cacheRemoteFile(Path downloaded, URLConnection conn) throws IOException {
        cacheData(() -> {
            String hash = DigestUtils.digestToString(SHA1, downloaded);
            Path cached = cacheFile(downloaded, SHA1, hash);
            return new CacheResult(hash, cached);
        }, conn);
    }

    public void cacheText(String text, URLConnection conn) throws IOException {
        cacheBytes(text.getBytes("UTF-8"), conn);
    }

    public void cacheBytes(byte[] bytes, URLConnection conn) throws IOException {
        cacheData(() -> {
            String hash = DigestUtils.digestToString(SHA1, bytes);
            Path cached = getFile(SHA1, hash);
            FileTools.writeBytes(cached, bytes);
            return new CacheResult(hash, cached);
        }, conn);
    }

    public synchronized void cacheData(ExceptionalSupplier<CacheResult, IOException> cacheSupplier, URLConnection conn) throws IOException {
        String eTag = conn.getHeaderField("ETag");
        if (eTag == null) return;
        String url = conn.getURL().toString();
        String lastModified = conn.getHeaderField("Last-Modified");
        CacheResult cacheResult = cacheSupplier.get();
        ETagItem eTagItem = new ETagItem(url, eTag, cacheResult.hash, Files.getLastModifiedTime(cacheResult.cachedFile).toMillis(), lastModified);
        Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            index.compute(eTagItem.url, updateEntity(eTagItem));
            saveETagIndex();
        } finally {
            writeLock.unlock();
        }
    }

    private BiFunction<String, ETagItem, ETagItem> updateEntity(ETagItem newItem) {
        return (key, oldItem) -> {
            if (oldItem == null) {
                return newItem;
            } else if (oldItem.compareTo(newItem) < 0) {
                Path cached = getFile(SHA1, oldItem.hash);
                try {
                    Files.deleteIfExists(cached);
                } catch (IOException e) {
                    LOG.log(Level.WARNING, "Cannot delete old file");
                }
                return newItem;
            } else {
                return oldItem;
            }
        };
    }

    @SafeVarargs
    private final Map<String, ETagItem> joinETagIndexes(Collection<ETagItem>... indexes) {
        Map<String, ETagItem> eTags = new ConcurrentHashMap<>();

        Stream<ETagItem> stream = Arrays.stream(indexes).filter(Objects::nonNull).map(Collection::stream)
                .reduce(Stream.empty(), Stream::concat);

        stream.forEach(eTag -> {
            eTags.compute(eTag.url, updateEntity(eTag));
        });

        return eTags;
    }

    public void saveETagIndex() throws IOException {
        try (FileChannel channel = FileChannel.open(indexFile, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            FileLock lock = channel.lock();
            try {
                ETagIndex indexOnDisk = JsonUtils.fromMaybeMalformedJson(new String(IOUtils.readFullyWithoutClosing(Channels.newInputStream(channel)), "UTF-8"), ETagIndex.class);
                Map<String, ETagItem> newIndex = joinETagIndexes(indexOnDisk == null ? null : indexOnDisk.eTag, index.values());
                channel.truncate(0);
                ByteBuffer writeTo = ByteBuffer.wrap(JsonUtils.GSON.toJson(new ETagIndex(newIndex.values())).getBytes("UTF-8"));
                while (writeTo.hasRemaining()) {
                    if (channel.write(writeTo) == 0) {
                        throw new IOException("No value is written");
                    }
                }
                this.index = newIndex;
            } finally {
                lock.release();
            }
        }
    }

    private static class CacheResult {
        public String hash;
        public Path cachedFile;

        public CacheResult(String hash, Path cachedFile) {
            this.hash = hash;
            this.cachedFile = cachedFile;
        }
    }

    private static final class ETagIndex {
        private final Collection<ETagItem> eTag;

        public ETagIndex() {
            this.eTag = new HashSet<>();
        }

        public ETagIndex(Collection<ETagItem> eTags) {
            this.eTag = new HashSet<>(eTags);
        }
    }

    private static final class ETagItem {
        private final String url;
        private final String eTag;
        private final String hash;
        @SerializedName("local")
        private final long localLastModified;
        @SerializedName("remote")
        private final String remoteLastModified;

        /**
         * For Gson.
         */
        public ETagItem() {
            this(null, null, null, 0, null);
        }

        public ETagItem(String url, String eTag, String hash, long localLastModified, String remoteLastModified) {
            this.url = url;
            this.eTag = eTag;
            this.hash = hash;
            this.localLastModified = localLastModified;
            this.remoteLastModified = remoteLastModified;
        }

        public int compareTo(ETagItem other) {
            if (!url.equals(other.url))
                throw new IllegalArgumentException();

            ZonedDateTime thisTime = Lang.ignoringException(() -> ZonedDateTime.parse(remoteLastModified, DateTimeFormatter.RFC_1123_DATE_TIME), null);
            ZonedDateTime otherTime = Lang.ignoringException(() -> ZonedDateTime.parse(other.remoteLastModified, DateTimeFormatter.RFC_1123_DATE_TIME), null);
            if (thisTime == null && otherTime == null) return 0;
            else if (thisTime == null) return -1;
            else if (otherTime == null) return 1;
            else return thisTime.compareTo(otherTime);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ETagItem eTagItem = (ETagItem) o;
            return localLastModified == eTagItem.localLastModified &&
                    Objects.equals(url, eTagItem.url) &&
                    Objects.equals(eTag, eTagItem.eTag) &&
                    Objects.equals(hash, eTagItem.hash) &&
                    Objects.equals(remoteLastModified, eTagItem.remoteLastModified);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, eTag, hash, localLastModified, remoteLastModified);
        }
    }

    /**
     * Universal cache
     */
    public static final class Storage {
        private final String name;
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private Map<String, Object> storage;
        private Path indexFile;

        public Storage(String name) {
            this.name = name;
        }

        public Object getEntry(String key) {
            lock.readLock().lock();
            try {
                return storage.get(key);
            } finally {
                lock.readLock().unlock();
            }
        }

        public void putEntry(String key, Object value) {
            lock.writeLock().lock();
            try {
                storage.put(key, value);
                saveToFile();
            } finally {
                lock.writeLock().unlock();
            }
        }

        private void joinEntries(Map<String, Object> storage) {
            this.storage.putAll(storage);
        }

        private void changeDirectory(Path cacheDirectory) {
            lock.writeLock().lock();
            try {
                indexFile = cacheDirectory.resolve(name + ".json");
                if (Files.isRegularFile(indexFile)) {
                    joinEntries(JsonUtils.fromNonNullJson(FileTools.readText(indexFile), new TypeToken<Map<String, Object>>() {
                    }.getType()));
                }
            } catch (IOException | JsonParseException e) {
                LOG.log(Level.WARNING, "Unable to read storage {" + name + "} file");
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void saveToFile() {
            try (FileChannel channel = FileChannel.open(indexFile, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
                FileLock lock = channel.lock();
                try {
                    Map<String, Object> indexOnDisk = JsonUtils.fromMaybeMalformedJson(new String(IOUtils.readFullyWithoutClosing(Channels.newInputStream(channel)), "UTF-8"), new TypeToken<Map<String, Object>>() {
                    }.getType());
                    if (indexOnDisk == null) indexOnDisk = new HashMap<>();
                    indexOnDisk.putAll(storage);
                    channel.truncate(0);
                    channel.write(ByteBuffer.wrap(JsonUtils.GSON.toJson(storage).getBytes("UTF-8")));
                    this.storage = indexOnDisk;
                } finally {
                    lock.release();
                }
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Unable to write storage {" + name + "} file");
            }
        }
    }
}
