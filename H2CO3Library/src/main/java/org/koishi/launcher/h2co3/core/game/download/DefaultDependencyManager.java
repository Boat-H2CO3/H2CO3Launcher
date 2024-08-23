package org.koishi.launcher.h2co3.core.game.download;

import org.koishi.launcher.h2co3.core.game.download.forge.ForgeInstallTask;
import org.koishi.launcher.h2co3.core.game.download.vanilla.GameAssetDownloadTask;
import org.koishi.launcher.h2co3.core.game.download.vanilla.GameDownloadTask;
import org.koishi.launcher.h2co3.core.game.download.vanilla.GameLibrariesTask;
import org.koishi.launcher.h2co3.core.game.download.neoforge.NeoForgeInstallTask;
import org.koishi.launcher.h2co3.core.game.download.optifine.OptiFineInstallTask;
import org.koishi.launcher.h2co3.core.utils.Artifact;
import org.koishi.launcher.h2co3.core.utils.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultDependencyManager extends AbstractDependencyManager {

    private final H2CO3GameRepository repository;
    private final DownloadProvider downloadProvider;
    private final DefaultCacheRepository cacheRepository;

    public DefaultDependencyManager(H2CO3GameRepository repository, DownloadProvider downloadProvider, DefaultCacheRepository cacheRepository) {
        this.repository = repository;
        this.downloadProvider = downloadProvider;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public H2CO3GameRepository getGameRepository() {
        return repository;
    }

    @Override
    public DownloadProvider getDownloadProvider() {
        return downloadProvider;
    }

    @Override
    public DefaultCacheRepository getCacheRepository() {
        return cacheRepository;
    }

    @Override
    public GameBuilder gameBuilder() {
        return new DefaultGameBuilder(this);
    }

    @Override
    public Task<?> checkGameCompletionAsync(Version version, boolean integrityCheck) {
        return Task.allOf(
                Task.composeAsync(() -> {
                    File versionJar = repository.getVersionJar(version);
                    if (!versionJar.exists() || versionJar.length() == 0)
                        return new GameDownloadTask(this, null, version);
                    else
                        return null;
                }).thenComposeAsync(checkPatchCompletionAsync(version, integrityCheck)),
                new GameAssetDownloadTask(this, version, GameAssetDownloadTask.DOWNLOAD_INDEX_IF_NECESSARY, integrityCheck),
                new GameLibrariesTask(this, version, integrityCheck)
        );
    }

    @Override
    public Task<?> checkLibraryCompletionAsync(Version version, boolean integrityCheck) {
        return new GameLibrariesTask(this, version, integrityCheck, version.getLibraries());
    }

    @Override
    public Task<?> checkPatchCompletionAsync(Version version, boolean integrityCheck) {
        return Task.composeAsync(() -> {
            List<Task<?>> tasks = new ArrayList<>(0);

            String gameVersion = repository.getGameVersion(version).orElse(null);
            if (gameVersion == null) return null;

            Version original = repository.getVersion(version.getId());
            Version resolved = original.resolvePreservingPatches(repository);

            LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(resolved);
            for (LibraryAnalyzer.LibraryType type : LibraryAnalyzer.LibraryType.values()) {
                if (!analyzer.has(type))
                    continue;

                if (type == LibraryAnalyzer.LibraryType.OPTIFINE) {
                    String optifinePatchVersion = analyzer.getVersion(type)
                            .map(optifineVersion -> {
                                Matcher matcher = Pattern.compile("^([0-9.]+)_(?<optifine>HD_.+)$").matcher(optifineVersion);
                                return matcher.find() ? matcher.group("optifine") : optifineVersion;
                            })
                            .orElseGet(() -> resolved.getPatches().stream()
                                    .filter(patch -> "optifine".equals(patch.getId()))
                                    .findAny()
                                    .map(Version::getVersion)
                                    .orElse(null));

                    boolean needsReInstallation = version.getLibraries().stream()
                            .anyMatch(library -> !library.hasDownloadURL()
                                    && "optifine".equals(library.getGroupId())
                                    && GameLibrariesTask.shouldDownloadLibrary(repository, version, library, integrityCheck));

                    if (needsReInstallation) {
                        Library installer = new Library(new Artifact("optifine", "OptiFine", gameVersion + "_" + optifinePatchVersion, "installer"));
                        if (GameLibrariesTask.shouldDownloadLibrary(repository, version, installer, integrityCheck)) {
                            tasks.add(installLibraryAsync(gameVersion, original, "optifine", optifinePatchVersion));
                        } else {
                            tasks.add(OptiFineInstallTask.install(this, original, repository.getLibraryFile(version, installer).toPath()));
                        }
                    }
                }
            }

            return Task.allOf(tasks);
        });
    }

    @Override
    public Task<Version> installLibraryAsync(String gameVersion, Version baseVersion, String libraryId, String libraryVersion) {
        if (baseVersion.isResolved())
            throw new IllegalArgumentException("VersionMod should not be resolved");

        VersionList<?> versionList = getVersionList(libraryId);
        return Task.fromCompletableFuture(versionList.loadAsync(gameVersion))
                .thenComposeAsync(() -> installLibraryAsync(baseVersion, versionList.getVersion(gameVersion, libraryVersion)
                        .orElseThrow(() -> new IOException("Remote library " + libraryId + " has no version " + libraryVersion))))
                .withStage(String.format("h2co3.install.%s:%s", libraryId, libraryVersion));
    }

    @Override
    public Task<Version> installLibraryAsync(Version baseVersion, RemoteVersion libraryVersion) {
        if (baseVersion.isResolved())
            throw new IllegalArgumentException("VersionMod should not be resolved");

        AtomicReference<Version> removedLibraryVersion = new AtomicReference<>();

        return removeLibraryAsync(baseVersion.resolvePreservingPatches(repository), libraryVersion.getLibraryId())
                .thenComposeAsync(version -> {
                    removedLibraryVersion.set(version);
                    System.out.println("Removed " + libraryVersion.getLibraryId() + " " + version);
                    return libraryVersion.getInstallTask(this, version);
                })
                .thenApplyAsync(patch -> {
                    if (patch == null) {
                        return removedLibraryVersion.get();
                    } else {
                        return removedLibraryVersion.get().addPatch(patch);
                    }
                })
                .withStage(String.format("h2co3.install.%s:%s", libraryVersion.getLibraryId(), libraryVersion.getSelfVersion()));
    }

    public Task<Version> installLibraryAsync(Version oldVersion, Path installer) {
        if (oldVersion.isResolved())
            throw new IllegalArgumentException("VersionMod should not be resolved");

        return Task
                .composeAsync(() -> {
                    try {
                        return NeoForgeInstallTask.install(this, oldVersion, installer);
                    } catch (IOException ignore) {
                    }

                    try {
                        return ForgeInstallTask.install(this, oldVersion, installer);
                    } catch (IOException ignore) {
                    }

                    try {
                        return OptiFineInstallTask.install(this, oldVersion, installer);
                    } catch (IOException ignore) {
                    }

                    throw new UnsupportedLibraryInstallerException();
                })
                .thenApplyAsync(oldVersion::addPatch);
    }

    /**
     * Remove installed library.
     * Will try to remove libraries and patches.
     *
     * @param version   not resolved version
     * @param libraryId forge/liteloader/optifine/fabric
     * @return task to remove the specified library
     */
    public Task<Version> removeLibraryAsync(Version version, String libraryId) {
        // MaintainTask requires version that does not inherits from any version.
        // If we want to remove a library in dependent version, we should keep the dependents not changed
        // So resolving this game version to preserve all information in this version.json is necessary.
        if (version.isResolved())
            throw new IllegalArgumentException("removeLibraryWithoutSavingAsync requires non-resolved version");
        Version independentVersion = version.resolvePreservingPatches(repository);

        return Task.supplyAsync(() -> LibraryAnalyzer.analyze(independentVersion).removeLibrary(libraryId).build());
    }

    public static class UnsupportedLibraryInstallerException extends Exception {
    }

}
