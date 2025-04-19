package org.koishi.launcher.h2co3.ui.download;

import static org.koishi.launcher.h2co3core.util.Logging.LOG;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.H2CO3LauncherGameRepository;
import org.koishi.launcher.h2co3.game.ManuallyCreatedModpackException;
import org.koishi.launcher.h2co3.game.ModpackHelper;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.ui.manage.ManagePageManager;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.mod.Modpack;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.io.CompressingUtils;
import org.koishi.launcher.h2co3core.util.io.FileUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.File;
import java.nio.charset.Charset;
import java.util.logging.Level;

public class LocalModpackPage extends ModpackPage implements View.OnClickListener {

    private final String updateVersion;
    private final File modpackFile;

    private final BooleanProperty installAsVersion = new SimpleBooleanProperty(true);
    private boolean isManuallyCreated = false;
    private Modpack manifest = null;
    private Charset charset;

    public LocalModpackPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, Profile profile, String updateVersion, File modpackFile) {
        super(context, id, parent, resId, profile);
        this.updateVersion = updateVersion;
        this.modpackFile = modpackFile;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (updateVersion != null) {
            editText.setText(updateVersion);
            editText.setEnabled(false);
        }

        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        Task.supplyAsync(() -> CompressingUtils.findSuitableEncoding(modpackFile.toPath()))
                .thenApplyAsync(encoding -> {
                    charset = encoding;
                    manifest = ModpackHelper.readModpackManifest(modpackFile.toPath(), encoding);
                    return manifest;
                })
                .whenComplete(Schedulers.androidUIThread(), (manifest, exception) -> {
                    if (exception instanceof ManuallyCreatedModpackException) {
                        progressBar.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        name.setText(modpackFile.getName());
                        installAsVersion.set(false);

                        if (updateVersion == null) {
                            editText.setText(FileUtils.getNameWithoutExtension(modpackFile));
                        }

                        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                        builder.setCancelable(false);
                        builder.setTitle(getContext().getString(R.string.install_modpack));
                        builder.setMessage(getContext().getString(R.string.modpack_type_manual_warning));
                        builder.setPositiveButton(null);
                        builder.setNegativeButton(() -> {
                            if (updateVersion == null) {
                                DownloadPageManager.getInstance().dismissCurrentTempPage();
                            } else {
                                ManagePageManager.getInstance().dismissCurrentTempPage();
                            }
                        });
                        builder.create().show();

                        this.manifest = null;
                        isManuallyCreated = true;
                        describe.setVisibility(View.GONE);
                    } else if (exception != null) {
                        LOG.log(Level.WARNING, "Failed to read modpack manifest", exception);
                        H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
                        builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
                        builder.setCancelable(false);
                        builder.setTitle(getContext().getString(R.string.message_error));
                        builder.setMessage(getContext().getString(R.string.modpack_task_install_error));
                        builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> {
                            if (updateVersion == null) {
                                DownloadPageManager.getInstance().dismissCurrentTempPage();
                            } else {
                                ManagePageManager.getInstance().dismissCurrentTempPage();
                            }
                        });
                        builder.create().show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        name.setText(manifest.getName());
                        version.setText(manifest.getVersion());
                        author.setText(manifest.getAuthor());

                        if (updateVersion == null) {
                            editText.setText(manifest.getName().trim());
                        }
                        describe.setVisibility(View.VISIBLE);
                    }
                }).start();
    }

    @Override
    protected void onInstall() {
        String name;
        if (updateVersion != null) {
            name = updateVersion;
        } else {
            String str = editText.getText().toString();
            if (installAsVersion.get()) {
                if (StringUtils.isBlank(str)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
                    return;
                } else if (profile.getRepository().versionIdConflicts(str)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_already_exists), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!H2CO3LauncherGameRepository.isValidVersionId(str)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if (StringUtils.isBlank(str)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
                    return;
                } else if (ModpackHelper.isExternalGameNameConflicts(str) || Profiles.getProfiles().stream().anyMatch(p -> p.getName().equals(str))) {
                    Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_already_exists), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!H2CO3LauncherGameRepository.isValidVersionId(str)) {
                    Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            name = str;
        }
        Task<?> task;
        if (isManuallyCreated) {
            task = ModpackInstaller.getModpackInstallTask(profile, modpackFile, name, charset);
        } else {
            if (updateVersion == null) {
                task = ModpackInstaller.getModpackInstallTask(getContext(), profile, modpackFile, manifest, name);
            } else {
                task = ModpackInstaller.getModpackInstallTask(getContext(), profile, updateVersion, modpackFile, manifest, name);
            }
        }
        ModpackInstaller.installModpack(getContext(), task, updateVersion != null);
    }

    @Override
    protected void onDescribe() {
        if (manifest != null) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setCancelable(false);
            builder.setTitle(getContext().getString(R.string.modpack_description));
            CharSequence charSequence = Html.fromHtml(manifest.getDescription(), 0);
            builder.setMessage(charSequence);
            builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), null);
            builder.create().show();
        }
    }
}
