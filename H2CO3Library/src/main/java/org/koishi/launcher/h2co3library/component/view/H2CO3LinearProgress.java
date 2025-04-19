package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3core.fakefx.beans.property.DoubleProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.DoublePropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;

public class H2CO3LinearProgress extends LinearProgressIndicator {

    private DoubleProperty progress;

    public H2CO3LinearProgress(@NonNull @NotNull Context context) {
        super(context);
    }

    public H2CO3LinearProgress(@NonNull @NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public H2CO3LinearProgress(@NonNull @NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final DoubleProperty percentProgressProperty() {
        if (progress == null) {
            progress = new DoublePropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        // progress should >= 0, <= 1
                        double progress = get();
                        setIndeterminate(progress < 0.0);
                        if (progress >= 0.0) {
                            setProgressCompat((int) (progress * getMax()), true);
                        }
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "percentProgress";
                }
            };
        }

        return progress;
    }
}
