package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;

import androidx.appcompat.app.AppCompatDialog;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.ManuallyCreatedModpackException;
import org.koishi.launcher.h2co3.game.ModpackHelper;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.ui.TaskDialog;
import org.koishi.launcher.h2co3.ui.manage.ManagePageManager;
import org.koishi.launcher.h2co3.util.TaskCancellationAction;
import org.koishi.launcher.h2co3core.mod.MismatchedModpackTypeException;
import org.koishi.launcher.h2co3core.mod.Modpack;
import org.koishi.launcher.h2co3core.mod.ModpackCompletionException;
import org.koishi.launcher.h2co3core.mod.UnsupportedModpackException;
import org.koishi.launcher.h2co3core.mod.server.ServerModpackManifest;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.task.TaskExecutor;
import org.koishi.launcher.h2co3core.task.TaskListener;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class ModpackInstaller {

    public static void installModpack(Context context, Task<?> task, boolean update) {
        TaskDialog pane = new TaskDialog(context, new TaskCancellationAction(TaskDialog::dismissDialog));
        pane.setTitle(context.getString(R.string.install_modpack));
        Schedulers.androidUIThread().execute(() -> {
            TaskExecutor executor = task.executor(new TaskListener() {
                @Override
                public void onStop(boolean success, TaskExecutor executor) {
                    Schedulers.androidUIThread().execute(() -> {
                        if (success) {
                            H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(context);
                            builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                            builder1.setCancelable(false);
                            builder1.setMessage(context.getString(R.string.install_success));
                            builder1.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> {
                                if (update) {
                                    ManagePageManager.getInstance().dismissCurrentTempPage();
                                } else {
                                    DownloadPageManager.getInstance().dismissCurrentTempPage();
                                }
                            });
                            builder1.create().show();
                        } else {
                            if (executor.getException() == null)
                                return;
                            if (executor.getException() instanceof ModpackCompletionException) {
                                if (executor.getException().getCause() instanceof FileNotFoundException) {
                                    H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(context);
                                    builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                                    builder1.setCancelable(false);
                                    builder1.setTitle(context.getString(R.string.install_failed));
                                    builder1.setMessage(context.getString(R.string.modpack_type_curse_not_found));
                                    builder1.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> {
                                        if (update) {
                                            ManagePageManager.getInstance().dismissCurrentTempPage();
                                        } else {
                                            DownloadPageManager.getInstance().dismissCurrentTempPage();
                                        }
                                    });
                                    builder1.create().show();
                                } else {
                                    H2CO3LauncherAlertDialog.Builder builder1 = new H2CO3LauncherAlertDialog.Builder(context);
                                    builder1.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
                                    builder1.setCancelable(false);
                                    builder1.setMessage(context.getString(R.string.install_success));
                                    builder1.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> {
                                        if (update) {
                                            ManagePageManager.getInstance().dismissCurrentTempPage();
                                        } else {
                                            DownloadPageManager.getInstance().dismissCurrentTempPage();
                                        }
                                    });
                                    builder1.create().show();
                                }
                            } else {
                                InstallersPage.alertFailureMessage(context, executor.getException(), () -> {});
                            }
                        }

                    });
                }
            });
            pane.setExecutor(executor);
            pane.createDialog();
            executor.start();
        });
    }

    public static Task<?> getModpackInstallTask(Profile profile, File selected, String name, Charset charset) {
        return getModpackInstallTaskAsync(null, profile, null, selected, null, null, name, charset, true);
    }

    public static Task<?> getModpackInstallTask(Context context, Profile profile, ServerModpackManifest serverModpackManifest, Modpack modpack, String name) {
        return getModpackInstallTask(context, profile, null, null, serverModpackManifest, modpack, name);
    }

    public static Task<?> getModpackInstallTask(Context context, Profile profile, File selected, Modpack modpack, String name) {
        return getModpackInstallTask(context, profile, null, selected, null, modpack, name);
    }

    public static Task<?> getModpackInstallTask(Context context, Profile profile, String updateVersion, File selected, Modpack modpack, String name) {
        return getModpackInstallTask(context, profile, updateVersion, selected, null, modpack, name);
    }

    public static Task<?> getModpackInstallTask(Context context, Profile profile, String updateVersion, File selected, ServerModpackManifest serverModpackManifest, Modpack modpack, String name) {
        return getModpackInstallTaskAsync(context, profile, updateVersion, selected, serverModpackManifest, modpack, name, null, false);
    }

    private static Task<?> getModpackInstallTaskAsync(Context context, Profile profile, String updateVersion, File selected, ServerModpackManifest serverModpackManifest, Modpack modpack, String name, Charset charset, boolean isManuallyCreated) {
        if (isManuallyCreated) {
            return ModpackHelper.getInstallManuallyCreatedModpackTask(profile, selected, name, charset);
        }

        if ((selected == null && serverModpackManifest == null) || modpack == null || name == null) return null;

        if (updateVersion != null) {
            if (selected == null) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                builder.setCancelable(false);
                builder.setTitle(context.getString(R.string.message_error));
                builder.setMessage(context.getString(R.string.modpack_unsupported));
                builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                builder.create().show();
                return null;
            }
            try {
                if (serverModpackManifest != null) {
                    return ModpackHelper.getUpdateTask(profile, serverModpackManifest, modpack.getEncoding(), name, ModpackHelper.readModpackConfiguration(profile.getRepository().getModpackConfiguration(name)));
                } else {
                    return ModpackHelper.getUpdateTask(profile, selected, modpack.getEncoding(), name, ModpackHelper.readModpackConfiguration(profile.getRepository().getModpackConfiguration(name)));
                }
            } catch (UnsupportedModpackException | ManuallyCreatedModpackException e) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                builder.setCancelable(false);
                builder.setTitle(context.getString(R.string.message_error));
                builder.setMessage(context.getString(R.string.modpack_unsupported));
                builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                builder.create().show();
            } catch (MismatchedModpackTypeException e) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                builder.setCancelable(false);
                builder.setTitle(context.getString(R.string.message_error));
                builder.setMessage(context.getString(R.string.modpack_mismatched_type));
                builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                builder.create().show();
            } catch (IOException e) {
                H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(context);
                builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                builder.setCancelable(false);
                builder.setTitle(context.getString(R.string.message_error));
                builder.setMessage(context.getString(R.string.modpack_invalid));
                builder.setNegativeButton(context.getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
                builder.create().show();
            }
            return null;
        } else {
            if (serverModpackManifest != null) {
                return ModpackHelper.getInstallTask(profile, serverModpackManifest, name, modpack)
                        .thenRunAsync(Schedulers.androidUIThread(), () -> profile.setSelectedVersion(name));
            } else {
                return ModpackHelper.getInstallTask(profile, selected, name, modpack)
                        .thenRunAsync(Schedulers.androidUIThread(), () -> profile.setSelectedVersion(name));
            }
        }
    }

}
