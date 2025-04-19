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

import java.util.function.Consumer;

public class EditDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final Consumer<String> callback;

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public EditDialog(@NonNull Context context, Consumer<String> callback) {
        super(context);
        this.callback = callback;
        setCancelable(false);
        setContentView(R.layout.dialog_edit);

        editText = findViewById(R.id.name);
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == positive) {
            String s = editText.getText().toString();
            if (!s.trim().equals("")) {
                callback.accept(s);
                dismiss();
            }
        }
        if (v == negative) {
            dismiss();
        }
    }

    public H2CO3LauncherEditText getEditText() {
        return editText;
    }
}
