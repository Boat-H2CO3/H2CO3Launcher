package org.koishi.launcher.h2co3library.component.dialog;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class H2CO3MaterialDialog extends MaterialAlertDialogBuilder {
    public AlertDialog alertDialog;
    public MaterialAlertDialogBuilder builder;

    public H2CO3MaterialDialog(@NonNull Context context) {
        super(context);
        this.builder = new MaterialAlertDialogBuilder((context));
        alertDialog = create();
    }

    public H2CO3MaterialDialog(@NonNull Context context, int style) {
        super(context, style);
        this.builder = new MaterialAlertDialogBuilder((context));
        alertDialog = create();
    }

    public void createDialog() {
        alertDialog.show();
    }

    public void dismissDialog() {
        alertDialog.dismiss();
    }
}