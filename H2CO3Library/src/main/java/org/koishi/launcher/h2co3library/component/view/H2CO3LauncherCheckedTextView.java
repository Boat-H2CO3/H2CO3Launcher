package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3library.R;

public class H2CO3LauncherCheckedTextView extends androidx.appcompat.widget.AppCompatCheckedTextView {

    private boolean autoTint;
    private boolean autoBackgroundTint;

    private final IntegerProperty theme = new IntegerPropertyBase() {

        @Override
        protected void invalidated() {
            get();
            if (autoTint) {
            }
            if (autoBackgroundTint) {
                setBackgroundTintList(new ColorStateList(new int[][] { { } }, new int[]{ }));
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

    public H2CO3LauncherCheckedTextView(@NonNull Context context) {
        super(context);
        autoTint = false;
        autoBackgroundTint = false;
    }

    public H2CO3LauncherCheckedTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherCheckedTextView);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherCheckedTextView_auto_checked_text_tint, false);
        autoBackgroundTint = typedArray.getBoolean(R.styleable.H2CO3LauncherCheckedTextView_auto_checked_text_background_tint, false);
        typedArray.recycle();
    }

    public H2CO3LauncherCheckedTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherCheckedTextView);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherCheckedTextView_auto_checked_text_tint, false);
        autoBackgroundTint = typedArray.getBoolean(R.styleable.H2CO3LauncherCheckedTextView_auto_checked_text_background_tint, false);
        typedArray.recycle();
    }

    public boolean isAutoTint() {
        return autoTint;
    }

    public void setAutoTint(boolean autoTint) {
        this.autoTint = autoTint;
    }

    public boolean isAutoBackgroundTint() {
        return autoBackgroundTint;
    }

    public void setAutoBackgroundTint(boolean autoBackgroundTint) {
        this.autoBackgroundTint = autoBackgroundTint;
    }
}
