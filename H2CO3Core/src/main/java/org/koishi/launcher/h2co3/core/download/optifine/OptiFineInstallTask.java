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
package org.koishi.launcher.h2co3.core.download.optifine;


import static org.koishi.launcher.h2co3.core.utils.Lang.getOrDefault;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import org.jenkinsci.constant_pool_scanner.ConstantPool;
import org.jenkinsci.constant_pool_scanner.ConstantPoolScanner;
import org.jenkinsci.constant_pool_scanner.ConstantType;
import org.jenkinsci.constant_pool_scanner.Utf8Constant;
import org.koishi.launcher.h2co3.core.H2CO3Tools;
import org.koishi.launcher.h2co3.core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3.core.download.DefaultGameRepository;
import org.koishi.launcher.h2co3.core.download.LibrariesDownloadInfo;
import org.koishi.launcher.h2co3.core.download.Library;
import org.koishi.launcher.h2co3.core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3.core.download.LibraryDownloadInfo;
import org.koishi.launcher.h2co3.core.download.Version;
import org.koishi.launcher.h2co3.core.download.VersionNumber;
import org.koishi.launcher.h2co3.core.shell.ProcessService;
import org.koishi.launcher.h2co3.core.utils.Arguments;
import org.koishi.launcher.h2co3.core.utils.Artifact;
import org.koishi.launcher.h2co3.core.utils.CommandBuilder;
import org.koishi.launcher.h2co3.core.utils.SocketServer;
import org.koishi.launcher.h2co3.core.utils.file.FileTools;
import org.koishi.launcher.h2co3.core.utils.io.CompressingUtils;
import org.koishi.launcher.h2co3.core.utils.io.UnsupportedInstallationException;
import org.koishi.launcher.h2co3.core.utils.io.VersionMismatchException;
import org.koishi.launcher.h2co3.core.utils.task.FileDownloadTask;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * <b>Note</b>: OptiFine should be installed in the end.
 */
public final class OptiFineInstallTask extends Task<Version> {

    private final DefaultGameRepository gameRepository;
    private final DefaultDependencyManager dependencyManager;
    private final Version version;
    private final OptiFineRemoteVersion remote;
    private final Path installer;
    private final List<Task<?>> dependents = new ArrayList<>(0);
    private final List<Task<?>> dependencies = new ArrayList<>(1);
    private final Library optiFineLibrary;
    private final Library optiFineInstallerLibrary;
    private Path dest;

    public OptiFineInstallTask(DefaultDependencyManager dependencyManager, Version version, OptiFineRemoteVersion remoteVersion) {
        this(dependencyManager, version, remoteVersion, null);
    }

    public OptiFineInstallTask(DefaultDependencyManager dependencyManager, Version version, OptiFineRemoteVersion remoteVersion, Path installer) {
        this.dependencyManager = dependencyManager;
        this.gameRepository = dependencyManager.getGameRepository();
        this.version = version;
        this.remote = remoteVersion;
        this.installer = installer;

        String mavenVersion = remote.getGameVersion() + "_" + remote.getSelfVersion();

        optiFineLibrary = new Library(new Artifact("optifine", "OptiFine", mavenVersion));

        optiFineInstallerLibrary = new Library(
                new Artifact("optifine", "OptiFine", mavenVersion, "installer"), null,
                new LibrariesDownloadInfo(new LibraryDownloadInfo(
                        "optifine/OptiFine/" + mavenVersion + "/OptiFine-" + mavenVersion + "-installer.jar",
                        remote.getUrls().get(0)))
        );
    }

    /**
     * Install OptiFine library from existing local file.
     *
     * @param dependencyManager game repository
     * @param version           version.json
     * @param installer         the OptiFine installer
     * @return the task to install library
     * @throws IOException              if unable to read compressed content of installer file, or installer file is corrupted, or the installer is not the one we want.
     * @throws VersionMismatchException if required game version of installer does not match the actual one.
     */
    public static Task<Version> install(DefaultDependencyManager dependencyManager, Version version, Path installer) throws IOException, VersionMismatchException {
        Optional<String> gameVersion = dependencyManager.getGameRepository().getGameVersion(version);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (gameVersion.isEmpty()) throw new IOException();
        }
        try (FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(installer)) {
            Path configClass = fs.getPath("Config.class");
            if (!Files.exists(configClass)) configClass = fs.getPath("net/optifine/Config.class");
            if (!Files.exists(configClass))
                configClass = fs.getPath("notch/net/optifine/Config.class");
            if (!Files.exists(configClass)) throw new IOException("Unrecognized installer");
            ConstantPool pool = ConstantPoolScanner.parse(Files.readAllBytes(configClass), ConstantType.UTF8);
            List<String> constants = new ArrayList<>();
            pool.list(Utf8Constant.class).forEach(utf8 -> constants.add(utf8.get()));
            String mcVersion = getOrDefault(constants, constants.indexOf("MC_VERSION") + 1, null);
            String ofEdition = getOrDefault(constants, constants.indexOf("OF_EDITION") + 1, null);
            String ofRelease = getOrDefault(constants, constants.indexOf("OF_RELEASE") + 1, null);

            if (mcVersion == null || ofEdition == null || ofRelease == null)
                throw new IOException("Unrecognized OptiFine installer");

            if (!mcVersion.equals(gameVersion.get()))
                throw new VersionMismatchException(mcVersion, gameVersion.get());

            return new OptiFineInstallTask(dependencyManager, version,
                    new OptiFineRemoteVersion(mcVersion, ofEdition + "_" + ofRelease, Collections.singletonList(""), false), installer);
        }
    }

    @Override
    public boolean doPreExecute() {
        return true;
    }

    @Override
    public void preExecute() throws Exception {
        dest = Files.createTempFile("optifine-installer", ".jar");

        if (installer == null) {
            FileDownloadTask task = new FileDownloadTask(
                    dependencyManager.getDownloadProvider().injectURLsWithCandidates(remote.getUrls()),
                    dest.toFile(), null);
            task.setCacheRepository(dependencyManager.getCacheRepository());
            task.setCaching(true);
            dependents.add(task);
        } else {
            FileTools.copyFile(installer, dest);
        }
    }

    @Override
    public Collection<Task<?>> getDependents() {
        return dependents;
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
    public void execute() throws Exception {
        String originalMainClass = version.resolve(dependencyManager.getGameRepository()).getMainClass();
        if (!LibraryAnalyzer.VANILLA_MAIN.equals(originalMainClass) &&
                !LibraryAnalyzer.LAUNCH_WRAPPER_MAIN.equals(originalMainClass) &&
                !LibraryAnalyzer.MOD_LAUNCHER_MAIN.equals(originalMainClass) &&
                !LibraryAnalyzer.BOOTSTRAP_LAUNCHER_MAIN.equals(originalMainClass))
            throw new UnsupportedInstallationException(UnsupportedInstallationException.UNSUPPORTED_LAUNCH_WRAPPER);

        List<Library> libraries = new ArrayList<>(4);
        libraries.add(optiFineLibrary);

        FileTools.copyFile(dest, gameRepository.getLibraryFile(version, optiFineInstallerLibrary).toPath());

        // Install launch wrapper modified by OptiFine
        boolean hasLaunchWrapper = false;
        try (FileSystem fs = CompressingUtils.createReadOnlyZipFileSystem(dest)) {
            if (Files.exists(fs.getPath("optifine/Patcher.class"))) {
                String[] command = {
                        "-cp",
                        dest.toString(),
                        "optifine.Patcher",
                        gameRepository.getVersionJar(version).getAbsolutePath(),
                        dest.toString(),
                        gameRepository.getLibraryFile(version, optiFineLibrary).toString()
                };
                runJVMProcess(command, 8);
            } else {
                FileTools.copyFile(dest, gameRepository.getLibraryFile(version, optiFineLibrary).toPath());
            }

            Path launchWrapper2 = fs.getPath("launchwrapper-2.0.jar");
            if (Files.exists(launchWrapper2)) {
                Library launchWrapper = new Library(new Artifact("optifine", "launchwrapper", "2.0"));
                File launchWrapperFile = gameRepository.getLibraryFile(version, launchWrapper);
                FileTools.makeDirectory(Objects.requireNonNull(launchWrapperFile.getAbsoluteFile().getParentFile()));
                FileTools.copyFile(launchWrapper2, launchWrapperFile.toPath());
                hasLaunchWrapper = true;
                libraries.add(launchWrapper);
            }

            Path launchWrapperVersionText = fs.getPath("launchwrapper-of.txt");
            if (Files.exists(launchWrapperVersionText)) {
                String launchWrapperVersion = FileTools.readText(launchWrapperVersionText).trim();
                Path launchWrapperJar = fs.getPath("launchwrapper-of-" + launchWrapperVersion + ".jar");

                Library launchWrapper = new Library(new Artifact("optifine", "launchwrapper-of", launchWrapperVersion));

                if (Files.exists(launchWrapperJar)) {
                    File launchWrapperFile = gameRepository.getLibraryFile(version, launchWrapper);
                    FileTools.makeDirectory(Objects.requireNonNull(launchWrapperFile.getAbsoluteFile().getParentFile()));
                    FileTools.copyFile(launchWrapperJar, launchWrapperFile.toPath());

                    hasLaunchWrapper = true;
                    libraries.add(launchWrapper);
                }
            }

            Path buildofText = fs.getPath("buildof.txt");
            if (Files.exists(buildofText)) {
                String buildof = FileTools.readText(buildofText).trim();
                VersionNumber buildofVer = VersionNumber.asVersion(buildof);

                if (LibraryAnalyzer.BOOTSTRAP_LAUNCHER_MAIN.equals(originalMainClass)) {
                    // OptiFine H1 Pre2+ is compatible with Forge 1.17
                    if (buildofVer.compareTo(VersionNumber.asVersion("20210924-190833")) < 0) {
                        throw new UnsupportedInstallationException(UnsupportedInstallationException.FORGE_1_17_OPTIFINE_H1_PRE2);
                    }
                }
            }
        }

        if (!hasLaunchWrapper) {
            libraries.add(new Library(new Artifact("net.minecraft", "launchwrapper", "1.12")));
        }

        setResult(new Version(
                LibraryAnalyzer.LibraryType.OPTIFINE.getPatchId(),
                remote.getSelfVersion(),
                10000,
                new Arguments().addGameArguments("--tweakClass", "optifine.OptiFineTweaker"),
                LibraryAnalyzer.LAUNCH_WRAPPER_MAIN,
                libraries
        ));

        dependencies.add(dependencyManager.checkLibraryCompletionAsync(getResult(), true));
    }

    private void runJVMProcess(String[] command, int java) throws Exception {
        int exitCode;
        boolean listen = true;
        while (listen) {
            if (((ActivityManager) H2CO3Tools.CONTEXT.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses().size() == 1) {
                listen = false;
            }
        }
        CountDownLatch latch = new CountDownLatch(1);
        SocketServer server = new SocketServer("127.0.0.1", ProcessService.PROCESS_SERVICE_PORT, (server1, msg) -> {
            server1.setResult(msg);
            server1.stop();
            latch.countDown();
        });
        Intent service = new Intent(H2CO3Tools.CONTEXT, ProcessService.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray("command", command);
        bundle.putInt("java", java);
        service.putExtras(bundle);
        H2CO3Tools.CONTEXT.startService(service);
        server.start();
        latch.await();
        exitCode = Integer.parseInt((String) server.getResult());
        if (exitCode != 0) {
            if (java == 8) {
                runJVMProcess(command, 17);
            } else if (java == 17) {
                runJVMProcess(command, 11);
            } else if (java == 11) {
                runJVMProcess(command, 21);
            } else {
                throw new IOException("OptiFine patcher failed, command: " + new CommandBuilder().addAll(Arrays.asList(command)));
            }
        }
    }
}
