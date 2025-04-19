package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

public class H2CO3LauncherImageButton extends AppCompatImageButton {

    private ObjectProperty<Drawable> image;
    private boolean autoTint;
    private boolean noPadding;
    private boolean useThemeColor;
    private BooleanProperty visibilityProperty;
    private BooleanProperty disableProperty;

    public H2CO3LauncherImageButton(@NonNull Context context) {
        super(context);
        init();
    }

    public H2CO3LauncherImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherImageButton);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherImageButton_auto_tint, false);
        noPadding = typedArray.getBoolean(R.styleable.H2CO3LauncherImageButton_no_padding, false);
        useThemeColor = typedArray.getBoolean(R.styleable.H2CO3LauncherImageButton_use_theme_color, false);
        typedArray.recycle();
        init();
    }

    public H2CO3LauncherImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherImageButton);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherImageButton_auto_tint, false);
        noPadding = typedArray.getBoolean(R.styleable.H2CO3LauncherImageButton_no_padding, false);
        useThemeColor = typedArray.getBoolean(R.styleable.H2CO3LauncherImageButton_use_theme_color, false);
        typedArray.recycle();
        init();
    }

    public void refreshStyle() {
    }

    private void init() {
        if (!noPadding) {
            setPadding(
                    ConvertUtils.dip2px(getContext(), 8f),
                    ConvertUtils.dip2px(getContext(), 8f),
                    ConvertUtils.dip2px(getContext(), 8f),
                    ConvertUtils.dip2px(getContext(), 8f)
            );
        } else {
            setPadding(0, 0, 0, 0);
        }
        setScaleType(ScaleType.FIT_XY);
    }

    public boolean isAutoTint() {
        return autoTint;
    }

    public void setAutoTint(boolean autoTint) {
        this.autoTint = autoTint;
        refreshStyle();
    }

    public boolean isNoPadding() {
        return noPadding;
    }

    public void setNoPadding(boolean noPadding) {
        this.noPadding = noPadding;
        refreshStyle();
    }

    public boolean isUseThemeColor() {
        return useThemeColor;
    }

    public void setUseThemeColor(boolean useThemeColor) {
        this.useThemeColor = useThemeColor;
        refreshStyle();
    }

    public final Drawable getImage() {
        return image == null ? null : image.get();
    }

    public final void setImage(Drawable drawable) {
        imageProperty().set(drawable);
    }

    public final ObjectProperty<Drawable> imageProperty() {
        if (image == null) {
            image = new ObjectPropertyBase<Drawable>() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        Drawable drawable = get();
                        setImageDrawable(drawable);
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "image";
                }
            };
        }

        return this.image;
    }

    public final boolean getVisibilityValue() {
        return visibilityProperty == null || visibilityProperty.get();
    }

    public final void setVisibilityValue(boolean visibility) {
        visibilityProperty().set(visibility);
    }

    public final BooleanProperty visibilityProperty() {
        if (visibilityProperty == null) {
            visibilityProperty = new BooleanPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        boolean visible = get();
                        setVisibility(visible ? VISIBLE : GONE);
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "visibility";
                }
            };
        }

        return visibilityProperty;
    }

    public final boolean getDisableValue() {
        return disableProperty == null || disableProperty.get();
    }

    public final void setDisableValue(boolean disableValue) {
        disableProperty().set(disableValue);
    }

    public final BooleanProperty disableProperty() {
        if (disableProperty == null) {
            disableProperty = new BooleanPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        boolean disable = get();
                        setEnabled(!disable);
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "disable";
                }
            };
        }

        return disableProperty;
    }
}
