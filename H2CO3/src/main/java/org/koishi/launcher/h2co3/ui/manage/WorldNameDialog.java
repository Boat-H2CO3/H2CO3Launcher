package org.koishi.launcher.h2co3.ui.manage;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.util.FutureCallback;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;

public class WorldNameDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final FutureCallback<String> callback;

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public WorldNameDialog(@NonNull Context context, String name, FutureCallback<String> callback) {
        super(context);
        this.callback = callback;
        setCancelable(false);
        setContentView(R.layout.dialog_world_name);

        editText = findViewById(R.id.name);
        editText.setText(name);
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    private void setLoading(boolean loading) {
        editText.setEnabled(!loading);
        positive.setEnabled(!loading);
        negative.setEnabled(!loading);
    }

    @Override
    public void onClick(View v) {
        if (v == positive) {
            setLoading(true);
            callback.call(editText.getText().toString(), () -> {
                setLoading(false);
                dismiss();
            }, s -> {
                setLoading(false);
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            });
        }
        if (v == negative) {
            dismiss();
        }
    }
}
