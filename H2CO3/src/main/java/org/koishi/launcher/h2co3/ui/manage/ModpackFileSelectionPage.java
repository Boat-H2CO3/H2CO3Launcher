package org.koishi.launcher.h2co3.ui.manage;

import static org.koishi.launcher.h2co3core.util.Lang.mapOf;
import static org.koishi.launcher.h2co3core.util.Pair.pair;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatDialog;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.VersionSetting;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.mod.ModAdviser;
import org.koishi.launcher.h2co3core.mod.ModpackExportInfo;
import org.koishi.launcher.h2co3core.mod.mcbbs.McbbsModpackExportTask;
import org.koishi.launcher.h2co3core.mod.multimc.MultiMCInstanceConfiguration;
import org.koishi.launcher.h2co3core.mod.multimc.MultiMCModpackExportTask;
import org.koishi.launcher.h2co3core.mod.server.ServerModpackExportTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.task.TaskListener;
import org.koishi.launcher.h2co3core.util.Lang;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherCheckBoxTreeAdapter;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherCheckBoxTreeItem;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;

import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModpackFileSelectionPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    private final Profile profile;
    private final String version;
    private final String modpackType;
    private final ModAdviser adviser;
    private final ModpackExportInfo exportInfo;
    private final File modpackFile;
    private final Map<String, String> TRANSLATION = mapOf(
            pair("minecraft/h2co3Launcherversion.cfg", getContext().getString(R.string.modpack_files_h2co3Launcherversion_cfg)),
            pair("minecraft/servers.dat", getContext().getString(R.string.modpack_files_servers_dat)),
            pair("minecraft/saves", getContext().getString(R.string.modpack_files_saves)),
            pair("minecraft/mods", getContext().getString(R.string.modpack_files_mods)),
            pair("minecraft/config", getContext().getString(R.string.modpack_files_config)),
            pair("minecraft/liteconfig", getContext().getString(R.string.modpack_files_liteconfig)),
            pair("minecraft/resourcepacks", getContext().getString(R.string.modpack_files_resourcepacks)),
            pair("minecraft/resources", getContext().getString(R.string.modpack_files_resourcepacks)),
            pair("minecraft/options.txt", getContext().getString(R.string.modpack_files_options_txt)),
            pair("minecraft/optionsshaders.txt", getContext().getString(R.string.modpack_files_optionsshaders_txt)),
            pair("minecraft/mods/VoxelMods", getContext().getString(R.string.modpack_files_mods_voxelmods)),
            pair("minecraft/dumps", getContext().getString(R.string.modpack_files_dumps)),
            pair("minecraft/blueprints", getContext().getString(R.string.modpack_files_blueprints)),
            pair("minecraft/scripts", getContext().getString(R.string.modpack_files_scripts))
    );
    private H2CO3LauncherCheckBoxTreeItem<String> rootItem;
    private H2CO3LauncherProgressBar progressBar;
    private ListView listView;
    private H2CO3LauncherButton next;

    public ModpackFileSelectionPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, Profile profile, String version, String type, ModAdviser adviser, ModpackExportInfo exportInfo, File file) {
        super(context, id, parent, resId);
        this.profile = profile;
        this.version = version;
        this.modpackType = type;
        this.adviser = adviser;
        this.exportInfo = exportInfo;
        this.modpackFile = file;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        progressBar = findViewById(R.id.progress);
        listView = findViewById(R.id.list);
        next = findViewById(R.id.next);
        next.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        next.setVisibility(View.GONE);

        new Thread(() -> {
            this.rootItem = getTreeItem(profile.getRepository().getRunDirectory(version), "minecraft");

            Schedulers.androidUIThread().execute(() -> {
                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                ObservableList<H2CO3LauncherCheckBoxTreeItem<String>> list = FXCollections.observableArrayList();
                list.add(rootItem);
                H2CO3LauncherCheckBoxTreeAdapter<String> adapter = new H2CO3LauncherCheckBoxTreeAdapter<>(getContext(), list);
                listView.setAdapter(adapter);
            });
        }).start();
    }

    private void finish() {
        ArrayList<String> list = new ArrayList<>();
        getFilesNeeded(rootItem, "minecraft", list);
        exportInfo.setWhitelist(list);

        TaskDialog taskDialog = new TaskDialog(getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
        taskDialog.setTitle(getContext().getString(R.string.message_doing));

        Task<?> task = getExportTask(modpackType, exportInfo, modpackFile);
        TaskExecutor executor = task.executor(new TaskListener() {
            @Override
            public void onStop(boolean success, TaskExecutor executor) {
                Schedulers.androidUIThread().execute(() -> {
                    if (success) {
                        H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                        builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                        builder1.setCancelable(false);
                        builder1.setMessage(getContext().getString(R.string.message_success));
                        builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> ManagePageManager.getInstance().dismissAllTempPagesCreatedByPage(ManagePageManager.PAGE_ID_MANAGE_MANAGE));
                        builder1.create().show();
                    } else {
                        if (executor.getException() == null)
                            return;
                        String appendix = StringUtils.getStackTrace(executor.getException());
                        H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                        builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                        builder1.setCancelable(false);
                        builder1.setTitle(getContext().getString(R.string.message_failed));
                        builder1.setMessage(appendix);
                        builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                        builder1.create().show();
                    }

                });
            }
        });
        taskDialog.setExecutor(executor);
        taskDialog.createDialog();
        executor.start();
    }

    private H2CO3LauncherCheckBoxTreeItem<String> getTreeItem(File file, String basePath) {
        if (!file.exists())
            return null;

        ModAdviser.ModSuggestion state = ModAdviser.ModSuggestion.SUGGESTED;
        if (basePath.length() > "minecraft/".length()) {
            state = adviser.advise(StringUtils.substringAfter(basePath, "minecraft/") + (file.isDirectory() ? "/" : ""), file.isDirectory());
            if (file.isFile() && Objects.equals(FileUtils.getNameWithoutExtension(file), version)) // Ignore <version>.json, <version>.jar
                state = ModAdviser.ModSuggestion.HIDDEN;
            if (file.isDirectory() && Objects.equals(file.getName(), version + "-natives")) // Ignore <version>-natives
                state = ModAdviser.ModSuggestion.HIDDEN;
            if (state == ModAdviser.ModSuggestion.HIDDEN)
                return null;
        }

        ObservableList<H2CO3LauncherCheckBoxTreeItem<String>> list = FXCollections.observableArrayList();
        H2CO3LauncherCheckBoxTreeItem<String> item = new H2CO3LauncherCheckBoxTreeItem<>(StringUtils.substringAfterLast(basePath, "/"), null, list);
        if (state == ModAdviser.ModSuggestion.SUGGESTED)
            item.setSelected(true);

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File it : files) {
                    H2CO3LauncherCheckBoxTreeItem<String> subItem = getTreeItem(it, basePath + "/" + it.getName());
                    if (subItem != null) {
                        item.setSelected(subItem.isSelected() || item.isSelected());
                        if (!subItem.isSelected()) {
                            item.setIndeterminate(true);
                        }
                        subItem.selectedProperty().addListener(observable -> item.checkProperty());
                        subItem.indeterminateProperty().addListener(observable -> item.checkProperty());
                        item.getSubItem().add(subItem);
                    }
                }
            }
            if (!item.isSelected()) item.setIndeterminate(false);

            // Empty folder need not to be displayed.
            if (item.getSubItem().size() == 0) {
                return null;
            }
        }

        if (TRANSLATION.containsKey(basePath)) {
            item.setComment(TRANSLATION.get(basePath));
        }
        item.setExpanded("minecraft".equals(basePath));

        return item;
    }

    private void getFilesNeeded(H2CO3LauncherCheckBoxTreeItem<String> item, String basePath, List<String> list) {
        if (item == null) return;
        if (item.isSelected() || item.isIndeterminate()) {
            if (basePath.length() > "minecraft/".length())
                list.add(StringUtils.substringAfter(basePath, "minecraft/"));
            item.getSubItem().forEach(it -> getFilesNeeded(it, basePath + "/" + it.getData(), list));
        }
    }

    private Task<?> getExportTask(String modpackType, ModpackExportInfo exportInfo, File modpackFile) {
        return new Task<Object>() {
            Task<?> exportTask;

            @Override
            public boolean doPreExecute() {
                return true;
            }

            @Override
            public void preExecute() throws Exception {
                switch (modpackType) {
                    case ModpackTypeSelectionPage.MODPACK_TYPE_MCBBS:
                        exportTask = exportAsMcbbs(exportInfo, modpackFile);
                        break;
                    case ModpackTypeSelectionPage.MODPACK_TYPE_MULTIMC:
                        exportTask = exportAsMultiMC(exportInfo, modpackFile);
                        break;
                    case ModpackTypeSelectionPage.MODPACK_TYPE_SERVER:
                        exportTask = exportAsServer(exportInfo, modpackFile);
                        break;
                    default:
                        throw new IllegalStateException("Unrecognized modpack type " + modpackType);
                }

            }

            @Override
            public Collection<Task<?>> getDependents() {
                return Collections.singleton(exportTask);
            }

            @Override
            public void execute() throws Exception {

            }
        };
    }

    private Task<?> exportAsMcbbs(ModpackExportInfo exportInfo, File modpackFile) {
        return new Task<Void>() {
            Task<?> dependency = null;

            @Override
            public void execute() {
                dependency = new McbbsModpackExportTask(profile.getRepository(), version, exportInfo, modpackFile);
            }

            @Override
            public Collection<Task<?>> getDependencies() {
                return Collections.singleton(dependency);
            }
        };
    }

    private Task<?> exportAsMultiMC(ModpackExportInfo exportInfo, File modpackFile) {
        return new Task<Void>() {
            Task<?> dependency;

            @Override
            public void execute() {
                VersionSetting vs = profile.getVersionSetting(version);
                dependency = new MultiMCModpackExportTask(profile.getRepository(), version, exportInfo.getWhitelist(),
                        new MultiMCInstanceConfiguration(
                                "OneSix",
                                exportInfo.getName() + "-" + exportInfo.getVersion(),
                                null,
                                Lang.toIntOrNull(vs.getPermSize()),
                                "",
                                "",
                                null,
                                exportInfo.getDescription(),
                                null,
                                exportInfo.getJavaArguments(),
                                false,
                                854,
                                480,
                                vs.getMaxMemory(),
                                exportInfo.getMinMemory(),
                                false,
                                /* showConsoleOnError */ true,
                                /* autoCloseConsole */ false,
                                /* overrideMemory */ true,
                                /* overrideJavaLocation */ false,
                                /* overrideJavaArgs */ true,
                                /* overrideConsole */ true,
                                /* overrideCommands */ true,
                                /* overrideWindow */ true
                        ), modpackFile);
            }

            @Override
            public Collection<Task<?>> getDependencies() {
                return Collections.singleton(dependency);
            }
        };
    }

    private Task<?> exportAsServer(ModpackExportInfo exportInfo, File modpackFile) {
        return new Task<Void>() {
            Task<?> dependency;

            @Override
            public void execute() {
                dependency = new ServerModpackExportTask(profile.getRepository(), version, exportInfo, modpackFile);
            }

            @Override
            public Collection<Task<?>> getDependencies() {
                return Collections.singleton(dependency);
            }
        };
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
        if (v == next) {
            finish();
        }
    }
}
