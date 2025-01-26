package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.util.FutureCallback;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;

import java.util.concurrent.CompletableFuture;

public class RenameVersionDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final FutureCallback<String> callback;
    private final CompletableFuture<String> future = new CompletableFuture<>();

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public RenameVersionDialog(@NonNull Context context, String oldName, FutureCallback<String> callback) {
        super(context);
        setContentView(R.layout.dialog_rename_version);
        setCancelable(false);
        this.callback = callback;
        editText = findViewById(R.id.new_name);
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
        editText.setText(oldName);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            positive.setEnabled(false);
            negative.setEnabled(false);
            callback.call(editText.getText().toString(), () -> {
                positive.setEnabled(true);
                negative.setEnabled(true);
                future.complete(editText.getText().toString());
                dismiss();
            }, msg -> {
                positive.setEnabled(true);
                negative.setEnabled(true);
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            });
        }
        if (view == negative) {
            dismiss();
        }
    }

    public CompletableFuture<String> getFuture() {
        return future;
    }
}
