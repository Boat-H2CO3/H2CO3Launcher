package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.util.platform.OperatingSystem;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;

public class DownloadAddonDialog extends MaterialAlertDialogBuilder implements View.OnClickListener {

    private final Callback callback;
    AlertDialog alertDialog;
    private H2CO3LauncherEditText editText;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public DownloadAddonDialog(@NonNull Context context, String name, Callback callback) {
        super(context);
        this.callback = callback;
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.edit_text, null);
        setView(view);
        alertDialog = this.create();
        alertDialog.setCancelable(false);

        editText = view.findViewById(R.id.name);
        editText.setText(name);

        positive = view.findViewById(R.id.positive);
        negative = view.findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == positive) {
            if (!OperatingSystem.isNameValid(editText.getText().toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
            } else {
                callback.onPositive(editText.getText().toString());
                alertDialog.dismiss();
            }
        }
        if (v == negative) {
            alertDialog.dismiss();
        }
    }

    public interface Callback {
        void onPositive(String name);
    }
}
