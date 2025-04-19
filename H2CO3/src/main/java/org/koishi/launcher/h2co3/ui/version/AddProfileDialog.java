package org.koishi.launcher.h2co3.ui.version;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.activity.H2CO3MainActivity;
import org.koishi.launcher.h2co3.setting.Profile;
import org.koishi.launcher.h2co3.setting.Profiles;
import org.koishi.launcher.h2co3.ui.UIManager;
import org.koishi.launcher.h2co3.util.RequestCodes;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.browser.FileBrowser;
import org.koishi.launcher.h2co3library.browser.options.LibMode;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3CustomViewDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

import java.io.File;
import java.util.ArrayList;

public class AddProfileDialog extends H2CO3CustomViewDialog implements View.OnClickListener {

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherTextView pathText;
    private H2CO3LauncherImageButton editPath;
    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public AddProfileDialog(@NonNull Context context) {
        super(context);
        setCustomView(R.layout.dialog_add_profile);
        setCancelable(false);
        editText = findViewById(R.id.name);
        pathText = findViewById(R.id.path);
        editPath = findViewById(R.id.edit);
        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        alertDialog = create();
        editPath.setOnClickListener(this);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == editPath) {
            FileBrowser.Builder builder = new FileBrowser.Builder(getContext());
            builder.setLibMode(LibMode.FOLDER_CHOOSER);
            builder.setTitle(getContext().getString(R.string.profile_select));
            builder.create().browse(UIManager.getInstance().getMainUI().getActivity(), RequestCodes.SELECT_PROFILE_CODE, (requestCode, resultCode, data) -> {
                if (requestCode == RequestCodes.SELECT_PROFILE_CODE && resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<String> strings = FileBrowser.getSelectedFiles(data);
                    pathText.setText(strings.get(0));
                }
            });
        }
        if (view == positive) {
            if (StringUtils.isBlank(editText.getText().toString()) || StringUtils.isBlank(pathText.getText().toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.input_not_empty), Toast.LENGTH_SHORT).show();
            } else if (Profiles.getProfiles().stream().anyMatch(profile -> profile.getName().equals(editText.getText().toString()))) {
                Toast.makeText(getContext(), getContext().getString(R.string.profile_already_exist), Toast.LENGTH_SHORT).show();
            } else {
                Profiles.getProfiles().add(new Profile(editText.getText().toString(), new File(pathText.getText().toString())));
                H2CO3MainActivity.getInstance().uiManager.getMainUI().refreshProfile();
                dismissDialog();
            }
        }
        if (view == negative) {
            dismissDialog();
        }
    }
}
