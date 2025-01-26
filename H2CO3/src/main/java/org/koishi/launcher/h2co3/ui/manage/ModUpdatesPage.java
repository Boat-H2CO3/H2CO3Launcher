package org.koishi.launcher.h2co3.ui.manage;

import static org.koishi.launcher.h2co3core.util.Pair.pair;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.mod.LocalModFile;
import org.koishi.launcher.h2co3core.mod.ModManager;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.task.FileDownloadTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.util.Pair;
import org.koishi.launcher.h2co3core.util.io.CSVTable;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;

import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ModUpdatesPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    private final ModListPage modListPage;
    private final ModManager modManager;
    private final ObservableList<ModUpdateObject> objects;

    private ListView listView;
    private H2CO3LauncherButton export;
    private H2CO3LauncherButton update;
    private H2CO3LauncherButton cancel;

    public ModUpdatesPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, ModListPage modListPage, ModManager modManager, List<LocalModFile.ModUpdate> list) {
        super(context, id, parent, resId);
        this.modListPage = modListPage;
        this.modManager = modManager;
        this.objects = FXCollections.observableList(list.stream().map(it -> new ModUpdateObject(getContext(), it)).collect(Collectors.toList()));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        listView = findViewById(R.id.list);
        export = findViewById(R.id.export);
        update = findViewById(R.id.update);
        cancel = findViewById(R.id.cancel);
        export.setOnClickListener(this);
        update.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        listView.setAdapter(new ModUpdateListAdapter(getContext(), objects));
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onClick(View v) {
        if (v == export) {
            exportList();
        }
        if (v == update) {
            updateMods();
        }
        if (v == cancel) {
            ManagePageManager.getInstance().dismissCurrentTempPage();
        }
    }

    private void updateMods() {
        ModUpdateTask task = new ModUpdateTask(
                modManager,
                objects.stream()
                        .filter(o -> o.enabled.get())
                        .map(object -> pair(object.data.getLocalMod(), object.data.getCandidates().get(0)))
                        .collect(Collectors.toList()));
        TaskDialog taskDialog = new TaskDialog(getContext(), TaskCancellationAction.NORMAL);
        taskDialog.setTitle(getContext().getString(R.string.mods_check_updates_update));
        TaskExecutor executor = task.whenComplete(Schedulers.androidUIThread(), exception -> {
            ManagePageManager.getInstance().dismissCurrentTempPage();
            modListPage.refresh();
            if (!task.getFailedMods().isEmpty()) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                builder.setCancelable(false);
                builder.setTitle(getContext().getString(R.string.install_failed));
                builder.setMessage(getContext().getString(R.string.mods_check_updates_failed) + "\n" + task.getFailedMods().stream().map(LocalModFile::getFileName).collect(Collectors.joining("\n")));
                builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                builder.create().show();
            }

            if (exception == null) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                builder.setCancelable(false);
                builder.setMessage(getContext().getString(R.string.install_success));
                builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                builder.create().show();
            }
        }).executor();
        taskDialog.setExecutor(executor);
        taskDialog.show();
        executor.start();
    }

    private void exportList() {
        Path path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/H2CO3Launcher", "h2co3Launcher-mod-update-list-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss")) + ".csv").toPath();

        TaskDialog taskDialog = new TaskDialog(getContext(), TaskCancellationAction.NORMAL);
        taskDialog.setTitle(getContext().getString(R.string.button_export));
        TaskExecutor executor = Task.runAsync(() -> {
            CSVTable csvTable = CSVTable.createEmpty();

            csvTable.set(0, 0, "Source File Name");
            csvTable.set(1, 0, "Current Version");
            csvTable.set(2, 0, "Target Version");
            csvTable.set(3, 0, "Update Source");

            for (int i = 0; i < objects.size(); i++) {
                csvTable.set(0, i + 1, objects.get(i).fileName.get());
                csvTable.set(1, i + 1, objects.get(i).currentVersion.get());
                csvTable.set(2, i + 1, objects.get(i).targetVersion.get());
                csvTable.set(3, i + 1, objects.get(i).source.get());
            }

            csvTable.write(Files.newOutputStream(path));
        }).whenComplete(Schedulers.androidUIThread(), exception -> {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            if (exception == null) {
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                builder.setCancelable(false);
                builder.setTitle(getContext().getString(R.string.message_success));
                builder.setMessage(path.toString());
            } else {
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                builder.setCancelable(false);
                builder.setTitle(getContext().getString(R.string.message_error));
                builder.setMessage(exception.getMessage());
            }
            builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
            builder.create().show();
        }).executor();
        taskDialog.setExecutor(executor);
        taskDialog.show();
        executor.start();
    }

    public static final class ModUpdateObject {
        final LocalModFile.ModUpdate data;
        final BooleanProperty enabled = new SimpleBooleanProperty();
        final StringProperty fileName = new SimpleStringProperty();
        final StringProperty currentVersion = new SimpleStringProperty();
        final StringProperty targetVersion = new SimpleStringProperty();
        final StringProperty source = new SimpleStringProperty();

        public ModUpdateObject(Context context, LocalModFile.ModUpdate data) {
            this.data = data;

            enabled.set(!data.getLocalMod().getModManager().isDisabled(data.getLocalMod().getFile()));
            fileName.set(data.getLocalMod().getFileName());
            currentVersion.set(data.getCurrentVersion().getVersion());
            targetVersion.set(data.getCandidates().get(0).getVersion());
            switch (data.getCurrentVersion().getSelf().getType()) {
                case CURSEFORGE:
                    source.set(context.getString(org.koishi.launcher.h2co3.R.string.mods_curseforge));
                    break;
                case MODRINTH:
                    source.set(context.getString(org.koishi.launcher.h2co3.R.string.mods_modrinth));
            }
        }

        public boolean isEnabled() {
            return enabled.get();
        }

        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        public BooleanProperty enabledProperty() {
            return enabled;
        }

        public String getFileName() {
            return fileName.get();
        }

        public void setFileName(String fileName) {
            this.fileName.set(fileName);
        }

        public StringProperty fileNameProperty() {
            return fileName;
        }

        public String getCurrentVersion() {
            return currentVersion.get();
        }

        public void setCurrentVersion(String currentVersion) {
            this.currentVersion.set(currentVersion);
        }

        public StringProperty currentVersionProperty() {
            return currentVersion;
        }

        public String getTargetVersion() {
            return targetVersion.get();
        }

        public void setTargetVersion(String targetVersion) {
            this.targetVersion.set(targetVersion);
        }

        public StringProperty targetVersionProperty() {
            return targetVersion;
        }

        public String getSource() {
            return source.get();
        }

        public void setSource(String source) {
            this.source.set(source);
        }

        public StringProperty sourceProperty() {
            return source;
        }
    }

    public static class ModUpdateTask extends Task<Void> {
        private final Collection<Task<?>> dependents;
        private final List<LocalModFile> failedMods = new ArrayList<>();

        ModUpdateTask(ModManager modManager, List<Pair<LocalModFile, RemoteMod.Version>> mods) {
            setStage("mods.check_updates.update");
            getProperties().put("total", mods.size());

            this.dependents = new ArrayList<>();
            for (Pair<LocalModFile, RemoteMod.Version> mod : mods) {
                LocalModFile local = mod.getKey();
                RemoteMod.Version remote = mod.getValue();
                boolean isDisabled = local.getModManager().isDisabled(local.getFile());

                dependents.add(Task
                        .runAsync(Schedulers.androidUIThread(), () -> local.setOld(true))
                        .thenComposeAsync(() -> {
                            String fileName = remote.getFile().getFilename();
                            if (isDisabled)
                                fileName += ModManager.DISABLED_EXTENSION;

                            FileDownloadTask task = new FileDownloadTask(
                                    new URL(remote.getFile().getUrl()),
                                    modManager.getModsDirectory().resolve(fileName).toFile());

                            task.setName(remote.getName());
                            return task;
                        })
                        .whenComplete(Schedulers.androidUIThread(), exception -> {
                            if (exception != null) {
                                // restore state if failed
                                local.setOld(false);
                                if (isDisabled)
                                    local.disable();
                                failedMods.add(local);
                            }
                        })
                        .withCounter("mods.check_updates.update"));
            }
        }

        public List<LocalModFile> getFailedMods() {
            return failedMods;
        }

        @Override
        public Collection<Task<?>> getDependents() {
            return dependents;
        }

        @Override
        public boolean doPreExecute() {
            return true;
        }

        @Override
        public void preExecute() {
            notifyPropertiesChanged();
        }

        @Override
        public boolean isRelyingOnDependents() {
            return false;
        }

        @Override
        public void execute() throws Exception {
            if (!isDependentsSucceeded())
                throw getException();
        }
    }
}
