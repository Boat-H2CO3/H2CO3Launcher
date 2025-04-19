package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;

public class ModpackSelectionDialog extends H2CO3LauncherDialog {

    public ModpackSelectionDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_modpack_selection);
        setCancelable(false);
    }
}
