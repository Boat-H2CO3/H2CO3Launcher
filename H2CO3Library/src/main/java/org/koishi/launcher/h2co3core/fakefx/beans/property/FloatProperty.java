package org.koishi.launcher.h2co3core.fakefx.beans.property;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WritableFloatValue;
import org.koishi.launcher.h2co3core.fakefx.binding.BidirectionalBinding;

import java.util.Objects;

public abstract class FloatProperty extends ReadOnlyFloatProperty implements
        Property<Number>, WritableFloatValue {

    /**
     * Creates a default {@code FloatProperty}.
     */
    public FloatProperty() {
    }

    public static FloatProperty floatProperty(final Property<Float> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new FloatPropertyBase() {
            {
                BidirectionalBinding.bindNumber(this, property);
            }

            @Override
            public Object getBean() {
                return null; // Virtual property, no bean
            }

            @Override
            public String getName() {
                return property.getName();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Number v) {
        if (v == null) {
            set(0.0f);
        } else {
            set(v.floatValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindBidirectional(Property<Number> other) {
        Bindings.bindBidirectional(this, other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindBidirectional(Property<Number> other) {
        Bindings.unbindBidirectional(this, other);
    }

    /**
     * Returns a string representation of this {@code FloatProperty} object.
     * @return a string representation of this {@code FloatProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "FloatProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if ((name != null) && (!name.equals(""))) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

    @Override
    public ObjectProperty<Float> asObject() {
        return new ObjectPropertyBase<Float> () {
            {
                BidirectionalBinding.bindNumber(this, FloatProperty.this);
            }

            @Override
            public Object getBean() {
                return null; // Virtual property, does not exist on a bean
            }

            @Override
            public String getName() {
                return FloatProperty.this.getName();
            }
        };
    }
}
