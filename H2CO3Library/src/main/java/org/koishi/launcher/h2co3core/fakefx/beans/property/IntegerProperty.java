package org.koishi.launcher.h2co3core.fakefx.beans.property;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WritableIntegerValue;
import org.koishi.launcher.h2co3core.fakefx.binding.BidirectionalBinding;

import java.util.Objects;

public abstract class IntegerProperty extends ReadOnlyIntegerProperty implements
        Property<Number>, WritableIntegerValue {

    /**
     * Creates a default {@code IntegerProperty}.
     */
    public IntegerProperty() {
    }

    public static IntegerProperty integerProperty(final Property<Integer> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new IntegerPropertyBase() {
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
            set(0);
        } else {
            set(v.intValue());
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
     * Returns a string representation of this {@code IntegerProperty} object.
     * @return a string representation of this {@code IntegerProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "IntegerProperty [");
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
    public ObjectProperty<Integer> asObject() {
        return new ObjectPropertyBase<Integer> () {
            {
                BidirectionalBinding.bindNumber(this, IntegerProperty.this);
            }

            @Override
            public Object getBean() {
                return null; // Virtual property, does not exist on a bean
            }

            @Override
            public String getName() {
                return IntegerProperty.this.getName();
            }
        };
    }
}
