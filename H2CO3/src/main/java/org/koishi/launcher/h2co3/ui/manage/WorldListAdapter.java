package org.koishi.launcher.h2co3.ui.manage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleListProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class WorldListAdapter extends H2CO3LauncherAdapter {

    private final ListProperty<WorldListItem> listProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public WorldListAdapter(Context context) {
        super(context);

        listProperty.addListener((InvalidationListener) observable -> {
            notifyDataSetChanged();
        });
    }

    public ListProperty<WorldListItem> listProperty() {
        return listProperty;
    }

    @Override
    public int getCount() {
        return listProperty.getSize();
    }

    @Override
    public Object getItem(int i) {
        return listProperty.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_world, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.description = view.findViewById(R.id.description);
            viewHolder.datapack = view.findViewById(R.id.datapack);
            viewHolder.export = view.findViewById(R.id.export);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        WorldListItem worldListItem = listProperty.get(i);
        viewHolder.parent.setOnClickListener(v -> worldListItem.showInfo());
        viewHolder.name.stringProperty().bind(worldListItem.titleProperty());
        viewHolder.description.stringProperty().bind(worldListItem.subtitleProperty());
        viewHolder.datapack.setOnClickListener(v -> worldListItem.manageDatapacks());
        viewHolder.export.setOnClickListener(v -> worldListItem.export());
        return view;
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherTextView name;
        H2CO3LauncherTextView description;
        H2CO3LauncherImageButton datapack;
        H2CO3LauncherImageButton export;
    }
}
