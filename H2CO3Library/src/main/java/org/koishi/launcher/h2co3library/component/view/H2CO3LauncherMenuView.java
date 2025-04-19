package org.koishi.launcher.h2co3library.component.view;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

public class H2CO3LauncherMenuView extends AppCompatImageButton {

    private boolean isSelected;
    private OnSelectListener onSelectListener;

    public H2CO3LauncherMenuView(@NonNull Context context) {
        super(context);
        init();

    }

    public H2CO3LauncherMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public H2CO3LauncherMenuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        setPadding(
                ConvertUtils.dip2px(getContext(), 8f),
                ConvertUtils.dip2px(getContext(), 8f),
                ConvertUtils.dip2px(getContext(), 8f),
                ConvertUtils.dip2px(getContext(), 8f)
        );
        setScaleType(ScaleType.FIT_XY);
        setOnClickListener(view -> {
            if (!isSelected) {
                setSelected(true);
            }
        });
        setStateListAnimator(AnimatorInflater.loadStateListAnimator(getContext(), R.xml.anim_scale_large));
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        final boolean oldSelect = isSelected;
        isSelected = selected;
        if (!oldSelect && selected && onSelectListener != null) {
            onSelectListener.onSelect(this);
        }
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {
        void onSelect(H2CO3LauncherMenuView view);
    }
}
