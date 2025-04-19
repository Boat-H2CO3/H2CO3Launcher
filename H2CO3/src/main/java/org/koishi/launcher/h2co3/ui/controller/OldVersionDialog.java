package org.koishi.launcher.h2co3.ui.controller;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.download.ControllerVersion;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;

import java.util.ArrayList;

public class OldVersionDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private ListView listView;
    private H2CO3LauncherButton cancel;

    public OldVersionDialog(@NonNull Context context, ArrayList<ControllerVersion.VersionInfo> versionInfos, Callback callback) {
        super(context);
        setCancelable(false);
        setContentView(R.layout.dialog_download_controllor);

        listView = findViewById(R.id.list);
        HistoricalListAdapter adapter = new HistoricalListAdapter(getContext(), versionInfos, versionInfo -> {
            callback.download(versionInfo.getVersionCode());
            dismiss();
        });
        listView.setAdapter(adapter);

        cancel = findViewById(R.id.negative);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == cancel) {
            dismiss();
        }
    }

    public interface Callback {
        void download(int versionCode);
    }
}
