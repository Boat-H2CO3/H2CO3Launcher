package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;
import org.koishi.launcher.h2co3library.R;

import java.util.regex.Pattern;

public class H2CO3LauncherEditText extends com.google.android.material.textfield.TextInputEditText {

    public boolean fromUserOrSystem = false;
    private boolean autoTint;
    private final IntegerProperty theme = new IntegerPropertyBase() {

        @Override
        protected void invalidated() {
            get();
            int[][] state = {
                    {
                            android.R.attr.state_focused
                    },
                    {

                    }
            };
            int[] color = {
                    Color.GRAY
            };
            setBackgroundTintList(new ColorStateList(state, color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            }
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
    private BooleanProperty visibilityProperty;
    private BooleanProperty disableProperty;
    private BooleanProperty focusedProperty;
    private final Thread focusListener = new Thread(() -> {
        if (focusedProperty == null) {
            focusedProperty = new BooleanPropertyBase() {

                public void invalidated() {

                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "focused";
                }
            };
        }
        while (true) {
            focusedProperty.set(isFocused());
        }
    });
    private StringProperty stringProperty;

    public H2CO3LauncherEditText(@NonNull Context context) {
        super(context);
        autoTint = false;
        addTextWatcher();
    }

    public H2CO3LauncherEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherEditText);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherEditText_auto_edit_tint, false);
        typedArray.recycle();
        addTextWatcher();
    }

    public H2CO3LauncherEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.H2CO3LauncherEditText);
        autoTint = typedArray.getBoolean(R.styleable.H2CO3LauncherEditText_auto_edit_tint, false);
        typedArray.recycle();
        addTextWatcher();
    }

    public void runFocusListener() {
        Schedulers.androidUIThread().execute(() -> {
            focusListener.setPriority(Thread.MIN_PRIORITY);
            focusListener.start();
        });
    }

    public void addTextWatcher() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                fromUserOrSystem = true;
                stringProperty().set(getText().toString());
                fromUserOrSystem = false;
            }
        });
    }

    public void setIntegerFilter(int min) {
        setFilters(new InputFilter[]{
                new SignedIntegerFilter(min)
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

    public final boolean getFocusedValue() {
        return focusedProperty == null || focusedProperty.get();
    }

    public final ReadOnlyBooleanProperty focusedProperty() {
        if (focusedProperty == null) {
            focusedProperty = new BooleanPropertyBase() {

                public void invalidated() {

                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "focused";
                }
            };
        }

        return focusedProperty;
    }

    public final String getStringValue() {
        return stringProperty == null ? null : stringProperty.get();
    }

    public final void setStringValue(String string) {
        stringProperty().set(string);
    }

    public final StringProperty stringProperty() {
        if (stringProperty == null) {
            stringProperty = new StringPropertyBase() {

                public void invalidated() {

                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "string";
                }
            };
        }

        return stringProperty;
    }

    private static final class SignedIntegerFilter implements InputFilter {
        private final Pattern pattern;

        SignedIntegerFilter(int min) {
            pattern = Pattern.compile("^" + (min < 0 ? "-?" : "") + "[0-9]*$");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder builder = new StringBuilder(dest);
            builder.insert(dstart, source);
            if (!pattern.matcher(builder.toString()).matches()) {
                return "";
            }
            return source;
        }
    }
}
