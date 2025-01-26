package org.koishi.launcher.h2co3library.component.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanPropertyBase;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectPropertyBase;
import org.koishi.launcher.h2co3core.task.Schedulers;

import java.util.ArrayList;

public class H2CO3LauncherSpinner<T> extends AppCompatSpinner {

    private boolean fromUserOrSystem = false;
    private ArrayList<T> dataList;
    private ObjectProperty<T> selectedItemProperty;
    private BooleanProperty visibilityProperty;

    public H2CO3LauncherSpinner(@NonNull Context context) {
        super(context);
    }

    public H2CO3LauncherSpinner(@NonNull Context context, int mode) {
        super(context, mode);
    }

    public H2CO3LauncherSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public H2CO3LauncherSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public H2CO3LauncherSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public H2CO3LauncherSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    public void addSelectListener() {
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (dataList != null && dataList.size() > i) {
                    fromUserOrSystem = true;
                    selectedItemProperty().set(dataList.get(i));
                    fromUserOrSystem = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public ArrayList<T> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<T> dataList) {
        this.dataList = dataList;
    }

    public final Object getSelectedItemValue() {
        return selectedItemProperty == null ? null : selectedItemProperty.get();
    }

    public final ObjectProperty<T> selectedItemProperty() {
        if (selectedItemProperty == null) {
            selectedItemProperty = new ObjectPropertyBase<T>() {

                public void invalidated() {
                    Schedulers.androidUIThread().execute(() -> {
                        if (!fromUserOrSystem) {
                            T data = get();
                            setSelection(dataList.indexOf(data));
                        }
                    });
                }

                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "selectedItem";
                }
            };
        }

        return selectedItemProperty;
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
