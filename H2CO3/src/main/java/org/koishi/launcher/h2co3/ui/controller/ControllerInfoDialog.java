package org.koishi.launcher.h2co3.ui.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3core.util.platform.OperatingSystem;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class ControllerInfoDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final boolean create;
    private final Controller controller;
    private final Callback callback;

    private H2CO3LauncherEditText editName;

    private H2CO3LauncherEditText editVersion;
    private H2CO3LauncherEditText editVersionCode;
    private H2CO3LauncherEditText editAuthor;
    private H2CO3LauncherEditText editDescription;

    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    @SuppressLint("SetTextI18n")
    public ControllerInfoDialog(@NonNull Context context, boolean create, Controller controller, Callback callback) {
        super(context);
        this.create = create;
        this.controller = controller;
        this.callback = callback;
        setContentView(R.layout.dialog_controller_info);
        setCancelable(false);

        H2CO3LauncherTextView titleView = findViewById(R.id.title);
        titleView.setText(create ? getContext().getString(R.string.control_create) : getContext().getString(R.string.control_info_edit));

        editName = findViewById(R.id.name);

        H2CO3LauncherLinearLayout moreInfoLayout = findViewById(R.id.more_info_layout);
        editVersion = findViewById(R.id.version);
        editVersionCode = findViewById(R.id.version_code);
        editAuthor = findViewById(R.id.author);
        editDescription = findViewById(R.id.description);

        editVersionCode.setIntegerFilter(1);

        editName.setText(controller.getName());
        editVersion.setText(controller.getVersion());
        editVersionCode.setText(controller.getVersionCode() + "");
        editAuthor.setText(controller.getAuthor());
        editDescription.setText(controller.getDescription());

        H2CO3LauncherCheckBox moreInfo = findViewById(R.id.more_info);
        moreInfo.addCheckedChangeListener();

        moreInfoLayout.visibilityProperty().bind(moreInfo.checkProperty());

        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            if (!OperatingSystem.isNameValid(editName.getText().toString()) || editName.getText().toString().equals("Error")) {
                Toast.makeText(getContext(), getContext().getString(R.string.control_info_name_invalid), Toast.LENGTH_SHORT).show();
            } else {
                String id = this.controller.getId();
                if (!editAuthor.getText().toString().equals(this.controller.getAuthor())) {
                    id = Controller.generateRandomId();
                }
                Controller controller = new Controller(id,
                        editName.getText().toString(),
                        editVersion.getText().toString(),
                        Integer.parseInt(StringUtils.isBlank(editVersionCode.getText().toString()) ? "1" : editVersionCode.getText().toString()),
                        editAuthor.getText().toString(),
                        editDescription.getText().toString(),
                        this.controller.getControllerVersion());
                callback.onInfoGenerate(controller);
                dismiss();
            }
        }
        if (view == negative) {
            dismiss();
        }
    }

    public interface Callback {
        void onInfoGenerate(Controller controller);
    }
}
