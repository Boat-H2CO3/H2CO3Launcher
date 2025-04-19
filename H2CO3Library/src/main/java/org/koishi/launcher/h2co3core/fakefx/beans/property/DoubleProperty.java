package org.koishi.launcher.h2co3core.fakefx.beans.property;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WritableDoubleValue;
import org.koishi.launcher.h2co3core.fakefx.binding.BidirectionalBinding;

import java.util.Objects;

public abstract class DoubleProperty extends ReadOnlyDoubleProperty implements
        Property<Number>, WritableDoubleValue {

    /**
     * Creates a default {@code DoubleProperty}.
     */
    public DoubleProperty() {
    }

    public static DoubleProperty doubleProperty(final Property<Double> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new DoublePropertyBase() {
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
            set(0.0);
        } else {
            set(v.doubleValue());
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
     * Returns a string representation of this {@code DoubleProperty} object.
     * @return a string representation of this {@code DoubleProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "DoubleProperty [");
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
    public ObjectProperty<Double> asObject() {
        return new ObjectPropertyBase<Double> () {
            {
                BidirectionalBinding.bindNumber(this, DoubleProperty.this);
            }

            @Override
            public Object getBean() {
                return null; // Virtual property, does not exist on a bean
            }

            @Override
            public String getName() {
                return DoubleProperty.this.getName();
            }
        };
    }
}
