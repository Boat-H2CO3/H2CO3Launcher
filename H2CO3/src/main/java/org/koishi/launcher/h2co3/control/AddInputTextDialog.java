package org.koishi.launcher.h2co3.control;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.QuickInputTexts;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;

import java.util.Objects;

public class AddInputTextDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final Callback callback;

    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;
    private H2CO3LauncherEditText editText;

    public AddInputTextDialog(@NonNull Context context, Callback callback) {
        super(context);
        this.callback = callback;
        setCancelable(false);
        setContentView(R.layout.dialog_add_input_text);

        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        editText = findViewById(R.id.text);
    }

    @Override
    public void onClick(View v) {
        if (v == positive) {
            if (StringUtils.isBlank(Objects.requireNonNull(editText.getText()).toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.quick_input_empty), Toast.LENGTH_SHORT).show();
            } else if (QuickInputTexts.getInputTexts().contains(editText.getText().toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.quick_input_exist), Toast.LENGTH_SHORT).show();
            } else {
                QuickInputTexts.addInputText(editText.getText().toString());
                callback.onTextAdd();
                dismiss();
            }
        }
        if (v == negative) {
            dismiss();
        }
    }

    public interface Callback {
        void onTextAdd();
    }
}
