package org.koishi.launcher.h2co3core.util.gson.fakefx.properties.primitives;

import com.google.gson.TypeAdapter;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty;

/**
 * An implementation of {@link PrimitivePropertyTypeAdapter} for JavaFX {@link IntegerProperty}. It serializes the int
 * value of the property instead of the property itself.
 */
public class IntegerPropertyTypeAdapter extends PrimitivePropertyTypeAdapter<Integer, IntegerProperty> {

    public IntegerPropertyTypeAdapter(TypeAdapter<Integer> delegate, boolean throwOnNullProperty,
                                      boolean crashOnNullValue) {
        super(delegate, throwOnNullProperty, crashOnNullValue);
    }

    @Override
    protected Integer extractPrimitiveValue(IntegerProperty property) {
        return property.getValue();
    }

    @Override
    protected IntegerProperty createDefaultProperty() {
        return new SimpleIntegerProperty();
    }

    @Override
    protected IntegerProperty wrapNonNullPrimitiveValue(Integer deserializedValue) {
        return new SimpleIntegerProperty(deserializedValue);
    }
}
