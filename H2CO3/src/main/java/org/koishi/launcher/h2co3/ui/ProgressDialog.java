package org.koishi.launcher.h2co3.ui;

import android.content.Context;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherProgressBar;

public class ProgressDialog extends H2CO3LauncherDialog {
    public ProgressDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        H2CO3LauncherProgressBar progressBar = new H2CO3LauncherProgressBar(context);
        setContentView(progressBar);
        show();
    }
}
