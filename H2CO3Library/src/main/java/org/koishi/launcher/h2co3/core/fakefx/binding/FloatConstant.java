package org.koishi.launcher.h2co3.core.fakefx.binding;

import org.koishi.launcher.h2co3.core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.value.ChangeListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.value.ObservableFloatValue;

/**
 * A simple FloatExpression that represents a single constant value.
 */
public final class FloatConstant implements ObservableFloatValue {

    private final float value;

    private FloatConstant(float value) {
        this.value = value;
    }

    public static FloatConstant valueOf(float value) {
        return new FloatConstant(value);
    }

    @Override
    public float get() {
        return value;
    }

    @Override
    public Float getValue() {
        return value;
    }

    @Override
    public void addListener(InvalidationListener observer) {
        // no-op
    }

    @Override
    public void addListener(ChangeListener<? super Number> listener) {
        // no-op
    }

    @Override
    public void removeListener(InvalidationListener observer) {
        // no-op
    }

    @Override
    public void removeListener(ChangeListener<? super Number> listener) {
        // no-op
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
}
