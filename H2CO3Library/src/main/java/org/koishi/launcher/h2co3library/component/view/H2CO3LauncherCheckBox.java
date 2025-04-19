package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.R;

public class H2CO3LauncherCheckBox extends AppCompatCheckBox {

    private boolean autoTint;
    private final IntegerProperty theme = new IntegerPropertyBase() {

        @Override
        protected void invalidated() {
            get();
            int[][] state = {
                    {
                            android.R.attr.state_checked
                    },
                    {

                    }
            };
            int[] color = {
                    Color.GRAY
            };
            setButtonTintList(new ColorStateList(state, color));
            if (autoTint) {
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
    private boolean fromUserOrSystem = false;
    private boolean fromIndeterminate = false;
    private BooleanProperty visibilityProperty;
    private BooleanProperty checkProperty;
    private BooleanProperty indeterminateProperty;
    private BooleanProperty disableProperty;

    public H2CO3LauncherCheckBox(@NonNull Context context) {
        super(context);

    }

    public H2CO3LauncherCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherCheckBox);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherCheckBox_auto_hint_tint, false);
        typedArray.recycle();

    }

    public H2CO3LauncherCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherCheckBox);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherCheckBox_auto_hint_tint, false);
        typedArray.recycle();

    }

    public void addCheckedChangeListener() {
        setOnCheckedChangeListener((compoundButton, b) -> {
            if (!fromIndeterminate) {
                fromUserOrSystem = true;
                checkProperty().set(b);
                indeterminateProperty().set(false);
                fromUserOrSystem = false;
            }
        });
    }

    public boolean isAutoTint() {
        return autoTint;
    }

    public void setAutoTint(boolean autoTint) {
        this.autoTint = autoTint;
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

    public final boolean getCheckValue() {
        return checkProperty == null || checkProperty.get();
    }

    public final void setCheckValue(boolean isChecked) {
        checkProperty().set(isChecked);
    }

    public final BooleanProperty checkProperty() {
        if (checkProperty == null) {
            checkProperty = new BooleanPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        if (!fromUserOrSystem) {
                            boolean isCheck = get();
                            setChecked(isCheck);
                        }
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "check";
                }
            };
        }

        return checkProperty;
    }

    public final boolean isIndeterminate() {
        return indeterminateProperty().get();
    }

    public final void setIndeterminate(boolean indeterminate) {
        checkProperty().set(indeterminate);
    }

    public final BooleanProperty indeterminateProperty() {
        if (indeterminateProperty == null) {
            indeterminateProperty = new BooleanPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        if (!fromUserOrSystem) {
                            fromIndeterminate = true;
                            if (get()) {
                                setChecked(true);
                            } else {
                                setChecked(checkProperty().get());
                            }
                            fromIndeterminate = false;
                        }
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "indeterminate";
                }
            };
        }

        return indeterminateProperty;
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
