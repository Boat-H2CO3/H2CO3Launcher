package org.koishi.launcher.h2co3core.fakefx.beans.property;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WritableLongValue;
import org.koishi.launcher.h2co3core.fakefx.binding.BidirectionalBinding;

import java.util.Objects;

public abstract class LongProperty extends ReadOnlyLongProperty implements
        Property<Number>, WritableLongValue {

    /**
     * Creates a default {@code LongProperty}.
     */
    public LongProperty() {
    }

    public static LongProperty longProperty(final Property<Long> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return new LongPropertyBase() {
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
            set(0L);
        } else {
            set(v.longValue());
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
     * Returns a string representation of this {@code LongProperty} object.
     * @return a string representation of this {@code LongProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder("LongProperty [");
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
    public ObjectProperty<Long> asObject() {
        return new ObjectPropertyBase<Long> () {
            {
                BidirectionalBinding.bindNumber(this, LongProperty.this);
            }

            @Override
            public Object getBean() {
                return null; // Virtual property, does not exist on a bean
            }

            @Override
            public String getName() {
                return LongProperty.this.getName();
            }
        };
    }
}
