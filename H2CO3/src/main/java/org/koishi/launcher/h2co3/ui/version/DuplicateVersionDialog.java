package org.koishi.launcher.h2co3.ui.version;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.game.H2CO3LauncherGameRepository;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3core.util.FutureCallback;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;

import java.util.ArrayList;
import java.util.Objects;

public class DuplicateVersionDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final Profile profile;
    private final String version;
    private final FutureCallback<ArrayList<Object>> callback;

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherCheckBox checkBox;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public DuplicateVersionDialog(@NonNull Context context, Profile profile, String version, FutureCallback<ArrayList<Object>> callback) {
        super(context);
        this.profile = profile;
        this.version = version;
        this.callback = callback;
        setContentView(R.layout.dialog_duplicate_version);
        setCancelable(false);

        editText = findViewById(R.id.new_name);
        checkBox = findViewById(R.id.check_save);
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);

        editText.setText(version);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            String newVersionName = Objects.requireNonNull(editText.getText()).toString();
            if (StringUtils.isBlank(newVersionName)) {
                Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
            } else if (profile.getRepository().versionIdConflicts(newVersionName)) {
                Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_already_exists), Toast.LENGTH_SHORT).show();
            } else if (!H2CO3LauncherGameRepository.isValidVersionId(newVersionName)) {
                Toast.makeText(getContext(), getContext().getString(R.string.install_new_game_malformed), Toast.LENGTH_SHORT).show();
            } else {
                positive.setEnabled(false);
                negative.setEnabled(false);
                ArrayList<Object> res = new ArrayList<>();
                res.add(newVersionName);
                res.add(checkBox.isChecked());
                callback.call(res, () -> {
                    positive.setEnabled(true);
                    negative.setEnabled(true);
                    dismiss();
                }, msg -> {
                    positive.setEnabled(true);
                    negative.setEnabled(true);
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                });
            }
        }
        if (view == negative) {
            dismiss();
        }
    }
}
