package org.koishi.launcher.h2co3.ui.version;

import static org.koishi.launcher.h2co3core.download.LibraryAnalyzer.LibraryType.MINECRAFT;
import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonParseException;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.H2CO3LauncherGameRepository;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.mod.ModpackConfiguration;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class VersionList {

    private final Context context;
    private final H2CO3RecyclerView listView;
    private final H2CO3LauncherButton refreshButton;
    private final H2CO3LauncherProgressBar progressBar;

    public VersionList(Context context, H2CO3RecyclerView listView, H2CO3LauncherButton refreshButton, H2CO3LauncherProgressBar progressBar) {
        this.context = context;
        this.listView = listView;
        this.refreshButton = refreshButton;
        this.progressBar = progressBar;

        Profiles.registerVersionsListener(this::loadVersions);
    }

    private void loadVersions(Profile profile) {
        Schedulers.androidUIThread().execute(() -> {
            refreshButton.setEnabled(false);
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        });
        H2CO3LauncherGameRepository repository = profile.getRepository();
        Schedulers.defaultScheduler().execute(()->{
            if (profile == Profiles.getSelectedProfile()) {
                List<VersionListItem> children = repository.getDisplayVersions()
                        .parallel()
                        .map(version -> {
                            String game = profile.getRepository().getGameVersion(version.getId()).orElse(context.getString(R.string.message_unknown));
                            StringBuilder libraries = new StringBuilder(game);
                            LibraryAnalyzer analyzer = LibraryAnalyzer.analyze(repository.getResolvedPreservingPatchesVersion(version.getId()));
                            for (LibraryAnalyzer.LibraryMark mark : analyzer) {
                                String libraryId = mark.getLibraryId();
                                String libraryVersion = mark.getLibraryVersion();
                                if (libraryId.equals(MINECRAFT.getPatchId())) continue;
                                if (AndroidUtils.hasStringId(context, "install_installer_" + libraryId.replace("-", "_"))) {
                                    libraries.append(", ").append(AndroidUtils.getLocalizedText(context, "install_installer_" + libraryId.replace("-", "_")));
                                    if (libraryVersion != null)
                                        libraries.append(": ").append(libraryVersion.replaceAll("(?i)" + libraryId, ""));
                                }
                            }
                            String tag = null;
                            try {
                                ModpackConfiguration<?> config = profile.getRepository().readModpackConfiguration(version.getId());
                                if (config != null)
                                    tag = config.getVersion();
                            } catch (IOException | JsonParseException e) {
                                LOG.log(Level.WARNING, "Failed to read modpack configuration from " + version, e);
                            }
                            return new VersionListItem(profile, version.getId(), libraries.toString(), tag, repository.getVersionIconImage(version.getId()));
                        })
                        .collect(Collectors.toList());
                Schedulers.androidUIThread().execute(() -> {
                    listView.setLayoutManager(new LinearLayoutManager(context));
                    VersionListAdapter adapter = new VersionListAdapter(context, (ArrayList<VersionListItem>) children);
                    listView.setAdapter(adapter);
                    refreshButton.setEnabled(true);
                    listView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                });
                children.forEach(it -> it.selectedProperty().bind(Bindings.createBooleanBinding(() -> profile.selectedVersionProperty().get().equals(it.getVersion()), profile.selectedVersionProperty())));
            }
        });
    }

    public void refreshList() {
        Profiles.getSelectedProfile().getRepository().refreshVersionsAsync().start();
    }
}