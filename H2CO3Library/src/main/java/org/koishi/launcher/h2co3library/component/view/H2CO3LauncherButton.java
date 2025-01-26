package org.koishi.launcher.h2co3library.component.view;

import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.button.MaterialButton;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.R;
import org.koishi.launcher.h2co3library.util.ConvertUtils;

public class H2CO3LauncherButton extends MaterialButton {

    private BooleanProperty visibilityProperty;
    private BooleanProperty disableProperty;
    private ObjectProperty<Drawable> image;

    public H2CO3LauncherButton(@NonNull Context context) {
        super(context);
        init();
    }

    public H2CO3LauncherButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherButton);
        init();
        typedArray.recycle();
    }

    public H2CO3LauncherButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherButton);
        init();
        typedArray.recycle();
    }

    public final Drawable getImage() {
        return image == null ? null : image.get();
    }

    public final void setImage(Drawable drawable) {
        imageProperty().set(drawable);
    }

    public final ObjectProperty<Drawable> imageProperty() {
        if (image == null) {
            image = new ObjectPropertyBase<>() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        Drawable drawable = get();
                        setIcon(drawable);
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

    private void init() {
        setMaxLines(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public int getShape() {
        return ((GradientDrawable) getBackground()).getShape();
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
