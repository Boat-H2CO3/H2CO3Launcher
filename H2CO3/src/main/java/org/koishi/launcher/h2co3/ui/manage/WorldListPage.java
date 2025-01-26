package org.koishi.launcher.h2co3.ui.manage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ListView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleListProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.game.World;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldListPage extends H2CO3LauncherCommonPage implements ManageUI.VersionLoadable, View.OnClickListener {

    private final ListProperty<WorldListItem> itemsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final BooleanProperty showAll = new SimpleBooleanProperty(this, "showAll", false);

    private Path savesDir;
    private List<World> worlds;
    private Profile profile;
    private String id;
    private String gameVersion;

    private H2CO3LauncherCheckBox showAllCheckBox;
    private H2CO3LauncherButton addButton;
    private H2CO3LauncherButton refreshButton;
    private ListView listView;
    private H2CO3LauncherProgressBar progressBar;

    public WorldListPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);

        create();
    }

    public void create() {
        showAllCheckBox = findViewById(R.id.show_all);
        addButton = findViewById(R.id.add);
        refreshButton = findViewById(R.id.refresh);
        listView = findViewById(R.id.list);
        progressBar = findViewById(R.id.progress);

        showAll.addListener(e -> {
            if (worlds != null)
                itemsProperty.setAll(worlds.stream()
                        .filter(world -> isShowAll() || world.getGameVersion() == null || world.getGameVersion().equals(gameVersion))
                        .map(it -> new WorldListItem(getContext(), getActivity(), getParent(), it)).collect(Collectors.toList()));
        });

        showAllCheckBox.addCheckedChangeListener();
        showAllCheckBox.checkProperty().bindBidirectional(showAllProperty());
        addButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);

        WorldListAdapter adapter = new WorldListAdapter(getContext());
        adapter.listProperty().bind(itemsProperty);
        listView.setAdapter(adapter);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void loadVersion(Profile profile, String version) {
        this.profile = profile;
        this.id = version;
        this.savesDir = profile.getRepository().getRunDirectory(id).toPath().resolve("saves");
        refresh();
    }

    @Override
    public void onClick(View v) {
        if (v == addButton) {
            add();
        }
        if (v == refreshButton) {
            refresh();
        }
    }

    public CompletableFuture<?> refresh() {
        if (profile == null || id == null)
            return CompletableFuture.completedFuture(null);

        setLoading(true);
        return CompletableFuture
                .runAsync(() -> gameVersion = profile.getRepository().getGameVersion(id).orElse(null))
                .thenApplyAsync(unused -> {
                    try (Stream<World> stream = World.getWorlds(savesDir)) {
                        return stream.parallel().collect(Collectors.toList());
                    }
                })
                .whenCompleteAsync((result, exception) -> {
                    worlds = result;
                    setLoading(false);
                    if (exception == null)
                        itemsProperty.setAll(result.stream()
                                .filter(world -> isShowAll() || world.getGameVersion() == null || world.getGameVersion().equals(gameVersion))
                                .map(it -> new WorldListItem(getContext(), getActivity(), getParent(), it)).collect(Collectors.toList()));

                    System.gc();
                }, Schedulers.androidUIThread());
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        listView.setVisibility(loading ? View.GONE : View.VISIBLE);
        showAllCheckBox.setEnabled(!loading);
        addButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
    }

    public void add() {
        FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
        builder.setLibMode(LibMode.FILE_CHOOSER);
        builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        ArrayList<String> suffix = new ArrayList<>();
        suffix.add(".zip");
        builder.setSuffix(suffix);
        builder.create().browse(getActivity(), RequestCodes.SELECT_WORLD_CODE, ((requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_WORLD_CODE && resultCode == Activity.RESULT_OK && data != null) {
                String path = FileBrowser.getSelectedFiles(data).get(0);
                Uri uri = Uri.parse(path);
                if (AndroidUtils.isDocUri(uri)) {
                    path = AndroidUtils.copyFileToDir(getActivity(), uri, new File(H2CO3LauncherTools.CACHE_DIR));
                }
                File file = new File(path);
                installWorld(file);
            }
        }));
    }

    private void installWorld(File zipFile) {
        // Only accept one world file because user is required to confirm the new world name
        // Or too many input dialogs are popped.
        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
        builder.setMessage(getContext().getString(R.string.world_add));
        H2CO3LauncherAlertDialog installDialog = builder.create();
        installDialog.show();
        Task.supplyAsync(() -> new World(zipFile.toPath()))
                .whenComplete(Schedulers.androidUIThread(), world -> {
                    installDialog.dismiss();
                    WorldNameDialog dialog = new WorldNameDialog(getContext(), world.getWorldName(), (name, resolve, reject) -> Task.runAsync(() -> world.install(savesDir, name))
                            .whenComplete(Schedulers.androidUIThread(), () -> {
                                itemsProperty.add(new WorldListItem(getContext(), getActivity(), getParent(), new World(savesDir.resolve(name))));
                                resolve.run();
                            }, e -> {
                                if (e instanceof FileAlreadyExistsException)
                                    reject.accept(AndroidUtils.getLocalizedText(getContext(), "world_import_failed", getContext().getString(R.string.world_import_already_exists)));
                                else if (e instanceof IOException && e.getCause() instanceof InvalidPathException)
                                    reject.accept(AndroidUtils.getLocalizedText(getContext(), getContext().getString(R.string.install_new_game_malformed)));
                                else
                                    reject.accept(AndroidUtils.getLocalizedText(getContext(), e.getClass().getName() + ": " + e.getLocalizedMessage()));
                            }).start());
                    dialog.show();
                }, e -> {
                    installDialog.dismiss();
                    Logging.LOG.log(Level.WARNING, "Unable to parse world file " + zipFile, e);
                    H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                    builder1.setCancelable(false);
                    builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                    builder1.setMessage(getContext().getString(R.string.world_import_invalid));
                    builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                    builder1.create().show();
                }).start();
    }

    public boolean isShowAll() {
        return showAll.get();
    }

    public void setShowAll(boolean showAll) {
        this.showAll.set(showAll);
    }

    public BooleanProperty showAllProperty() {
        return showAll;
    }
}
