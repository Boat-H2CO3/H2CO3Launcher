package org.koishi.launcher.h2co3.control;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.ControlViewGroup;
import org.koishi.launcher.h2co3core.util.StringUtils;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSpinner;

import java.util.ArrayList;
import java.util.Objects;

public class EditViewGroupDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final GameMenu menu;
    private final ControlViewGroup viewGroup;
    private final Callback callback;

    private H2CO3LauncherEditText editText;
    private H2CO3LauncherSpinner<ControlViewGroup.Visibility> visibilitySpinner;

    private H2CO3LauncherButton positive;
    private H2CO3LauncherButton negative;

    public EditViewGroupDialog(@NonNull Context context, GameMenu menu, ControlViewGroup viewGroup, Callback callback) {
        super(context);
        this.menu = menu;
        this.viewGroup = viewGroup;
        this.callback = callback;
        setCancelable(false);
        setContentView(R.layout.dialog_edit_view_group);

        editText = findViewById(R.id.name);
        visibilitySpinner = findViewById(R.id.visibility);
        ArrayList<ControlViewGroup.Visibility> visibilities = new ArrayList<>();
        visibilities.add(ControlViewGroup.Visibility.VISIBLE);
        visibilities.add(ControlViewGroup.Visibility.INVISIBLE);
        visibilitySpinner.setDataList(visibilities);
        ArrayList<String> visibilityString = new ArrayList<>();
        visibilityString.add(getContext().getString(R.string.menu_control_view_group_visible));
        visibilityString.add(getContext().getString(R.string.menu_control_view_group_invisible));
        ArrayAdapter<String> visibilityAdapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner, visibilityString);
        visibilityAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        visibilitySpinner.setAdapter(visibilityAdapter);

        editText.setText(viewGroup.getName());
        visibilitySpinner.setSelection(viewGroup.getVisibility() == ControlViewGroup.Visibility.VISIBLE ? 0 : 1);

        positive = findViewById(R.id.positive);
        negative = findViewById(R.id.negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == positive) {
            if (menu.getController().viewGroups().stream().anyMatch(it -> it.getName().equals(Objects.requireNonNull(editText.getText()).toString()) && !viewGroup.getName().equals(editText.getText().toString()))) {
                Toast.makeText(getContext(), getContext().getString(R.string.menu_control_view_group_exist), Toast.LENGTH_SHORT).show();
            } else if (StringUtils.isBlank(Objects.requireNonNull(editText.getText()).toString())) {
                Toast.makeText(getContext(), getContext().getString(R.string.menu_control_view_group_empty), Toast.LENGTH_SHORT).show();
            } else {
                dismiss();
                callback.onPositive(editText.getText().toString(), visibilitySpinner.getSelectedItemPosition() == 0 ? ControlViewGroup.Visibility.VISIBLE : ControlViewGroup.Visibility.INVISIBLE);
            }
        }
        if (v == negative) {
            dismiss();
        }
    }

    public interface Callback {
        void onPositive(String name, ControlViewGroup.Visibility visibility);
    }
}
