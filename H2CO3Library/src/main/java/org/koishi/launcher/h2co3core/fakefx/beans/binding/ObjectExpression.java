package org.koishi.launcher.h2co3core.fakefx.beans.binding;

import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableObjectValue;
import org.koishi.launcher.h2co3core.fakefx.binding.StringFormatter;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;

import java.util.Locale;

public abstract class ObjectExpression<T> implements ObservableObjectValue<T> {

    /**
     * Creates a default {@code ObjectExpression}.
     */
    public ObjectExpression() {
    }

    public static <T> ObjectExpression<T> objectExpression(
            final ObservableObjectValue<T> value) {
        if (value == null) {
            throw new NullPointerException("Value must be specified.");
        }
        return value instanceof ObjectExpression ? (ObjectExpression<T>) value
                : new ObjectBinding<T>() {
                    {
                        super.bind(value);
                    }

                    @Override
                    public void dispose() {
                        super.unbind(value);
                    }

                    @Override
                    protected T computeValue() {
                        return value.get();
                    }

                    @Override
                    public ObservableList<ObservableObjectValue<T>> getDependencies() {
                        return FXCollections.singletonObservableList(value);
                    }
                };
    }

    @Override
    public T getValue() {
        return get();
    }

    public BooleanBinding isEqualTo(final ObservableObjectValue<?> other) {
        return Bindings.equal(this, other);
    }

    /**
     * Creates a new {@code BooleanExpression} that holds {@code true} if this
     * {@code ObjectExpression} is equal to a constant value.
     *
     * @param other
     *            the constant value
     * @return the new {@code BooleanExpression}
     */
    public BooleanBinding isEqualTo(final Object other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isNotEqualTo(final ObservableObjectValue<?> other) {
        return Bindings.notEqual(this, other);
    }

    /**
     * Creates a new {@code BooleanExpression} that holds {@code true} if this
     * {@code ObjectExpression} is not equal to a constant value.
     *
     * @param other
     *            the constant value
     * @return the new {@code BooleanExpression}
     */
    public BooleanBinding isNotEqualTo(final Object other) {
        return Bindings.notEqual(this, other);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if this
     * {@code ObjectExpression} is {@code null}.
     *
     * @return the new {@code BooleanBinding}
     */
    public BooleanBinding isNull() {
        return Bindings.isNull(this);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if this
     * {@code ObjectExpression} is not {@code null}.
     *
     * @return the new {@code BooleanBinding}
     */
    public BooleanBinding isNotNull() {
        return Bindings.isNotNull(this);
    }

    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    public StringBinding asString(String format) {
        return (StringBinding) Bindings.format(format, this);
    }

    public StringBinding asString(Locale locale, String format) {
        return (StringBinding) Bindings.format(locale, format, this);
    }
}
