package org.koishi.launcher.h2co3core.fakefx.beans.binding;

import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableStringValue;
import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableValue;
import org.koishi.launcher.h2co3core.fakefx.binding.StringFormatter;

public abstract class StringExpression implements ObservableStringValue {

    public StringExpression() {
    }

    public static StringExpression stringExpression(
            final ObservableValue<?> value) {
        if (value == null) {
            throw new NullPointerException("Value must be specified.");
        }
        return StringFormatter.convert(value);
    }

    @Override
    public String getValue() {
        return get();
    }

    public final String getValueSafe() {
        final String value = get();
        return value == null ? "" : value;
    }

    public StringExpression concat(Object other) {
        return Bindings.concat(this, other);
    }

    public BooleanBinding isEqualTo(final ObservableStringValue other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isEqualTo(final String other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isNotEqualTo(final ObservableStringValue other) {
        return Bindings.notEqual(this, other);
    }

    public BooleanBinding isNotEqualTo(final String other) {
        return Bindings.notEqual(this, other);
    }

    public BooleanBinding isEqualToIgnoreCase(final ObservableStringValue other) {
        return Bindings.equalIgnoreCase(this, other);
    }

    public BooleanBinding isEqualToIgnoreCase(final String other) {
        return Bindings.equalIgnoreCase(this, other);
    }

    public BooleanBinding isNotEqualToIgnoreCase(
            final ObservableStringValue other) {
        return Bindings.notEqualIgnoreCase(this, other);
    }

    public BooleanBinding isNotEqualToIgnoreCase(final String other) {
        return Bindings.notEqualIgnoreCase(this, other);
    }

    public BooleanBinding greaterThan(final ObservableStringValue other) {
        return Bindings.greaterThan(this, other);
    }

    public BooleanBinding greaterThan(final String other) {
        return Bindings.greaterThan(this, other);
    }

    public BooleanBinding lessThan(final ObservableStringValue other) {
        return Bindings.lessThan(this, other);
    }

    public BooleanBinding lessThan(final String other) {
        return Bindings.lessThan(this, other);
    }

    public BooleanBinding greaterThanOrEqualTo(final ObservableStringValue other) {
        return Bindings.greaterThanOrEqual(this, other);
    }

    public BooleanBinding greaterThanOrEqualTo(final String other) {
        return Bindings.greaterThanOrEqual(this, other);
    }

    public BooleanBinding lessThanOrEqualTo(final ObservableStringValue other) {
        return Bindings.lessThanOrEqual(this, other);
    }

    public BooleanBinding lessThanOrEqualTo(final String other) {
        return Bindings.lessThanOrEqual(this, other);
    }

    public BooleanBinding isNull() {
        return Bindings.isNull(this);
    }

    public BooleanBinding isNotNull() {
        return Bindings.isNotNull(this);
    }

    public IntegerBinding length() {
        return Bindings.length(this);
    }

    public BooleanBinding isEmpty() {
        return Bindings.isEmpty(this);
    }

    public BooleanBinding isNotEmpty() {
        return Bindings.isNotEmpty(this);
    }
}
