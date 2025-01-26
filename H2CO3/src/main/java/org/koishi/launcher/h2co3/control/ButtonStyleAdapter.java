package org.koishi.launcher.h2co3.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3.control.data.ButtonStyles;
import org.koishi.launcher.h2co3.control.data.ControlButtonStyle;
import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3library.component.H2CO3LauncherAdapter;
import org.koishi.launcher.h2co3library.component.dialog.H2CO3LauncherAlertDialog;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherImageButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherRadioButton;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

public class ButtonStyleAdapter extends H2CO3LauncherAdapter {

    private final ObservableList<ControlButtonStyle> list;
    private final boolean select;

    private final ObjectProperty<ControlButtonStyle> selectedStyle = new SimpleObjectProperty<>(this, "style", null);

    public ButtonStyleAdapter(Context context, ObservableList<ControlButtonStyle> list, boolean select, ControlButtonStyle initStyle) {
        super(context);
        this.list = list;
        this.select = select;

        if (ButtonStyles.getStyles().stream().anyMatch(it -> it == initStyle)) {
            selectedStyle.set(initStyle);
        } else  {
            selectedStyle.set(list.get(0));
        }
    }

    public ObjectProperty<ControlButtonStyle> selectedStyleProperty() {
        return selectedStyle;
    }

    public ControlButtonStyle getSelectedStyle() {
        return selectedStyle.get();
    }

    public void setSelectedStyle(ControlButtonStyle style) {
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_button_style, null);
            viewHolder.button = view.findViewById(R.id.button);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.radioButton = view.findViewById(R.id.radio_button);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ControlButtonStyle style = list.get(i);
        GradientDrawable drawableNormal = new GradientDrawable();
        drawableNormal.setCornerRadius(ConvertUtils.dip2px(getContext(), style.getCornerRadius() / 10f));
        drawableNormal.setStroke(ConvertUtils.dip2px(getContext(), style.getStrokeWidth() / 10f), style.getStrokeColor());
        drawableNormal.setColor(style.getFillColor());
        GradientDrawable drawablePressed = new GradientDrawable();
        drawablePressed.setCornerRadius(ConvertUtils.dip2px(getContext(), style.getCornerRadiusPressed() / 10f));
        drawablePressed.setStroke(ConvertUtils.dip2px(getContext(), style.getStrokeWidthPressed() / 10f), style.getStrokeColorPressed());
        drawablePressed.setColor(style.getFillColorPressed());
        viewHolder.button.setGravity(Gravity.CENTER);
        viewHolder.button.setPadding(0, 0, 0, 0);
        viewHolder.button.setText("S");
        viewHolder.button.setAllCaps(false);
        viewHolder.button.setTextSize(style.getTextSize());
        viewHolder.button.setTextColor(style.getTextColor());
        viewHolder.button.setBackground(drawableNormal);
        viewHolder.button.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                ((AppCompatButton) view1).setTextSize(style.getTextSizePressed());
                ((AppCompatButton) view1).setTextColor(style.getTextColorPressed());
                ((AppCompatButton) view1).setBackground(drawablePressed);
            }
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP || motionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                ((AppCompatButton) view1).setTextSize(style.getTextSize());
                ((AppCompatButton) view1).setTextColor(style.getTextColor());
                ((AppCompatButton) view1).setBackground(drawableNormal);
            }
            return true;
        });
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
                ButtonStyles.removeStyles(style);
                ButtonStyles.checkStyles();
                notifyDataSetChanged();
            });
            builder.setNegativeButton(null);
            builder.create().show();
        });
        return view;
    }

    static class ViewHolder {
        AppCompatButton button;
        H2CO3LauncherTextView name;
        H2CO3LauncherRadioButton radioButton;
        H2CO3LauncherImageButton delete;
    }
}
