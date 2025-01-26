package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textview.MaterialTextView;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.R;

public class H2CO3LauncherTextView extends MaterialTextView {

    private final IntegerProperty theme = createThemeProperty("theme");
    private final IntegerProperty theme2 = createThemeProperty("theme2");
    private boolean autoTint;
    private boolean autoBackgroundTint;
    private boolean useThemeColor;
    private StringProperty string;
    private BooleanProperty visibilityProperty;

    public H2CO3LauncherTextView(@NonNull Context context) {
        super(context);
        init(false, false, false);
    }

    public H2CO3LauncherTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initFromAttributes(context, attrs);
    }

    public H2CO3LauncherTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFromAttributes(context, attrs);
    }

    private void init(boolean autoTint, boolean autoBackgroundTint, boolean useThemeColor) {
        this.autoTint = autoTint;
        this.autoBackgroundTint = autoBackgroundTint;
        this.useThemeColor = useThemeColor;
    }

    private void initFromAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherTextView);
        init(
                typedArray.getBoolean(R.styleable.H2CO3LauncherTextView_auto_text_tint, false),
                typedArray.getBoolean(R.styleable.H2CO3LauncherTextView_auto_text_background_tint, false),
                typedArray.getBoolean(R.styleable.H2CO3LauncherTextView_use_theme_color, false)
        );
        typedArray.recycle();
    }

    private IntegerProperty createThemeProperty(String name) {
        return new IntegerPropertyBase() {
            @Override
            public Object getBean() {
                return this;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            protected void invalidated() {
                get();
            }
        };
    }

    public void alert() {
        setTextColor(Color.RED);
    }

    public void normal() {
        setTextColor(Color.GRAY);
    }

    public void emphasize() {
        setTextColor(Color.BLACK);
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

    public boolean isAutoBackgroundTint() {
        return autoBackgroundTint;
    }

    public void setAutoBackgroundTint(boolean autoBackgroundTint) {
        this.autoBackgroundTint = autoBackgroundTint;
    }

    public final String getString() {
        return string != null ? string.get() : null;
    }

    public final void setString(String string) {
        stringProperty().set(string);
    }

    public final StringProperty stringProperty() {
        if (string == null) {
            string = new StringPropertyBase() {
                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> setText(get()));
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "string";
                }
            };
        }
        return string;
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
                    Schedulers.androidUIThread().execute(() -> setVisibility(get() ? VISIBLE : GONE));
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