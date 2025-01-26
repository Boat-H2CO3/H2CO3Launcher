package org.koishi.launcher.h2co3.ui.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherLinearLayout;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

import java.util.List;

public class DocCategoryAdapter extends H2CO3LauncherAdapter {

    private final List<DocIndex> list;

    private final ObjectProperty<DocIndex> selectedIndexProperty = new SimpleObjectProperty<>(null);

    public DocCategoryAdapter(Context context, List<DocIndex> list) {
        super(context);
        this.list = list;

        if (!list.isEmpty()) {
            selectedIndexProperty.set(list.get(0));
        }
        selectedIndexProperty.addListener(invalidate -> notifyDataSetChanged());
    }

    public ObjectProperty<DocIndex> selectedIndexProperty() {
        return selectedIndexProperty;
    }

    public DocIndex getSelectedIndex() {
        return selectedIndexProperty.get();
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
            view = new H2CO3LauncherLinearLayout(getContext());
            viewHolder.parent = new H2CO3LauncherLinearLayout(getContext());
            viewHolder.name = new H2CO3LauncherTextView(getContext());
            ((H2CO3LauncherLinearLayout) view).addView(viewHolder.parent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int padding = ConvertUtils.dip2px(getContext(), 10);
            viewHolder.parent.setPadding(padding, padding, padding, padding);
            viewHolder.parent.setBackground(getContext().getDrawable(R.drawable.bg_container_transparent_clickable));
            viewHolder.parent.addView(viewHolder.name, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewHolder.name.setSingleLine(true);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        DocIndex index = list.get(i);
        viewHolder.parent.setBackground(index == getSelectedIndex() ? getContext().getDrawable(R.drawable.bg_container_transparent_selected) : getContext().getDrawable(R.drawable.bg_container_transparent_clickable));
        viewHolder.parent.setOnClickListener(v -> selectedIndexProperty.set(index));
        viewHolder.name.setText(index.getDisplayName(getContext()));
        return view;
    }

    private static class ViewHolder {
        H2CO3LauncherLinearLayout parent;
        H2CO3LauncherTextView name;
    }
}
