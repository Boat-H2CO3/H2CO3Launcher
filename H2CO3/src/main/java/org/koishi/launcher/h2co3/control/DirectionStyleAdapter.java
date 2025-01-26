package org.koishi.launcher.h2co3.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.BaseInfoData;
import org.koishi.launcher.h2co3.control.data.ControlDirectionStyle;
import org.koishi.launcher.h2co3.control.data.DirectionStyles;
import org.koishi.launcher.h2co3.control.view.ControlDirection;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherRadioButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class DirectionStyleAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<ControlDirectionStyle> list;
    private final boolean select;

    private final ObjectProperty<ControlDirectionStyle> selectedStyle = new SimpleObjectProperty<>(this, "style", null);

    public DirectionStyleAdapter(Context context, ObservableList<ControlDirectionStyle> list, boolean select, ControlDirectionStyle initStyle) {
        super(context);
        this.list = list;
        this.select = select;

        if (DirectionStyles.getStyles().stream().anyMatch(it -> it == initStyle)) {
            selectedStyle.set(initStyle);
        } else  {
            selectedStyle.set(list.get(0));
        }
    }

    public ObjectProperty<ControlDirectionStyle> selectedStyleProperty() {
        return selectedStyle;
    }

    public ControlDirectionStyle getSelectedStyle() {
        return selectedStyle.get();
    }

    public void setSelectedStyle(ControlDirectionStyle style) {
        selectedStyle.set(style);
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_direction_style, null);
            viewHolder.direction = view.findViewById(R.id.direction);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.radioButton = view.findViewById(R.id.radio_button);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ControlDirectionStyle style = list.get(i);
        viewHolder.direction.getData().setStyle(style);
        viewHolder.direction.getData().getBaseInfo().setSizeType(BaseInfoData.SizeType.ABSOLUTE);
        viewHolder.direction.getData().getBaseInfo().setAbsoluteWidth(60);
        viewHolder.direction.getData().getBaseInfo().setAbsoluteHeight(60);
        viewHolder.name.setText(style.getName());
        if (select) {
            viewHolder.radioButton.setVisibility(View.VISIBLE);
            viewHolder.delete.setVisibility(View.GONE);
        } else {
            viewHolder.radioButton.setVisibility(View.GONE);
            viewHolder.delete.setVisibility(View.VISIBLE);
        }
        viewHolder.radioButton.checkProperty().unbind();
        viewHolder.radioButton.checkProperty().bind(Bindings.createBooleanBinding(() -> selectedStyle.get() == style, selectedStyle));
        viewHolder.radioButton.setOnClickListener(view1 -> selectedStyle.set(style));
        viewHolder.delete.setOnClickListener(view1 -> {
            H2CO3LauncherAlertDialog.Builder builder = new H2CO3LauncherAlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setAlertLevel(H2CO3LauncherAlertDialog.AlertLevel.INFO);
            builder.setMessage(getContext().getString(R.string.style_warning_delete));
            builder.setPositiveButton(() -> {
                DirectionStyles.removeStyles(style);
                DirectionStyles.checkStyles();
                notifyDataSetChanged();
            });
            builder.setNegativeButton(null);
            builder.create().show();
        });
        return view;
    }

    static class ViewHolder {
        ControlDirection direction;
        H2CO3LauncherTextView name;
        H2CO3LauncherRadioButton radioButton;
        H2CO3LauncherImageButton delete;
    }
}
