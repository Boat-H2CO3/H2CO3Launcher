package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.R;

public class H2CO3LauncherImageView extends AppCompatImageView {

    private ObjectProperty<Drawable> image;
    private boolean autoTint;
    private boolean useThemeColor;
    private final IntegerProperty theme = new IntegerPropertyBase() {

        @Override
        protected void invalidated() {
            get();
            if (autoTint) {
                int[][] state = {
                        {

                        }
                };
                int[] color = {
                };
                setImageTintList(new ColorStateList(state, color));
            }
            if (useThemeColor && getBackground() != null) {
            }
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "theme";
        }
    };
    private final IntegerProperty theme2 = new IntegerPropertyBase() {

        @Override
        protected void invalidated() {
            get();
            if (useThemeColor && getBackground() != null) {
            }
        }

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "theme2";
        }
    };
    private BooleanProperty visibilityProperty;

    public H2CO3LauncherImageView(@NonNull Context context) {
        super(context);
    }

    public H2CO3LauncherImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherImageView);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherImageView_auto_src_tint, false);
        useThemeColor = typedArray.getBoolean(R.styleable.H2CO3LauncherImageView_use_theme_color, false);
        typedArray.recycle();
    }

    public H2CO3LauncherImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherImageView);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherImageView_auto_src_tint, false);
        useThemeColor = typedArray.getBoolean(R.styleable.H2CO3LauncherImageView_use_theme_color, false);
        typedArray.recycle();
    }

    public boolean isAutoTint() {
        return autoTint;
    }

    public void setAutoTint(boolean autoTint) {
        this.autoTint = autoTint;
    }

    public boolean isUseThemeColor() {
        return useThemeColor;
    }

    public void setUseThemeColor(boolean useThemeColor) {
        this.useThemeColor = useThemeColor;
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
                        setBackground(drawable);
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
}
