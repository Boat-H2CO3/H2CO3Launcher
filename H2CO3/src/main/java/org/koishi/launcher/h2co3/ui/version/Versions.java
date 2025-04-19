package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;
import android.widget.Toast;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.LauncherHelper;
import org.koishi.launcher.h2co3.setting.Accounts;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.ui.PageManager;
import org.koishi.launcher.h2co3.ui.ProgressDialog;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.ui.account.CreateAccountDialog;
import org.koishi.launcher.h2co3.ui.download.DownloadPageManager;
import org.koishi.launcher.h2co3.ui.download.LocalModpackPage;
import org.koishi.launcher.h2co3.ui.download.ModpackSelectionPage;
import org.koishi.launcher.h2co3.ui.manage.ManagePageManager;
import org.koishi.launcher.h2co3.ui.manage.ModpackTypeSelectionPage;
import org.koishi.launcher.h2co3.util.AndroidUtils;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.auth.Account;
import org.koishi.launcher.h2co3core.auth.AccountFactory;
import org.koishi.launcher.h2co3core.download.game.GameAssetDownloadTask;
import org.koishi.launcher.h2co3core.mod.RemoteMod;
import org.koishi.launcher.h2co3core.task.FileDownloadTask;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.util.Logging;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.platform.OperatingSystem;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Versions {

    public static void importModpack(Context context, H2CO3LauncherUILayout parent) {
        Profile profile = Profiles.getSelectedProfile();
        if (profile != null && profile.getRepository().isLoaded()) {
            ModpackSelectionPage page = new ModpackSelectionPage(context, PageManager.PAGE_ID_TEMP, parent, R.layout.page_modpack_selection, profile, null);
            DownloadPageManager.getInstance().showTempPage(page);
        }
    }

    public static void downloadModpackImpl(Context context, H2CO3LauncherUILayout parent, Profile profile, RemoteMod.Version file) {
        if (profile == null || file == null) return;

        Path modpack;
        URL downloadURL;
        try {
            modpack = Files.createTempFile("modpack", ".zip");
            downloadURL = new URL(file.getFile().getUrl());
        } catch (IOException e) {
            showAlert(context, context.getString(R.string.download_failed),
                    AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", file.getFile().getUrl()) + "\n" + StringUtils.getStackTrace(e));
            return;
        }

        TaskDialog taskDialog = new TaskDialog(context, new TaskCancellationAction(TaskDialog::dismissDialog));
        taskDialog.setTitle(context.getString(R.string.message_downloading));
        TaskExecutor executor = new FileDownloadTask(downloadURL, modpack.toFile())
                .whenComplete(Schedulers.androidUIThread(), e -> {
                    if (e == null) {
                        LocalModpackPage page = new LocalModpackPage(context, PageManager.PAGE_ID_TEMP, parent, R.layout.page_modpack, profile, null, modpack.toFile());
                        DownloadPageManager.getInstance().showTempPage(page);
                    } else if (e instanceof CancellationException) {
                        Toast.makeText(context, context.getString(R.string.message_cancelled), Toast.LENGTH_SHORT).show();
                    } else {
                        showAlert(context, context.getString(R.string.download_failed),
                                AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", file.getFile().getUrl()) + "\n" + StringUtils.getStackTrace(e));
                    }
                }).executor();
        taskDialog.setExecutor(executor);
        taskDialog.show();
        executor.start();
    }

    public static void deleteVersion(Context context, Profile profile, String version) {
        if (profile == null || version == null) return;

        boolean isIndependent = profile.getVersionSetting(version).isIsolateGameDir();
        String message = isIndependent ? String.format(context.getString(R.string.version_manage_remove_confirm_independent), version) : String.format(context.getString(R.string.version_manage_remove_confirm), version);

        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
        builder.setMessage(message);
        builder.setPositiveButton(() -> {
            ProgressDialog progress = new ProgressDialog(context);
            Task.runAsync(() -> profile.getRepository().removeVersionFromDisk(version))
                    .whenComplete(Schedulers.androidUIThread(), (e) -> progress.dismiss())
                    .start();
        });
        builder.setNegativeButton(null);
        builder.create().show();
    }

    public static CompletableFuture<String> renameVersion(Context context, Profile profile, String version) {
        if (profile == null || version == null) return CompletableFuture.completedFuture(null);

        RenameVersionDialog dialog = new RenameVersionDialog(context, version, (newName, resolve, reject) -> {
            if (!OperatingSystem.isNameValid(newName)) {
                reject.accept(context.getString(R.string.install_new_game_malformed));
                return;
            }
            ProgressDialog progress = new ProgressDialog(context);
            Task.supplyAsync(() -> profile.getRepository().renameVersion(version, newName))
                    .thenComposeAsync(Schedulers.androidUIThread(), result -> {
                        progress.dismiss();
                        if (result) {
                            resolve.run();
                            profile.getRepository().refreshVersionsAsync()
                                    .thenRunAsync(Schedulers.androidUIThread(), () -> {
                                        if (profile.getRepository().hasVersion(newName)) {
                                            profile.setSelectedVersion(newName);
                                        }
                                    }).start();
                        } else {
                            reject.accept(context.getString(R.string.version_manage_rename_fail));
                        }
                        return null;
                    }).start();
        });
        dialog.show();
        return dialog.getFuture();
    }

    public static void exportVersion(Context context, H2CO3LauncherUILayout parent, Profile profile, String version) {
        if (profile == null || version == null) return;

        ModpackTypeSelectionPage page = new ModpackTypeSelectionPage(context, PageManager.PAGE_ID_TEMP, parent, R.layout.page_modpack_type, profile, version);
        ManagePageManager.getInstance().showTempPage(page);
    }

    public static void duplicateVersion(Context context, Profile profile, String version) {
        if (profile == null || version == null) return;

        DuplicateVersionDialog dialog = new DuplicateVersionDialog(context, profile, version, (res, resolve, reject) -> {
            String newVersionName = (String) res.get(0);
            boolean copySaves = (boolean) res.get(1);
            ProgressDialog progress = new ProgressDialog(context);
            Task.runAsync(() -> profile.getRepository().duplicateVersion(version, newVersionName, copySaves))
                    .thenComposeAsync(profile.getRepository().refreshVersionsAsync())
                    .whenComplete(Schedulers.androidUIThread(), (result, exception) -> {
                        progress.dismiss();
                        if (exception == null) {
                            resolve.run();
                        } else {
                            reject.accept(StringUtils.getStackTrace(exception));
                            profile.getRepository().removeVersionFromDisk(newVersionName);
                        }
                    }).start();
        });
        dialog.show();
    }

    public static void updateVersion(Context context, H2CO3LauncherUILayout parent, Profile profile, String version) {
        if (profile == null || version == null) return;

        ModpackSelectionPage page = new ModpackSelectionPage(context, PageManager.PAGE_ID_TEMP, parent, R.layout.page_modpack_selection, profile, version);
        ManagePageManager.getInstance().showTempPage(page);
    }

    public static void updateGameAssets(Context context, Profile profile, String version) {
        if (profile == null || version == null) return;

        TaskExecutor executor = new GameAssetDownloadTask(profile.getDependency(), profile.getRepository().getVersion(version), GameAssetDownloadTask.DOWNLOAD_INDEX_FORCIBLY, true)
                .executor();
        TaskDialog dialog = new TaskDialog(context, TaskCancellationAction.NORMAL);
        dialog.setExecutor(executor);
        dialog.setTitle(context.getString(R.string.version_manage_redownload_assets_index));
        dialog.show();
        executor.start();
    }

    public static void cleanVersion(Profile profile, String id) {
        if (profile == null || id == null) return;

        try {
            profile.getRepository().clean(id);
        } catch (IOException e) {
            Logging.LOG.log(Level.WARNING, "Unable to clean game directory", e);
        }
    }

    public static void launch(Context context, Profile profile) {
        launch(context, profile, profile.getSelectedVersion());
    }

    public static void launch(Context context, Profile profile, String id) {
        launch(context, profile, id, null);
    }

    public static void launch(Context context, Profile profile, String id, Consumer<LauncherHelper> injector) {
        if (!checkVersionForLaunching(context, profile, id)) return;
        ensureSelectedAccount(context, account -> {
            LauncherHelper launcherHelper = new LauncherHelper(context, profile, account, id);
            if (injector != null) injector.accept(launcherHelper);
            launcherHelper.launch();
        });
    }

    private static boolean checkVersionForLaunching(Context context, Profile profile, String id) {
        if (id == null || profile == null || !profile.getRepository().isLoaded() || !profile.getRepository().hasVersion(id)) {
            showAlert(context, context.getString(R.string.launch_failed), context.getString(R.string.version_empty_launch));
            return false;
        }
        return true;
    }

    private static void ensureSelectedAccount(Context context, Consumer<Account> action) {
        Account account = Accounts.getSelectedAccount();
        if (account == null) {
            CreateAccountDialog dialog = new CreateAccountDialog(context, (AccountFactory<?>) null);
            dialog.alertDialog.setOnDismissListener(dialogInterface -> {
                Account newAccount = Accounts.getSelectedAccount();
                if (newAccount != null) {
                    action.accept(newAccount);
                }
            });
            dialog.createDialog();
        } else {
            action.accept(account);
        }
    }

    private static void showAlert(Context context, String title, String message) {
        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
        builder.create().show();
    }
}