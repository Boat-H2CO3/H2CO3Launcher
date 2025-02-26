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

import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.game.download.AbstractDependencyManager;
import org.koishi.launcher.h2co3.core.game.download.DefaultCacheRepository;
import org.koishi.launcher.h2co3.core.game.download.Library;
import org.koishi.launcher.h2co3.core.message.H2CO3MessageManager;
import org.koishi.launcher.h2co3.core.utils.DigestUtils;
import org.koishi.launcher.h2co3.core.utils.NetworkUtils;
import org.koishi.launcher.h2co3.core.utils.Pack200Utils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.io.ArtifactMalformedException;
import org.koishi.launcher.h2co3.core.utils.io.DownloadException;
import org.koishi.launcher.h2co3.core.utils.io.IOUtils;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;
import org.koishi.launcher.h2co3.core.utils.task.Task;
import org.tukaani.xz.XZInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;

public class LibraryDownloadTask extends Task<Void> {
    private FileDownloadTask task;
    protected final File jar;
    protected final DefaultCacheRepository cacheRepository;
    protected final AbstractDependencyManager dependencyManager;
    private final File xzFile;
    protected final Library library;
    protected final String url;
    protected boolean xz;
    private final Library originalLibrary;
    private boolean cached = false;

    public LibraryDownloadTask(AbstractDependencyManager dependencyManager, File file, Library library) {
        this.dependencyManager = dependencyManager;
        this.originalLibrary = library;

        setSignificance(TaskSignificance.MODERATE);

        if (library.is("net.minecraftforge", "forge"))
            library = library.setClassifier("universal");

        this.library = library;
        this.cacheRepository = dependencyManager.getCacheRepository();

        url = library.getDownload().getUrl();
        jar = file;

        xzFile = new File(file.getAbsoluteFile().getParentFile(), file.getName() + ".pack.xz");
    }

    @Override
    public Collection<Task<?>> getDependents() {
        if (cached) return Collections.emptyList();
        else return Collections.singleton(task);
    }

    @Override
    public boolean isRelyingOnDependents() {
        return false;
    }

    @Override
    public void execute() throws Exception {
        if (cached) return;

        if (!isDependentsSucceeded()) {
            // Since FileDownloadTask wraps the actual exception with DownloadException.
            // We should extract it letting the error message clearer.
            Exception t = task.getException();
            if (t instanceof DownloadException)
                throw new LibraryDownloadException(library, t.getCause());
            else if (t instanceof CancellationException)
                throw new CancellationException();
            else
                throw new LibraryDownloadException(library, t);
        } else {
            if (xz) unpackLibrary(jar, Files.readAllBytes(xzFile.toPath()));
        }
    }

    @Override
    public boolean doPreExecute() {
        return true;
    }

    @Override
    public void preExecute() {
        Optional<Path> libPath = cacheRepository.getLibrary(originalLibrary);
        if (libPath.isPresent()) {
            try {
                FileTools.copyFile(libPath.get().toFile(), jar);
                cached = true;
                return;
            } catch (IOException e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, e.getMessage());
                // We cannot copy cached file to current location
                // so we try to download a new one.
            }
        }

        if (testURLExistence(url)) {
            List<URL> urls = dependencyManager.getDownloadProvider().injectURLWithCandidates(url + ".pack.xz");
            task = new FileDownloadTask(urls, xzFile, null);
            task.setCacheRepository(cacheRepository);
            task.setCaching(true);
            xz = true;
        } else {
            List<URL> urls = dependencyManager.getDownloadProvider().injectURLWithCandidates(url);
            task = new FileDownloadTask(urls, jar,
                    library.getDownload().getSha1() != null ? new FileDownloadTask.IntegrityCheck("SHA-1", library.getDownload().getSha1()) : null);
            task.setCacheRepository(cacheRepository);
            task.setCaching(true);
            task.addIntegrityCheckHandler(FileDownloadTask.ZIP_INTEGRITY_CHECK_HANDLER);
            xz = false;
        }
    }

    private boolean testURLExistence(String rawUrl) {
        List<URL> urls = dependencyManager.getDownloadProvider().injectURLWithCandidates(rawUrl);
        for (URL url : urls) {
            URL rawURL = NetworkUtils.toURL(url.toString());
            URL xzURL = NetworkUtils.toURL(url + ".pack.xz");
            for (int retry = 0; retry < 3; retry++) {
                try {
                    if (NetworkUtils.urlExists(rawURL))
                        return false;
                    return NetworkUtils.urlExists(xzURL);
                } catch (IOException e) {
                    H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, "Failed to test for url existence: " + url + ".pack.xz" + e);
                }
            }
        }
        return false; // maybe some ugly implementation will give timeout for not existent url.
    }

    @Override
    public boolean doPostExecute() {
        return true;
    }

    @Override
    public void postExecute() throws Exception {
        if (!cached) {
            try {
                cacheRepository.cacheLibrary(library, jar.toPath(), xz);
            } catch (IOException e) {
                H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.ERROR, "Failed to cache downloaded library " + library + e);
            }
        }
    }

    public static boolean checksumValid(File libPath, List<String> checksums) {
        try {
            if (checksums == null || checksums.isEmpty()) {
                return true;
            }
            byte[] fileData = Files.readAllBytes(libPath.toPath());
            boolean valid = checksums.contains(DigestUtils.digestToString("SHA-1", fileData));
            if (!valid && libPath.getName().endsWith(".jar")) {
                valid = validateJar(fileData, checksums);
            }
            return valid;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean validateJar(byte[] data, List<String> checksums) throws IOException {
        HashMap<String, String> files = new HashMap<>();
        String[] hashes = null;
        JarInputStream jar = new JarInputStream(new ByteArrayInputStream(data));
        JarEntry entry = jar.getNextJarEntry();
        while (entry != null) {
            byte[] eData = IOUtils.readFullyWithoutClosing(jar);
            if (entry.getName().equals("checksums.sha1")) {
                hashes = new String(eData, StandardCharsets.UTF_8).split("\n");
            }
            if (!entry.isDirectory()) {
                files.put(entry.getName(), DigestUtils.digestToString("SHA-1", eData));
            }
            entry = jar.getNextJarEntry();
        }
        jar.close();
        if (hashes != null) {
            boolean failed = !checksums.contains(files.get("checksums.sha1"));
            if (!failed) {
                for (String hash : hashes) {
                    if ((!hash.trim().equals("")) && (hash.contains(" "))) {
                        String[] e = hash.split(" ");
                        String validChecksum = e[0];
                        String target = hash.substring(validChecksum.length() + 1);
                        String checksum = files.get(target);
                        if ((!files.containsKey(target)) || (checksum == null)) {
                            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.WARNING, target + " : missing");
                            failed = true;
                            break;
                        } else if (!checksum.equals(validChecksum)) {
                            H2CO3Tools.showMessage(H2CO3MessageManager.NotificationItem.Type.WARNING, target + " : failed (" + checksum + ", " + validChecksum + ")");
                            failed = true;
                            break;
                        }
                    }
                }
            }
            return !failed;
        }
        return false;
    }

    private static void unpackLibrary(File dest, byte[] src) throws IOException {
        if (dest.exists())
            if (!dest.delete())
                throw new IOException("Unable to delete file " + dest);

        byte[] decompressed;
        try {
            decompressed = IOUtils.readFullyAsByteArray(new XZInputStream(new ByteArrayInputStream(src)));
        } catch (IOException e) {
            throw new ArtifactMalformedException("Library " + dest + " is malformed");
        }

        String end = new String(decompressed, decompressed.length - 4, 4);
        if (!end.equals("SIGN"))
            throw new IOException("Unpacking failed, signature missing " + end);

        int x = decompressed.length;
        int len = decompressed[(x - 8)] & 0xFF | (decompressed[(x - 7)] & 0xFF) << 8 | (decompressed[(x - 6)] & 0xFF) << 16 | (decompressed[(x - 5)] & 0xFF) << 24;

        Path temp = Files.createTempFile("minecraft", ".pack");

        byte[] checksums = Arrays.copyOfRange(decompressed, decompressed.length - len - 8, decompressed.length - 8);

        try (OutputStream out = Files.newOutputStream(temp)) {
            out.write(decompressed, 0, decompressed.length - len - 8);
        }

        try (FileOutputStream jarBytes = new FileOutputStream(dest); JarOutputStream jos = new JarOutputStream(jarBytes)) {
            Pack200Utils.unpack(H2CO3Tools.NATIVE_LIB_DIR, temp.toAbsolutePath().toString(), dest.getAbsolutePath());

            JarEntry checksumsFile = new JarEntry("checksums.sha1");
            checksumsFile.setTime(0L);
            jos.putNextEntry(checksumsFile);
            jos.write(checksums);
            jos.closeEntry();
        }
    }
}
