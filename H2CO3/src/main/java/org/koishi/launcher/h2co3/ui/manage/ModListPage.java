package org.koishi.launcher.h2co3.ui.manage;

import static org.koishi.launcher.h2co3core.util.Logging.LOG;
import static org.koishi.launcher.h2co3core.util.StringUtils.isNotBlank;
import static org.koishi.launcher.h2co3library.browser.FileBrowser.SELECTED_FILES;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.ui.download.DownloadPageManager;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.ModTranslations;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleListProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.mod.LocalModFile;
import org.koishi.launcher.h2co3core.mod.ModManager;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModListPage extends H2CO3LauncherCommonPage implements ManageUI.VersionLoadable, View.OnClickListener {

    private final BooleanProperty modded = new SimpleBooleanProperty(this, "modded", false);
    private final ListProperty<ModInfoObject> itemsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final LocalModListAdapter adapter;
    private ModManager modManager;
    private LibraryAnalyzer libraryAnalyzer;
    private Profile profile;
    private String versionId;
    private boolean isSearching = false;
    private H2CO3LauncherTextView warningText;
    private LinearLayoutCompat left;
    private RelativeLayout right;
    private H2CO3LauncherEditText searchBar;
    private H2CO3LauncherButton searchButton;
    private H2CO3LauncherLinearLayout normalGroup;
    private H2CO3LauncherLinearLayout selectedGroup;
    private H2CO3LauncherButton addButton;
    private H2CO3LauncherButton checkUpdateAllButton;
    private H2CO3LauncherButton checkUpdateButton;
    private H2CO3LauncherButton refreshButton;
    private H2CO3LauncherButton deleteButton;
    private H2CO3LauncherButton selectAllButton;
    private H2CO3LauncherButton cancelButton;
    private H2CO3LauncherProgressBar progressBar;
    private ListView listView;

    public ModListPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);
        adapter = new LocalModListAdapter(getContext(), this);
        listView.setAdapter(adapter);
        Bindings.bindContent(adapter.listProperty(), itemsProperty);

        adapter.selectedItemsProperty().addListener((InvalidationListener) observable -> switchLayout(adapter.selectedItemsProperty().getSize() > 0));

        moddedProperty().addListener(observable -> setEnable(isModded()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        warningText = findViewById(R.id.warning);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        searchBar = findViewById(R.id.search_filter);
        searchButton = findViewById(R.id.search);
        normalGroup = findViewById(R.id.normal_layout);
        selectedGroup = findViewById(R.id.selected_layout);
        addButton = findViewById(R.id.add);
        checkUpdateAllButton = findViewById(R.id.check_update_all);
        checkUpdateButton = findViewById(R.id.check_update);
        refreshButton = findViewById(R.id.refresh);
        deleteButton = findViewById(R.id.delete);
        selectAllButton = findViewById(R.id.select_all);
        cancelButton = findViewById(R.id.cancel);
        progressBar = findViewById(R.id.progress);
        listView = findViewById(R.id.list);

        searchButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        checkUpdateAllButton.setOnClickListener(this);
        checkUpdateButton.setOnClickListener(this);
        refreshButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        selectAllButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            search();
        }
        if (v == addButton) {
            add();
        }
        if (v == checkUpdateAllButton) {
            checkUpdateAllButton.setFocusable(false);
            checkUpdates(false);
        }
        if (v == checkUpdateButton) {
            checkUpdateButton.setFocusable(false);
            checkUpdates(true);
        }
        if (v == refreshButton) {
            refresh();
        }
        if (v == deleteButton) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setMessage(getContext().getString(R.string.button_remove_confirm));
            builder.setPositiveButton(getContext().getString(R.string.button_remove), () -> removeSelected(adapter.selectedItemsProperty()));
            builder.setNegativeButton(null);
            builder.create().show();
        }
        if (v == selectAllButton) {
            adapter.selectAll();
        }
        if (v == cancelButton) {
            adapter.selectedItemsProperty().clear();
        }
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void loadVersion(Profile profile, String version) {
        this.profile = profile;
        this.versionId = version;

        switchLayout(false);
        adapter.selectedItemsProperty().clear();
        cancelSearch();

        libraryAnalyzer = LibraryAnalyzer.analyze(profile.getRepository().getResolvedPreservingPatchesVersion(version));
        setModded(libraryAnalyzer.hasModLoader());
        loadMods(profile.getRepository().getModManager(version));
    }

    private void setEnable(boolean enable) {
        if (enable) {
            left.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            warningText.setVisibility(View.GONE);
        } else {
            left.setVisibility(View.GONE);
            right.setVisibility(View.GONE);
            warningText.setVisibility(View.VISIBLE);
        }
    }

    private void setLoading(boolean loading) {
        Schedulers.androidUIThread().execute(() -> {
            if (loading) {
                cancelSearch();
                searchBar.setEnabled(false);
                searchButton.setEnabled(false);
                addButton.setEnabled(false);
                checkUpdateAllButton.setEnabled(false);
                checkUpdateButton.setEnabled(false);
                refreshButton.setEnabled(false);
                deleteButton.setEnabled(false);
                selectAllButton.setEnabled(false);
                cancelButton.setEnabled(false);
                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                searchBar.setEnabled(true);
                searchButton.setEnabled(true);
                addButton.setEnabled(true);
                checkUpdateAllButton.setEnabled(true);
                checkUpdateButton.setEnabled(true);
                refreshButton.setEnabled(true);
                deleteButton.setEnabled(true);
                selectAllButton.setEnabled(true);
                cancelButton.setEnabled(true);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                cancelSearch();
            }
        });
    }

    private void switchLayout(boolean select) {
        if (select) {
            normalGroup.setVisibility(View.GONE);
            selectedGroup.setVisibility(View.VISIBLE);
        } else {
            normalGroup.setVisibility(View.VISIBLE);
            selectedGroup.setVisibility(View.GONE);
        }
    }

    public void refresh() {
        loadMods(modManager);
    }

    private CompletableFuture<?> loadMods(ModManager modManager) {
        this.modManager = modManager;
        return CompletableFuture.supplyAsync(() -> {
            try {
                synchronized (ModListPage.this) {
                    setLoading(true);
                    modManager.refreshMods();
                    return new ArrayList<>(modManager.getMods());
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, Schedulers.defaultScheduler()).whenCompleteAsync((list, exception) -> {
            setLoading(false);
            if (exception == null)
                itemsProperty.setAll(list.stream().map(it -> new ModInfoObject(getContext(), it)).sorted().collect(Collectors.toList()));
            else
                LOG.log(Level.SEVERE, "Failed to load local mod list", exception);

            System.gc();
        }, Schedulers.androidUIThread());
    }

    public void add() {
        ArrayList<String> suffix = new ArrayList<>();
        suffix.add(".jar");
        suffix.add(".zip");
        suffix.add(".litemod");
        FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
        builder.setLibMode(LibMode.FILE_CHOOSER);
        builder.setTitle(getContext().getString(R.string.mods_choose_mod));
        builder.setSuffix(suffix);
        builder.setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
        builder.create().browse(getActivity(), RequestCodes.SELECT_MODS_CODE, (requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_MODS_CODE && resultCode == Activity.RESULT_OK && data != null) {
                ArrayList<Uri> selectedFiles = data.getParcelableArrayListExtra(SELECTED_FILES);
                List<Object> res = selectedFiles.stream().filter(Objects::nonNull).map(uri -> {
                    if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT) || Objects.equals(uri.getScheme(), ContentResolver.SCHEME_FILE)) {
                        return uri;
                    } else {
                        return new File(uri.toString());
                    }
                }).collect(Collectors.toList());
                // It's guaranteed that succeeded and failed are thread safe here.
                List<String> succeeded = new ArrayList<>(res.size());
                List<String> failed = new ArrayList<>();

                Task.runAsync(() -> {
                    for (Object obj : res) {
                        if (obj instanceof File) {
                            File file = (File) obj;
                            try {
                                modManager.addMod(file.toPath());
                                succeeded.add(file.getName());
                            } catch (Exception e) {
                                LOG.log(Level.WARNING, "Unable to add mod " + file, e);
                                failed.add(file.getName());

                                // Actually addMod will not throw exceptions because FileChooser has already filtered files.
                            }
                        } else {
                            try {
                                Uri uri = (Uri) obj;
                                modManager.addMod(getActivity(), uri);
                                succeeded.add(new File(uri.getPath()).getName());
                            } catch (Exception e) {
                                LOG.log(Level.WARNING, "Unable to add mod " + obj.toString(), e);
                                failed.add(obj.toString());

                                // Actually addMod will not throw exceptions because FileChooser has already filtered files.
                            }
                        }
                    }
                }).withRunAsync(Schedulers.androidUIThread(), () -> {
                    List<String> prompt = new ArrayList<>(1);
                    if (!succeeded.isEmpty())
                        prompt.add(AndroidUtils.getLocalizedText(getContext(), "mods_add_success", String.join(", ", succeeded)));
                    if (!failed.isEmpty())
                        prompt.add(AndroidUtils.getLocalizedText(getContext(), "mods_add_failed", String.join(", ", failed)));
                    H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                    builder1.setCancelable(false);
                    builder1.setAlertLevel(failed.isEmpty() ? H2CO3LauncherAlertDialog.AlertLevel.INFO : H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                    builder1.setTitle(getContext().getString(R.string.mods_add));
                    builder1.setMessage(String.join("\n", prompt));
                    builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                    builder1.create().show();
                    loadMods(modManager);
                }).start();
            }
        });
    }

    public void removeSelected(ObservableList<ModInfoObject> selectedItems) {
        try {
            modManager.removeMods(selectedItems.stream()
                    .filter(Objects::nonNull)
                    .map(ModInfoObject::getModInfo)
                    .toArray(LocalModFile[]::new));
            loadMods(modManager);
        } catch (IOException ignore) {
            // Fail to remove mods if the game is running or the mod is absent.
        }
    }

    public void checkUpdates(boolean isSelected) {
        Runnable action = () -> {
            TaskDialog dialog = new TaskDialog(getContext(), TaskCancellationAction.NORMAL);
            dialog.setTitle(getContext().getString(R.string.update_checking));

            Task<?> task = Task
                    .composeAsync(() -> {
                        Optional<String> gameVersion = profile.getRepository().getGameVersion(versionId);
                        if (gameVersion.isPresent()) {
                            if (isSelected) {
                                return new ModCheckUpdatesTask(gameVersion.get(), adapter.selectedItemsProperty().stream()
                                        .filter(Objects::nonNull)
                                        .map(ModInfoObject::getModInfo)
                                        .collect(Collectors.toList()));
                            } else {
                                return new ModCheckUpdatesTask(gameVersion.get(), modManager.getMods());
                            }
                        }
                        return null;
                    })
                    .whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
                        checkUpdateAllButton.setFocusable(true);
                        checkUpdateButton.setFocusable(true);
                        if (exception != null || result == null) {
                            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                            builder.setTitle(getContext().getString(R.string.message_failed));
                            builder.setMessage("Failed to check updates");
                            builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder.create().show();
                        } else if (result.isEmpty()) {
                            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                            builder.setMessage(getContext().getString(R.string.mods_check_updates_empty));
                            builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                            builder.create().show();
                        } else {
                            ModUpdatesPage page = new ModUpdatesPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_mod_update, this, modManager, result);
                            ManagePageManager.getInstance().showTempPage(page);
                        }
                    })
                    .withStagesHint(Collections.singletonList("mods.check_updates"));
            TaskExecutor executor = task.executor();
            dialog.setExecutor(executor);
            dialog.show();
            executor.start();
        };

        if (profile.getRepository().isModpack(versionId)) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setMessage(getContext().getString(R.string.mods_update_modpack_mod_warning));
            builder.setPositiveButton(action::run);
            builder.setNegativeButton(null);
            builder.create().show();
        } else {
            action.run();
        }
    }

    public void download() {
        H2CO3MainActivity.getInstance().setNavigationItemChecked(R.id.navigation_download);
        H2CO3MainActivity.getInstance().toolbar.setTitle(R.string.app_name);
        DownloadPageManager.getInstance().switchPage(DownloadPageManager.PAGE_ID_DOWNLOAD_MOD);
    }

    public void rollback(LocalModFile from, LocalModFile to) {
        try {
            modManager.rollback(from, to);
            refresh();
        } catch (IOException ex) {
            Toast.makeText(getContext(), getContext().getString(R.string.message_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelSearch() {
        if (isSearching) {
            isSearching = false;
            searchBar.setText("");
            Bindings.bindContent(adapter.listProperty(), itemsProperty);
        }
    }

    private void search() {
        isSearching = true;
        adapter.selectedItemsProperty().clear();

        Bindings.unbindContent(adapter.listProperty(), itemsProperty);

        String queryString = searchBar.getText().toString();
        if (StringUtils.isBlank(queryString)) {
            adapter.listProperty().setAll(itemsProperty.get());
        } else {
            adapter.listProperty().clear();

            Predicate<String> predicate;
            if (queryString.startsWith("regex:")) {
                try {
                    Pattern pattern = Pattern.compile(queryString.substring("regex:".length()));
                    predicate = s -> pattern.matcher(s).find();
                } catch (Throwable e) {
                    LOG.log(Level.WARNING, "Illegal regular expression", e);
                    return;
                }
            } else {
                String lowerQueryString = queryString.toLowerCase(Locale.ROOT);
                predicate = s -> s.toLowerCase(Locale.ROOT).contains(lowerQueryString);
            }

            // Do we need to search in the background thread?
            for (ModInfoObject item : itemsProperty.get()) {
                if (predicate.test(item.getModInfo().getFileName())) {
                    adapter.listProperty().add(item);
                }
            }
        }
    }

    public boolean isModded() {
        return modded.get();
    }

    public void setModded(boolean modded) {
        this.modded.set(modded);
    }

    public BooleanProperty moddedProperty() {
        return modded;
    }

    static class ModInfoObject implements Comparable<ModInfoObject> {
        private final BooleanProperty active;
        private final LocalModFile localModFile;
        private final String title;
        private final String message;
        private final ModTranslations.Mod mod;
        private RemoteMod remoteMod;

        ModInfoObject(Context context, LocalModFile localModFile) {
            this.localModFile = localModFile;
            this.active = localModFile.activeProperty();

            StringBuilder title = new StringBuilder(localModFile.getName());
            if (isNotBlank(localModFile.getVersion()))
                title.append(" ").append(localModFile.getVersion());
            this.title = title.toString();

            StringBuilder message = new StringBuilder(localModFile.getFileName());
            if (isNotBlank(localModFile.getGameVersion()))
                message.append(", ").append(context.getString(R.string.archive_game_version)).append(": ").append(localModFile.getGameVersion());
            if (isNotBlank(localModFile.getAuthors()))
                message.append(", ").append(context.getString(R.string.archive_author)).append(": ").append(localModFile.getAuthors());
            this.message = message.toString();

            this.mod = ModTranslations.MOD.getModById(localModFile.getId());
        }

        public BooleanProperty getActive() {
            return active;
        }

        String getTitle() {
            return title;
        }

        String getSubtitle() {
            return message;
        }

        LocalModFile getModInfo() {
            return localModFile;
        }

        public ModTranslations.Mod getMod() {
            return mod;
        }

        @Override
        public int compareTo(@NotNull ModInfoObject o) {
            return localModFile.getFileName().toLowerCase().compareTo(o.localModFile.getFileName().toLowerCase());
        }

        public RemoteMod getRemoteMod() {
            return remoteMod;
        }

        public void setRemoteMod(RemoteMod remoteMod) {
            this.remoteMod = remoteMod;
        }
    }
}
