package org.koishi.launcher.h2co3core.fakefx.beans.property;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WritableObjectValue;

public abstract class ObjectProperty<T> extends ReadOnlyObjectProperty<T>
        implements Property<T>, WritableObjectValue<T> {

    /**
     * Creates a default {@code ObjectProperty}.
     */
    public ObjectProperty() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(T v) {
        set(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindBidirectional(Property<T> other) {
        Bindings.bindBidirectional(this, other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindBidirectional(Property<T> other) {
        Bindings.unbindBidirectional(this, other);
    }

    /**
     * Returns a string representation of this {@code ObjectProperty} object.
     * @return a string representation of this {@code ObjectProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "ObjectProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if ((name != null) && (!name.equals(""))) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }
}
