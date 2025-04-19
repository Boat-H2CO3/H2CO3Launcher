package org.koishi.launcher.h2co3.ui.manage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class ModUpdateListAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<ModUpdatesPage.ModUpdateObject> list;

    public ModUpdateListAdapter(Context context, ObservableList<ModUpdatesPage.ModUpdateObject> list) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_update_mod, null);
            viewHolder.checkBox = view.findViewById(R.id.check);
            viewHolder.file = view.findViewById(R.id.name);
            viewHolder.source = view.findViewById(R.id.source);
            viewHolder.desc = view.findViewById(R.id.desc);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ModUpdatesPage.ModUpdateObject modUpdateObject = list.get(i);
        viewHolder.checkBox.addCheckedChangeListener();
        if (viewHolder.booleanProperty != null) {
            viewHolder.checkBox.checkProperty().unbindBidirectional(viewHolder.booleanProperty);
        }
        viewHolder.checkBox.checkProperty().bindBidirectional(viewHolder.booleanProperty = modUpdateObject.enabledProperty());
        viewHolder.file.setText(modUpdateObject.getFileName());
        viewHolder.source.setText(modUpdateObject.getSource());
        viewHolder.desc.setText(modUpdateObject.getCurrentVersion() + "  ->  " + modUpdateObject.getTargetVersion());
        return view;
    }

    private static class ViewHolder {
        H2CO3LauncherCheckBox checkBox;
        H2CO3LauncherTextView file;
        H2CO3LauncherTextView source;
        H2CO3LauncherTextView desc;
        BooleanProperty booleanProperty;
    }
}
