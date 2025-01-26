package org.koishi.launcher.h2co3core.util.gson.fakefx.properties;

import com.google.gson.TypeAdapter;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ListProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleListProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;

import org.jetbrains.annotations.NotNull;

/**
 * A basic {@link TypeAdapter} for JavaFX {@link ListProperty}. It serializes the list inside the property instead of
 * the property itself.
 */
public class ListPropertyTypeAdapter<T> extends PropertyTypeAdapter<ObservableList<T>, ListProperty<T>> {

    public ListPropertyTypeAdapter(TypeAdapter<ObservableList<T>> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    @NotNull
    @Override
    protected ListProperty<T> createProperty(ObservableList<T> deserializedValue) {
        return new SimpleListProperty<>(deserializedValue);
    }
}