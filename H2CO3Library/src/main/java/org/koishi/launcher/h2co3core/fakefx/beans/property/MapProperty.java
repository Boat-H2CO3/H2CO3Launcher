package org.koishi.launcher.h2co3core.fakefx.beans.property;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WritableMapValue;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableMap;

public abstract class MapProperty<K, V> extends ReadOnlyMapProperty<K, V> implements
        Property<ObservableMap<K, V>>, WritableMapValue<K, V> {

    /**
     * Creates a default {@code MapProperty}.
     */
    public MapProperty() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(ObservableMap<K, V> v) {
        set(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindBidirectional(Property<ObservableMap<K, V>> other) {
        Bindings.bindBidirectional(this, other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindBidirectional(Property<ObservableMap<K, V>> other) {
        Bindings.unbindBidirectional(this, other);
    }

    /**
     * Returns a string representation of this {@code MapProperty} object.
     * @return a string representation of this {@code MapProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "MapProperty [");
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
