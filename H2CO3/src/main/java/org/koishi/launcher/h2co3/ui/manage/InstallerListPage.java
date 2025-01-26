package org.koishi.launcher.h2co3.ui.manage;

import static org.koishi.launcher.h2co3.ui.download.DownloadPageManager.getInstance;
import static org.koishi.launcher.h2co3.ui.download.InstallersPage.alertFailureMessage;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.LinearLayoutCompat;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.DownloadProviders;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.ui.InstallerItem;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.ui.download.InstallerVersionPage;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3launcher.utils.H2CO3LauncherTools;
import org.koishi.launcher.h2co3core.download.LibraryAnalyzer;
import org.koishi.launcher.h2co3core.download.RemoteVersion;
import org.koishi.launcher.h2co3core.event.Event;
import org.koishi.launcher.h2co3core.game.Version;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.task.TaskListener;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.browser.options.SelectionMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherCommonPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class InstallerListPage extends H2CO3LauncherCommonPage implements ManageUI.VersionLoadable, View.OnClickListener {

    private Profile profile;
    private String versionId;
    private Version version;
    private String gameVersion;

    private ScrollView scrollView;
    private LinearLayoutCompat parent;

    private H2CO3LauncherButton installOfflineButton;

    public InstallerListPage(Context context, int id, H2CO3LauncherUILayout parent, int resId) {
        super(context, id, parent, resId);
        create();
    }

    private void create() {
        scrollView = findViewById(R.id.scroll);
        installOfflineButton = findViewById(R.id.install_offline);
        installOfflineButton.setOnClickListener(this);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void loadVersion(Profile profile, String versionId) {
        this.profile = profile;
        this.versionId = versionId;
        this.version = profile.getRepository().getVersion(versionId);
        this.gameVersion = null;

        CompletableFuture.supplyAsync(() -> {
            gameVersion = profile.getRepository().getGameVersion(version).orElse(null);

            return LibraryAnalyzer.analyze(profile.getRepository().getResolvedPreservingPatchesVersion(versionId));
        }).thenAcceptAsync(analyzer -> {
            Function<String, Runnable> removeAction = libraryId -> () -> profile.getDependency().removeLibraryAsync(version, libraryId)
                    .thenComposeAsync(profile.getRepository()::saveAsync)
                    .withComposeAsync(profile.getRepository().refreshVersionsAsync())
                    .withRunAsync(Schedulers.androidUIThread(), () -> {
                        loadVersion(this.profile, this.versionId);
                        profile.getRepository().onVersionIconChanged.fireEvent(new Event(this));
                    })
                    .start();

            clear();

            InstallerItem.InstallerItemGroup group = new InstallerItem.InstallerItemGroup(getContext(), gameVersion);

            // Conventional libraries: game, fabric, quilt, forge, neoforge, liteloader, optifine
            for (InstallerItem installerItem : group.getLibraries()) {
                String libraryId = installerItem.getLibraryId();

                // Skip fabric-api and quilt-api
                if (libraryId.contains("fabric-api") || libraryId.contains("quilt-api")) {
                    continue;
                }

                String libraryVersion = analyzer.getVersion(libraryId).orElse(null);
                installerItem.libraryVersion.set(libraryVersion);
                installerItem.upgradable.set(libraryVersion != null);
                installerItem.installable.set(true);
                installerItem.action.set(() -> {
                    InstallerVersionPage page = new InstallerVersionPage(getContext(), PageManager.PAGE_ID_TEMP, getParent(), R.layout.page_install_version, gameVersion, libraryId, remoteVersion -> {
                        if (libraryVersion == null) {
                            finish(profile, remoteVersion);
                        } else {
                            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                            builder.setTitle(getContext().getString(R.string.install_change_version));
                            builder.setMessage(AndroidUtils.getLocalizedText(getContext(), "install_change_version_confirm", AndroidUtils.getLocalizedText(getContext(), "install_installer_" + libraryId), libraryVersion, remoteVersion.getSelfVersion()));
                            builder.setPositiveButton(() -> finish(profile, remoteVersion));
                            builder.setNegativeButton(null);
                            builder.create().show();
                        }
                    });
                    ManagePageManager.getInstance().showTempPage(page);
                });
                boolean removable = !"game".equals(libraryId) && libraryVersion != null;
                installerItem.removable.set(removable);
                if (removable) {
                    Runnable action = removeAction.apply(libraryId);
                    installerItem.removeAction.set(action);
                }
                addView(installerItem);
            }
        }, Schedulers.androidUIThread());
    }

    public void installOffline() {
        ArrayList<String> suffix = new ArrayList<>();
        suffix.add(".jar");
        FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.install_installer_install_offline_extension));
        builder.setLibMode(LibMode.FILE_CHOOSER);
        builder.setSelectionMode(SelectionMode.SINGLE_SELECTION);
        builder.setSuffix(suffix);
        builder.create().browse(getActivity(), RequestCodes.SELECT_AUTO_INSTALLER_CODE, (requestCode, resultCode, data) -> {
            if (requestCode == RequestCodes.SELECT_AUTO_INSTALLER_CODE && resultCode == Activity.RESULT_OK && data != null) {
                String path = FileBrowser.getSelectedFiles(data).get(0);
                Uri uri = Uri.parse(path);
                if (AndroidUtils.isDocUri(uri)) {
                    path = AndroidUtils.copyFileToDir(getActivity(), uri, new File(H2CO3LauncherTools.CACHE_DIR));
                }
                if (new File(path).exists()) {
                    doInstallOffline(new File(path));
                }
            }
        });
    }

    private void doInstallOffline(File file) {
        Task<?> task = profile.getDependency().installLibraryAsync(version, file.toPath())
                .thenComposeAsync(profile.getRepository()::saveAsync)
                .thenComposeAsync(profile.getRepository().refreshVersionsAsync());
        task.setName(getContext().getString(R.string.install_installer_install_offline));
        TaskExecutor executor = task.executor(new TaskListener() {
            @Override
            public void onStop(boolean success, TaskExecutor executor) {
                Schedulers.androidUIThread().execute(() -> {
                    if (success) {
                        loadVersion(profile, versionId);
                        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                        builder.setCancelable(false);
                        builder.setMessage(getContext().getString(R.string.install_success));
                        builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> profile.getRepository().onVersionIconChanged.fireEvent(new Event(this)));
                        builder.create().show();
                    } else {
                        if (executor.getException() == null)
                            return;
                        alertFailureMessage(getContext(), executor.getException(), () -> {});
                    }
                    loadVersion(InstallerListPage.this.profile, InstallerListPage.this.versionId);
                });
            }
        });
        TaskDialog dialog = new TaskDialog(getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
        dialog.setTitle(getContext().getString(R.string.install_installer_install_offline));
        dialog.setExecutor(executor);
        dialog.show();
        executor.start();
    }

    private void clear() {
        if (parent == null) {
            parent = new LinearLayoutCompat(getContext());
            parent.setOrientation(LinearLayoutCompat.VERTICAL);
            scrollView.addView(parent);
            ViewGroup.LayoutParams layoutParams = scrollView.getChildAt(0).getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            scrollView.getChildAt(0).setLayoutParams(layoutParams);
        }
        parent.removeAllViews();
    }

    private void addView(InstallerItem installerItem) {
        if (parent == null) {
            parent = new LinearLayoutCompat(getContext());
            parent.setOrientation(LinearLayoutCompat.VERTICAL);
            scrollView.addView(parent);
            ViewGroup.LayoutParams layoutParams = scrollView.getChildAt(0).getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            scrollView.getChildAt(0).setLayoutParams(layoutParams);
        }
        View view = installerItem.createView();
        if (parent.getChildCount() > 0) {
            view.setPadding(0, ConvertUtils.dip2px(getContext(), 10), 0, 0);
        }
        parent.addView(view);
    }

    private void finish(Profile profile, RemoteVersion remoteVersion) {
        // We remove library but not save it,
        // so if installation failed will not break down current version.
        Task<Version> ret = Task.supplyAsync(() -> version);
        List<String> stages = new ArrayList<>();
        ret = ret.thenComposeAsync(version -> profile.getDependency(DownloadProviders.getDownloadProvider()).installLibraryAsync(version, remoteVersion));
        stages.add(String.format("h2co3Launcher.install.%s:%s", remoteVersion.getLibraryId(), remoteVersion.getSelfVersion()));

        Task<?> task = ret.thenComposeAsync(profile.getRepository()::saveAsync).thenComposeAsync(profile.getRepository().refreshVersionsAsync()).withStagesHint(stages);

        TaskDialog taskListPane = new TaskDialog(getInstance().getContext(), new TaskCancellationAction(TaskDialog::dismissDialog));
        taskListPane.setTitle(getContext().getString(R.string.install_change_version));
        AlertDialog alertDialog = taskListPane.create();

        Schedulers.androidUIThread().execute(() -> {
            TaskExecutor executor = task.executor(new TaskListener() {
                @Override
                public void onStop(boolean success, TaskExecutor executor) {
                    Schedulers.androidUIThread().execute(() -> {
                        if (success) {
                            H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(getContext());
                            builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                            builder1.setCancelable(false);
                            builder1.setMessage(getContext().getString(R.string.install_success));
                            builder1.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> {
                                ManagePageManager.getInstance().dismissCurrentTempPage();
                                profile.getRepository().onVersionIconChanged.fireEvent(new Event(this));
                            });
                            builder1.create().show();
                        } else {
                            if (executor.getException() == null)
                                return;
                            alertFailureMessage(getContext(), executor.getException(), () -> {});
                        }
                        loadVersion(InstallerListPage.this.profile, InstallerListPage.this.versionId);
                    });
                }
            });
            taskListPane.setExecutor(executor);
            alertDialog.show();
            executor.start();
        });
    }

    @Override
    public void onClick(View view) {
        if (view == installOfflineButton) {
            installOffline();
        }
    }
}
