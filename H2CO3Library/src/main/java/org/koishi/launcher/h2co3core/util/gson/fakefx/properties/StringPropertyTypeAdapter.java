package org.koishi.launcher.h2co3core.util.gson.fakefx.properties;

import com.google.gson.TypeAdapter;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;

import org.jetbrains.annotations.NotNull;

/**
 * A basic {@link TypeAdapter} for JavaFX {@link StringProperty}. It serializes the string inside the property instead
 * of the property itself.
 */
public class StringPropertyTypeAdapter extends PropertyTypeAdapter<String, StringProperty> {

    public StringPropertyTypeAdapter(TypeAdapter<String> delegate, boolean throwOnNullProperty) {
        super(delegate, throwOnNullProperty);
    }

    @NotNull
    @Override
    protected StringProperty createProperty(String deserializedValue) {
        return new SimpleStringProperty(deserializedValue);
    }
}