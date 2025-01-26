package org.koishi.launcher.h2co3core.download.neoforge;

import org.koishi.launcher.h2co3core.download.DefaultDependencyManager;
import org.koishi.launcher.h2co3core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3core.download.RemoteVersion;
import org.koishi.launcher.h2co3core.game.Version;
import org.koishi.launcher.h2co3core.task.Task;

import java.util.List;

public class NeoForgeRemoteVersion extends RemoteVersion {
    public NeoForgeRemoteVersion(String gameVersion, String selfVersion, List<String> urls) {
        super(LibraryAnalyzer.LibraryType.NEO_FORGE.getPatchId(), gameVersion, selfVersion, null, urls);
    }

    public static String normalize(String version) {
        if (version.startsWith("1.20.1-")) {
            if (version.startsWith("forge-", "1.20.1-".length())) {
                return version.substring("1.20.1-forge-".length());
            } else {
                return version.substring("1.20.1-".length());
            }
        } else {
            return version;
        }
    }

    @Override
    public Task<Version> getInstallTask(DefaultDependencyManager dependencyManager, Version baseVersion) {
        return new NeoForgeInstallTask(dependencyManager, baseVersion, this);
    }
}
