package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.H2CO3LauncherGameRepository;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.ui.manage.ManagePageManager;
import org.koishi.launcher.h2co3core.mod.Modpack;
import org.koishi.launcher.h2co3core.mod.server.ServerModpackManifest;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

import java.io.IOException;

public class RemoteModpackPage extends ModpackPage {

    private final String updateVersion;
    private final ServerModpackManifest manifest;

    private Modpack modpack;

    public RemoteModpackPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, Profile profile, String updateVersion, ServerModpackManifest manifest) {
        super(context, id, parent, resId, profile);
        this.updateVersion = updateVersion;
        this.manifest = manifest;
    }

    @Override
    public void onStart() {
        super.onStart();

        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        try {
            modpack = manifest.toModpack(null);
        } catch (IOException e) {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.ALERT);
            builder.setCancelable(false);
            builder.setTitle(getContext().getString(R.string.message_error));
            builder.setMessage(getContext().getString(R.string.modpack_type_server_malformed));
            builder.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), () -> {
                if (updateVersion == null) {
                    DownloadPageManager.getInstance().dismissCurrentTempPage();
                } else {
                    ManagePageManager.getInstance().dismissCurrentTempPage();
                }
            });
            builder.create().show();
            return;
        }

        progressBar.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        describe.setVisibility(View.VISIBLE);

        name.setText(manifest.getName());
        version.setText(manifest.getVersion());
        author.setText(manifest.getAuthor());

        if (updateVersion != null) {
            editText.setText(updateVersion);
            editText.setEnabled(false);
        } else {
            editText.setText(manifest.getName().trim());
        }
    }

    @Override
    protected void onInstall() {
        String name;
        if (updateVersion != null) {
            name = updateVersion;
        } else {
            String str = editText.getText().toString();
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
            name = str;
        }
        Task<?> task;
        if (updateVersion == null) {
            task = ModpackInstaller.getModpackInstallTask(getContext(), profile, manifest, modpack, name);
        } else {
            task = ModpackInstaller.getModpackInstallTask(getContext(), profile, updateVersion, null, manifest, modpack, name);
        }
        ModpackInstaller.installModpack(getContext(), task, updateVersion != null);
    }

    @Override
    protected void onDescribe() {
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
