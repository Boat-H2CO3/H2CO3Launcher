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
package org.koishi.launcher.h2co3.core.game.mod.server;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.koishi.launcher.h2co3.core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.download.DefaultGameRepository;
import org.koishi.launcher.h2co3.core.download.GameBuilder;
import org.koishi.launcher.h2co3.core.game.mod.ModpackConfiguration;
import org.koishi.launcher.h2co3.core.utils.DigestUtils;
import org.koishi.launcher.h2co3.core.utils.Logging;
import org.koishi.launcher.h2co3.core.utils.NetworkUtils;
import org.koishi.launcher.h2co3.core.utils.StringUtils;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;
import org.koishi.launcher.h2co3.core.utils.task.GetTask;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ServerModpackCompletionTask extends Task<Void> {

    private final DefaultDependencyManager dependencyManager;
    private final DefaultGameRepository repository;
    private final String version;
    private final List<Task<?>> dependencies = new ArrayList<>();
    private ModpackConfiguration<ServerModpackManifest> manifest;
    private GetTask dependent;
    private ServerModpackManifest remoteManifest;

    public ServerModpackCompletionTask(DefaultDependencyManager dependencyManager, String version) {
        this(dependencyManager, version, null);
    }

    public ServerModpackCompletionTask(DefaultDependencyManager dependencyManager, String version, ModpackConfiguration<ServerModpackManifest> manifest) {
        this.dependencyManager = dependencyManager;
        this.repository = dependencyManager.getGameRepository();
        this.version = version;

        if (manifest == null) {
            try {
                File manifestFile = repository.getModpackConfiguration(version);
                if (manifestFile.exists()) {
                    this.manifest = JsonUtils.GSON.fromJson(FileTools.readText(manifestFile), new TypeToken<ModpackConfiguration<ServerModpackManifest>>() {
                    }.getType());
                }
            } catch (Exception e) {
                Logging.LOG.log(Level.WARNING, "Unable to read Server modpack manifest.json", e);
            }
        } else {
            this.manifest = manifest;
        }

        setStage("h2co3.modpack.download");
    }

    @Override
    public boolean doPreExecute() {
        return true;
    }

    @Override
    public void preExecute() throws Exception {
        if (manifest == null || StringUtils.isBlank(manifest.getManifest().getFileApi())) return;
        dependent = new GetTask(new URL(manifest.getManifest().getFileApi() + "/server-manifest.json"));
    }

    @Override
    public Collection<Task<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public Collection<Task<?>> getDependents() {
        return dependent == null ? Collections.emptySet() : Collections.singleton(dependent);
    }

    private Map<String, String> toMap(Collection<ServerModpackManifest.Addon> addons) {
        return addons.stream().collect(Collectors.toMap(ServerModpackManifest.Addon::getId, ServerModpackManifest.Addon::getVersion));
    }

    @Override
    public void execute() throws Exception {
        if (manifest == null || StringUtils.isBlank(manifest.getManifest().getFileApi())) return;

        try {
            remoteManifest = JsonUtils.fromNonNullJson(dependent.getResult(), ServerModpackManifest.class);
        } catch (JsonParseException e) {
            throw new IOException(e);
        }

        Map<String, String> oldAddons = toMap(manifest.getManifest().getAddons());
        Map<String, String> newAddons = toMap(remoteManifest.getAddons());
        if (!Objects.equals(oldAddons, newAddons)) {
            GameBuilder builder = dependencyManager.gameBuilder().name(version);
            for (ServerModpackManifest.Addon addon : remoteManifest.getAddons()) {
                builder.version(addon.getId(), addon.getVersion());
            }

            dependencies.add(builder.buildAsync());
        }

        Path rootPath = repository.getVersionRoot(version).toPath();
        Map<String, ModpackConfiguration.FileInformation> files = manifest.getManifest().getFiles().stream()
                .collect(Collectors.toMap(ModpackConfiguration.FileInformation::getPath,
                        Function.identity()));

        Set<String> remoteFiles = remoteManifest.getFiles().stream().map(ModpackConfiguration.FileInformation::getPath)
                .collect(Collectors.toSet());

        int total = 0;
        // for files in new modpack
        for (ModpackConfiguration.FileInformation file : remoteManifest.getFiles()) {
            Path actualPath = rootPath.resolve(file.getPath());
            boolean download;
            if (!files.containsKey(file.getPath())) {
                // If old modpack does not have this entry, download it
                download = true;
            } else if (!Files.exists(actualPath)) {
                // If both old and new modpacks have this entry, but the file is missing...
                // Re-download it since network problem may cause file missing
                download = true;
            } else {
                // If user modified this entry file, we will not replace this file since this modified file is that user expects.
                String fileHash = DigestUtils.digestToString("SHA-1", actualPath);
                String oldHash = files.get(file.getPath()).getHash();
                download = !Objects.equals(oldHash, file.getHash()) && Objects.equals(oldHash, fileHash);
            }

            if (download) {
                total++;
                dependencies.add(new FileDownloadTask(
                        new URL(remoteManifest.getFileApi() + "/overrides/" + NetworkUtils.encodeLocation(file.getPath())),
                        actualPath.toFile(),
                        new FileDownloadTask.IntegrityCheck("SHA-1", file.getHash()))
                        .withCounter("h2co3.modpack.download"));
            }
        }

        // If old modpack have this entry, and new modpack deleted it. Delete this file.
        for (ModpackConfiguration.FileInformation file : manifest.getManifest().getFiles()) {
            Path actualPath = rootPath.resolve(file.getPath());
            if (Files.exists(actualPath) && !remoteFiles.contains(file.getPath()))
                Files.deleteIfExists(actualPath);
        }

        getProperties().put("total", dependencies.size());
        notifyPropertiesChanged();
    }

    @Override
    public boolean doPostExecute() {
        return true;
    }

    @Override
    public void postExecute() throws Exception {
        if (manifest == null || StringUtils.isBlank(manifest.getManifest().getFileApi())) return;
        File manifestFile = repository.getModpackConfiguration(version);
        FileTools.writeText(manifestFile, JsonUtils.GSON.toJson(new ModpackConfiguration<>(remoteManifest, this.manifest.getType(), this.manifest.getName(), this.manifest.getVersion(), remoteManifest.getFiles())));
    }
}