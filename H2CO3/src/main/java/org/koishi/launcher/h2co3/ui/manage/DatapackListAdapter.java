package org.koishi.launcher.h2co3.ui.manage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleListProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;

import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class DatapackListAdapter extends H2CO3LauncherAdapter {

    private final ListProperty<DatapackListPage.DatapackInfoObject> listProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<DatapackListPage.DatapackInfoObject> selectedItemsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private boolean fromSelf = false;

    public DatapackListAdapter(Context context) {
        super(context);

        Handler handler = new Handler();

        this.listProperty.addListener((InvalidationListener) observable -> {
            fromSelf = true;
            selectedItemsProperty.clear();
            fromSelf = false;
            handler.post(this::notifyDataSetChanged);
        });
        selectedItemsProperty.addListener((InvalidationListener) observable -> {
            if (!fromSelf) {
                handler.post(this::notifyDataSetChanged);
            }
        });
    }

    public ListProperty<DatapackListPage.DatapackInfoObject> listProperty() {
        return listProperty;
    }

    public ListProperty<DatapackListPage.DatapackInfoObject> selectedItemsProperty() {
        return selectedItemsProperty;
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_datapack, null);
            viewHolder.parent = view.findViewById(R.id.parent);
            viewHolder.checkBox = view.findViewById(R.id.check);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.description = view.findViewById(R.id.description);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DatapackListPage.DatapackInfoObject datapackInfoObject = listProperty.get(i);
        viewHolder.parent.setOnClickListener(v -> {
            if (selectedItemsProperty.contains(datapackInfoObject)) {
                fromSelf = true;
                selectedItemsProperty.remove(datapackInfoObject);
                fromSelf = false;
            } else {
                fromSelf = true;
                selectedItemsProperty.add(datapackInfoObject);
                fromSelf = false;
            }
        });
        viewHolder.checkBox.addCheckedChangeListener();
        if (viewHolder.booleanProperty != null) {
            viewHolder.checkBox.checkProperty().unbindBidirectional(viewHolder.booleanProperty);
        }
        viewHolder.checkBox.checkProperty().bindBidirectional(viewHolder.booleanProperty = datapackInfoObject.getActive());
        viewHolder.name.setText(datapackInfoObject.getTitle());
        viewHolder.description.setText(datapackInfoObject.getSubtitle());
        return view;
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherCheckBox checkBox;
        H2CO3LauncherTextView name;
        H2CO3LauncherTextView description;
        BooleanProperty booleanProperty;
    }
}
