package org.koishi.launcher.h2co3.core.download.neoforge;

import static org.koishi.launcher.h2co3.core.utils.Lang.wrap;

import org.koishi.launcher.h2co3.core.download.DownloadProvider;
import org.koishi.launcher.h2co3.core.download.VersionList;
import org.koishi.launcher.h2co3.core.utils.HttpRequest;
import org.koishi.launcher.h2co3.core.utils.Lang;
import org.koishi.launcher.h2co3.core.utils.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class NeoForgeOfficialVersionList extends VersionList<NeoForgeRemoteVersion> {
    private static final String OLD_URL = "https://maven.neoforged.net/api/maven/versions/releases/net/neoforged/forge";
    private static final String META_URL = "https://maven.neoforged.net/api/maven/versions/releases/net/neoforged/neoforge";
    private final DownloadProvider downloadProvider;

    public NeoForgeOfficialVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public Optional<NeoForgeRemoteVersion> getVersion(String gameVersion, String remoteVersion) {
        if (gameVersion.equals("1.20.1")) {
            remoteVersion = NeoForgeRemoteVersion.fixInvalidVersion(remoteVersion);
            if (!remoteVersion.equals("47.1.82")) {
                remoteVersion = "1.20.1-" + remoteVersion;
            }
        }
        return super.getVersion(gameVersion, remoteVersion);
    }

    @Override
    public CompletableFuture<?> refreshAsync() {
        return CompletableFuture.supplyAsync(wrap(() -> new OfficialAPIResult[]{
                HttpRequest.GET(downloadProvider.injectURL(OLD_URL)).getJson(OfficialAPIResult.class),
                HttpRequest.GET(downloadProvider.injectURL(META_URL)).getJson(OfficialAPIResult.class)
        })).thenAccept(results -> {
            lock.writeLock().lock();

            try {
                versions.clear();

                for (String version : results[0].versions) {
                    versions.put("1.20.1", new NeoForgeRemoteVersion(
                            "1.20.1", StringUtils.removePrefix(version, "1.20.1-"),
                            Lang.immutableListOf(
                                    downloadProvider.injectURL("https://maven.neoforged.net/releases/net/neoforged/forge/" + version + "/forge-" + version + "-installer.jar")
                            )
                    ));
                }

                for (String version : results[1].versions) {
                    String mcVersion = "1." + version.substring(0, version.indexOf('.', version.indexOf('.') + 1));
                    versions.put(mcVersion, new NeoForgeRemoteVersion(
                            mcVersion, version,
                            Lang.immutableListOf(
                                    downloadProvider.injectURL("https://maven.neoforged.net/releases/net/neoforged/neoforge/" + version + "/neoforge-" + version + "-installer.jar")
                            )
                    ));
                }
            } finally {
                lock.writeLock().unlock();
            }
        });
    }

    private static final class OfficialAPIResult {

        private final List<String> versions;

        public OfficialAPIResult(boolean isSnapshot, List<String> versions) {
            this.versions = versions;
        }
    }
}