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
package org.koishi.launcher.h2co3.core.download.quilt;

import static org.koishi.launcher.h2co3.core.utils.io.UnsupportedInstallationException.FABRIC_NOT_COMPATIBLE_WITH_FORGE;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.koishi.launcher.h2co3.core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.download.Library;
import org.koishi.launcher.h2co3.core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.utils.Arguments;
import org.koishi.launcher.h2co3.core.utils.Artifact;
import org.koishi.launcher.h2co3.core.utils.gson.JsonUtils;
import org.koishi.launcher.h2co3.core.utils.io.UnsupportedInstallationException;
import org.koishi.launcher.h2co3.core.utils.task.GetTask;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <b>Note</b>: Quilt should be installed first.
 */
public final class QuiltInstallTask extends Task<Version> {

    private final DefaultDependencyManager dependencyManager;
    private final Version version;
    private final QuiltRemoteVersion remote;
    private final GetTask launchMetaTask;
    private final List<Task<?>> dependencies = new ArrayList<>(1);

    public QuiltInstallTask(DefaultDependencyManager dependencyManager, Version version, QuiltRemoteVersion remoteVersion) {
        this.dependencyManager = dependencyManager;
        this.version = version;
        this.remote = remoteVersion;

        launchMetaTask = new GetTask(dependencyManager.getDownloadProvider().injectURLsWithCandidates(remoteVersion.getUrls()));
        launchMetaTask.setCacheRepository(dependencyManager.getCacheRepository());
    }

    private static String getMavenRepositoryByGroup(String maven) {
        Artifact artifact = Artifact.fromDescriptor(maven);
        return switch (artifact.getGroup()) {
            case "net.fabricmc" -> "https://maven.fabricmc.net/";
            case "org.quiltmc" -> "https://maven.quiltmc.org/repository/release/";
            default -> "https://maven.fabricmc.net/";
        };
    }

    @Override
    public boolean doPreExecute() {
        return true;
    }

    @Override
    public void preExecute() throws Exception {
        if (!Objects.equals("net.minecraft.client.main.Main", version.resolve(dependencyManager.getGameRepository()).getMainClass()))
            throw new UnsupportedInstallationException(FABRIC_NOT_COMPATIBLE_WITH_FORGE);
    }

    @Override
    public Collection<Task<?>> getDependents() {
        return Collections.singleton(launchMetaTask);
    }

    @Override
    public Collection<Task<?>> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean isRelyingOnDependencies() {
        return false;
    }

    @Override
    public void execute() {
        setResult(getPatch(JsonUtils.GSON.fromJson(launchMetaTask.getResult(), QuiltInfo.class), remote.getGameVersion(), remote.getSelfVersion()));

        dependencies.add(dependencyManager.checkLibraryCompletionAsync(getResult(), true));
    }

    private Version getPatch(QuiltInfo quiltInfo, String gameVersion, String loaderVersion) {
        JsonObject launcherMeta = quiltInfo.launcherMeta;
        Arguments arguments = new Arguments();

        String mainClass;
        if (!launcherMeta.get("mainClass").isJsonObject()) {
            mainClass = launcherMeta.get("mainClass").getAsString();
        } else {
            mainClass = launcherMeta.get("mainClass").getAsJsonObject().get("client").getAsString();
        }

        if (launcherMeta.has("launchwrapper")) {
            String clientTweaker = launcherMeta.get("launchwrapper").getAsJsonObject().get("tweakers").getAsJsonObject().get("client").getAsJsonArray().get(0).getAsString();
            arguments = arguments.addGameArguments("--tweakClass", clientTweaker);
        }

        JsonObject librariesObject = launcherMeta.getAsJsonObject("libraries");
        List<Library> libraries = new ArrayList<>();

        // "common, server" is hard coded in fabric installer.
        // Don't know the purpose of ignoring client libraries.
        for (String side : new String[]{"common", "server"}) {
            for (JsonElement element : librariesObject.getAsJsonArray(side)) {
                libraries.add(JsonUtils.GSON.fromJson(element, Library.class));
            }
        }

        // libraries.add(new Library(Artifact.fromDescriptor(quiltInfo.hashed.maven), getMavenRepositoryByGroup(quiltInfo.hashed.maven), null));
        libraries.add(new Library(Artifact.fromDescriptor(quiltInfo.intermediary.maven), getMavenRepositoryByGroup(quiltInfo.intermediary.maven), null));
        libraries.add(new Library(Artifact.fromDescriptor(quiltInfo.loader.maven), getMavenRepositoryByGroup(quiltInfo.loader.maven), null));

        return new Version(LibraryAnalyzer.LibraryType.QUILT.getPatchId(), loaderVersion, 30000, arguments, mainClass, libraries);
    }

    public record QuiltInfo(LoaderInfo loader, IntermediaryInfo hashed,
                            IntermediaryInfo intermediary, JsonObject launcherMeta) {
    }

    public record LoaderInfo(String separator, int build, String maven, String version,
                             boolean stable) {
    }

    public record IntermediaryInfo(String maven, String version) {
    }
}
