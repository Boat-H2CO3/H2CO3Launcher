package org.koishi.launcher.h2co3core.fakefx.beans.property;

import org.koishi.launcher.h2co3core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WritableBooleanValue;
import org.koishi.launcher.h2co3core.fakefx.binding.BidirectionalBinding;

import java.util.Objects;

public abstract class BooleanProperty extends ReadOnlyBooleanProperty implements
        Property<Boolean>, WritableBooleanValue {

    /**
     * Sole constructor
     */
    public BooleanProperty() {
    }

    public static BooleanProperty booleanProperty(final Property<Boolean> property) {
        Objects.requireNonNull(property, "Property cannot be null");
        return property instanceof BooleanProperty ? (BooleanProperty)property : new BooleanPropertyBase() {
            {
                BidirectionalBinding.bind(this, property);
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
    public void setValue(Boolean v) {
        if (v == null) {
            set(false);
        } else {
            set(v.booleanValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindBidirectional(Property<Boolean> other) {
        Bindings.bindBidirectional(this, other);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindBidirectional(Property<Boolean> other) {
        Bindings.unbindBidirectional(this, other);
    }

    /**
     * Returns a string representation of this {@code BooleanProperty} object.
     * @return a string representation of this {@code BooleanProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "BooleanProperty [");
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
    public ObjectProperty<Boolean> asObject() {
        return new ObjectPropertyBase<Boolean> () {
            {
                BidirectionalBinding.bind(this, BooleanProperty.this);
            }

            @Override
            public Object getBean() {
                return null; // Virtual property, does not exist on a bean
            }

            @Override
            public String getName() {
                return BooleanProperty.this.getName();
            }
        };
    }
}
