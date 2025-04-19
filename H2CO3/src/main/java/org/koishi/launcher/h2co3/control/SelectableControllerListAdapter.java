package org.koishi.launcher.h2co3.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.setting.Controller;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherRadioButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class SelectableControllerListAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<Controller> list;
    private final SelectControllerDialog dialog;

    public SelectableControllerListAdapter(Context context, ObservableList<Controller> list, SelectControllerDialog dialog) {
        super(context);
        this.list = list;
        this.dialog = dialog;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_controller_selectable, null);
            viewHolder.radioButton = view.findViewById(R.id.radio);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.version = view.findViewById(R.id.version);
            viewHolder.description = view.findViewById(R.id.description);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Controller controller = list.get(i);
        viewHolder.radioButton.setChecked(controller == dialog.getSelectedController());
        viewHolder.name.stringProperty().bind(controller.nameProperty());
        viewHolder.version.stringProperty().bind(controller.versionProperty());
        viewHolder.description.stringProperty().bind(controller.descriptionProperty());
        viewHolder.radioButton.setOnClickListener(view1 -> {
            dialog.setSelectedController(controller);
            notifyDataSetChanged();
        });
        return view;
    }

    static class ViewHolder {
        H2CO3LauncherRadioButton radioButton;
        H2CO3LauncherTextView name;
        H2CO3LauncherTextView version;
        H2CO3LauncherTextView description;
    }
}
