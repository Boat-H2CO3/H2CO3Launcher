package org.koishi.launcher.h2co3.ui.download;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;

public class ModpackUrlDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final Callback callback;

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public ModpackUrlDialog(@NonNull Context context, Callback callback) {
        super(context);
        this.callback = callback;
        setCancelable(false);
        setContentView(R.layout.dialog_modpack_url);

        editText = findViewById(R.id.url);

        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == positive) {
            if (StringUtils.isNotBlank(editText.getText().toString())) {
                callback.onPositive(editText.getText().toString());
                dismiss();
            }
        }
        if (v == negative) {
            dismiss();
        }
    }

    public interface Callback {
        void onPositive(String urlString);
    }
}
