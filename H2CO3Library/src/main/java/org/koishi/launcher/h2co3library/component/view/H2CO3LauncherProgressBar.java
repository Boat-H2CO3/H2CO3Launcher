package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.DoubleProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.DoublePropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;


public class H2CO3LauncherProgressBar extends ProgressBar {

    private DoubleProperty progress;
    private IntegerProperty firstProgressProperty;
    private IntegerProperty secondProgressProperty;
    private BooleanProperty visibilityProperty;
    private BooleanProperty disableProperty;

    public H2CO3LauncherProgressBar(Context context) {
        super(context);
        
    }

    public H2CO3LauncherProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }

    public H2CO3LauncherProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
    }

    public H2CO3LauncherProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        
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
                            setProgress((int) (progress * getMax()));
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

    public final IntegerProperty firstProgressProperty() {
        if (firstProgressProperty == null) {
            firstProgressProperty = new IntegerPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        int progress = get();
                        if (progress >= 0) {
                            setProgress(Math.min(progress, getMax()));
                        }
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "firstProgress";
                }
            };
        }

        return firstProgressProperty;
    }

    public final IntegerProperty secondProgressProperty() {
        if (secondProgressProperty == null) {
            secondProgressProperty = new IntegerPropertyBase() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        int progress = get();
                        if (progress >= 0) {
                            setSecondaryProgress(Math.min(progress, getMax()));
                        }
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "secondProgress";
                }
            };
        }

        return secondProgressProperty;
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
