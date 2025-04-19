package org.koishi.launcher.h2co3.control.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import org.koishi.launcher.h2co3.R;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView;

public class LogWindow extends ScrollView {

    private boolean autoTint;
    private BooleanProperty visibilityProperty;
    private H2CO3LauncherTextView textView;
    private int lineCount;

    public LogWindow(Context context) {
        super(context);
        autoTint = false;
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, org.koishi.launcher.h2co3library.R.styleable.LogWindow);
        autoTint = typedArray.getBoolean(org.koishi.launcher.h2co3library.R.styleable.LogWindow_auto_log_tint, false);
        typedArray.recycle();
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, org.koishi.launcher.h2co3library.R.styleable.LogWindow);
        autoTint = typedArray.getBoolean(org.koishi.launcher.h2co3library.R.styleable.LogWindow_auto_log_tint, false);
        typedArray.recycle();
        init(context);
    }

    public LogWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, org.koishi.launcher.h2co3library.R.styleable.LogWindow);
        autoTint = typedArray.getBoolean(org.koishi.launcher.h2co3library.R.styleable.LogWindow_auto_log_tint, false);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        this.textView = new H2CO3LauncherTextView(context);
        textView.setAutoTint(autoTint);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(textView);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(15);
        textView.setLineSpacing(0, 1f);
        textView.setEllipsize(null);
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

                @Override
                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        boolean visible = get();
                        setVisibility(visible ? VISIBLE : GONE);
                        if (!visible) {
                            cleanLog();
                        }
                    });
                }

                @Override
                public Object getBean() {
                    return this;
                }

                @Override
                public String getName() {
                    return "visibility";
                }
            };
        }

        return visibilityProperty;
    }

    public void appendLog(String str) {
        if (!getVisibilityValue()) {
            return;
        }
        lineCount++;
        this.post(() -> {
            if (textView != null) {
                if (lineCount < 100) {
                    textView.append(str);
                } else {
                    cleanLog();
                }
                fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void cleanLog() {
        this.textView.setText("");
        lineCount = 0;
    }
}
