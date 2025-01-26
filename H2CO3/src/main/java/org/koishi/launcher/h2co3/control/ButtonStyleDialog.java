package org.koishi.launcher.h2co3.control;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.ButtonStyles;
import org.koishi.launcher.h2co3.control.data.ControlButtonStyle;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherButton;
import org.koishi.launcher.h2co3library.component.view.HorizontalListView;

public class ButtonStyleDialog extends H2CO3LauncherDialog implements View.OnClickListener {

    private final boolean select;
    private final ControlButtonStyle initStyle;
    private final Callback callback;

    private H2CO3LauncherButton addStyle;
    private H2CO3LauncherButton editStyle;
    private H2CO3LauncherButton positive;

    private HorizontalListView listView;
    private ButtonStyleAdapter adapter;

    public ButtonStyleDialog(@NonNull Context context, boolean select, @Nullable ControlButtonStyle initStyle, Callback callback) {
        super(context);
        this.select = select;
        this.initStyle = initStyle;
        this.callback = callback;
        setContentView(R.layout.dialog_manage_button_style);
        setCancelable(false);

        addStyle = findViewById(R.id.add_style);
        editStyle = findViewById(R.id.edit_style);
        positive = findViewById(R.id.positive);
        addStyle.setOnClickListener(this);
        editStyle.setOnClickListener(this);
        positive.setOnClickListener(this);

        listView = findViewById(R.id.list);
        refreshList();

        if (!select) {
            editStyle.setVisibility(View.GONE);
        }
    }

    public void refreshList() {
        adapter = new ButtonStyleAdapter(getContext(), ButtonStyles.getStyles(), select, initStyle);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v == addStyle) {
            AddButtonStyleDialog dialog = new AddButtonStyleDialog(getContext(), null, false, style -> {
                ButtonStyles.addStyle(style);
                refreshList();
            });
            dialog.show();
        }
        if (v == editStyle) {
            AddButtonStyleDialog dialog = new AddButtonStyleDialog(getContext(), adapter.getSelectedStyle(), true, style -> {
                ButtonStyles.removeStyles(adapter.getSelectedStyle());
                ButtonStyles.addStyle(style);
                refreshList();
            });
            dialog.show();
        }
        if (v == positive) {
            dismiss();
            if (callback != null && select) {
                callback.onStyleSelect(adapter.getSelectedStyle());
            }
        }
    }

    public interface Callback {
        void onStyleSelect(ControlButtonStyle style);
    }
}
