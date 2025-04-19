package org.koishi.launcher.h2co3.ui.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3.ui.UIManager;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class EditableControllerListAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<Controller> list;

    public EditableControllerListAdapter(Context context, ObservableList<Controller> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_controller_editable, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.version = view.findViewById(R.id.version);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Controller controller = list.get(i);
        viewHolder.parent.setBackground(controller == ((ControllerManagePage) UIManager.getInstance().getControllerUI().getPage(ControllerPageManager.PAGE_ID_CONTROLLER_MANAGER)).getSelectedController() ? getContext().getDrawable(R.drawable.bg_container_transparent_selected) : getContext().getDrawable(R.drawable.bg_container_transparent_clickable));
        viewHolder.name.stringProperty().bind(controller.nameProperty());
        viewHolder.version.stringProperty().bind(controller.versionProperty());
        viewHolder.parent.setOnClickListener(view1 -> {
            ((ControllerManagePage) UIManager.getInstance().getControllerUI().getPage(ControllerPageManager.PAGE_ID_CONTROLLER_MANAGER)).setSelectedController(controller);
            notifyDataSetChanged();
        });
        viewHolder.delete.setOnClickListener(view1 -> {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
            builder.setCancelable(false);
            builder.setMessage(getContext().getString(R.string.control_delete));
            builder.setPositiveButton(() -> ((ControllerManagePage) UIManager.getInstance().getControllerUI().getPage(ControllerPageManager.PAGE_ID_CONTROLLER_MANAGER)).removeController(controller));
            builder.setNegativeButton(null);
            builder.create().show();
        });
        return view;
    }

    static class ViewHolder {
        ConstraintLayout parent;
        H2CO3LauncherTextView name;
        H2CO3LauncherTextView version;
        H2CO3LauncherImageButton delete;
    }
}
