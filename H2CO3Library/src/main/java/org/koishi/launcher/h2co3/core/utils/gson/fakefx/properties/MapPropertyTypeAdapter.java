package org.koishi.launcher.h2co3.core.utils.gson.fakefx.properties;

import com.google.gson.TypeAdapter;

import org.jetbrains.annotations.NotNull;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.MapProperty;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.SimpleMapProperty;
import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableMap;

/**
 * A basic {@link TypeAdapter} for JavaFX {@link MapProperty}. It serializes the map inside the property instead of the
 * property itself.
 */
public class MapPropertyTypeAdapter<K, V> extends PropertyTypeAdapter<ObservableMap<K, V>, MapProperty<K, V>> {

    public MapPropertyTypeAdapter(TypeAdapter<ObservableMap<K, V>> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    @NotNull
    @Override
    protected MapProperty<K, V> createProperty(ObservableMap<K, V> deserializedValue) {
        return new SimpleMapProperty<>(deserializedValue);
    }
}