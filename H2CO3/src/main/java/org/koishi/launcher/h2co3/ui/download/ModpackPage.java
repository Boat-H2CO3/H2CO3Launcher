package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ScrollView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3core.task.Task;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;

import org.koishi.launcher.h2co3library.component.ui.H2CO3LauncherTempPage;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherUILayout;

public abstract class ModpackPage extends H2CO3LauncherTempPage implements View.OnClickListener {

    protected final Profile profile;

    protected H2CO3LauncherProgressBar progressBar;
    protected H2CO3LauncherLinearLayout layout;
    protected ScrollView infoLayout;

    protected H2CO3LauncherEditText editText;
    protected H2CO3LauncherTextView name;
    protected H2CO3LauncherTextView version;
    protected H2CO3LauncherTextView author;

    protected H2CO3LauncherButton install;
    protected H2CO3LauncherButton describe;

    public ModpackPage(Context context, int id, H2CO3LauncherUILayout parent, int resId, Profile profile) {
        super(context, id, parent, resId);
        this.profile = profile;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        progressBar = findViewById(R.id.progress);
        layout = findViewById(R.id.layout);
        infoLayout = findViewById(R.id.info_layout);
        editText = findViewById(R.id.name);
        name = findViewById(R.id.modpack_name);
        version = findViewById(R.id.version);
        author = findViewById(R.id.author);
        install = findViewById(R.id.install);
        describe = findViewById(R.id.describe);
        install.setOnClickListener(this);
        describe.setOnClickListener(this);
    }

    @Override
    public Task<?> refresh(Object... param) {
        return null;
    }

    @Override
    public void onRestart() {

    }

    protected abstract void onInstall();

    protected abstract void onDescribe();

    @Override
    public void onClick(View v) {
        if (v == install) {
            H2CO3LauncherAlertDialog dialog = new H2CO3LauncherAlertDialog(getContext());
            dialog.setTitle(R.string.modpack_download_warn_title);
            dialog.setMessage(getContext().getString(R.string.modpack_download_warn_msg));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setPositiveButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_positive), this::onInstall);
            dialog.setNegativeButton(getContext().getString(org.koishi.launcher.h2co3library.R.string.dialog_negative), null);
            dialog.show();
        }
        if (v == describe) {
            onDescribe();
        }
    }
}
