package org.koishi.launcher.h2co3core.fakefx.beans.binding;

import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableBooleanValue;
import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableValue;
import org.koishi.launcher.h2co3core.fakefx.binding.StringFormatter;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;

public abstract class BooleanExpression implements ObservableBooleanValue {

    public BooleanExpression() {
    }

    public static BooleanExpression booleanExpression(
            final ObservableBooleanValue value) {
        if (value == null) {
            throw new NullPointerException("Value must be specified.");
        }
        return (value instanceof BooleanExpression) ? (BooleanExpression) value
                : new BooleanBinding() {
                    {
                        super.bind(value);
                    }

                    @Override
                    public void dispose() {
                        super.unbind(value);
                    }

                    @Override
                    protected boolean computeValue() {
                        return value.get();
                    }

                    @Override
                    public ObservableList<ObservableBooleanValue> getDependencies() {
                        return FXCollections.singletonObservableList(value);
                    }
                };
    }

    public static BooleanExpression booleanExpression(final ObservableValue<Boolean> value) {
        if (value == null) {
            throw new NullPointerException("Value must be specified.");
        }
        return (value instanceof BooleanExpression) ? (BooleanExpression) value
                : new BooleanBinding() {
            {
                super.bind(value);
            }

            @Override
            public void dispose() {
                super.unbind(value);
            }

            @Override
            protected boolean computeValue() {
                final Boolean val = value.getValue();
                return val == null ? false : val;
            }

            @Override
            public ObservableList<ObservableValue<Boolean>> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    @Override
    public Boolean getValue() {
        return get();
    }

    public BooleanBinding and(final ObservableBooleanValue other) {
        return Bindings.and(this, other);
    }

    public BooleanBinding or(final ObservableBooleanValue other) {
        return Bindings.or(this, other);
    }

    public BooleanBinding not() {
        return Bindings.not(this);
    }

    public BooleanBinding isEqualTo(final ObservableBooleanValue other) {
        return Bindings.equal(this, other);
    }

    public BooleanBinding isNotEqualTo(final ObservableBooleanValue other) {
        return Bindings.notEqual(this, other);
    }

    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    public ObjectExpression<Boolean> asObject() {
        return new ObjectBinding<Boolean>() {
            {
                bind(BooleanExpression.this);
            }

            @Override
            public void dispose() {
                unbind(BooleanExpression.this);
            }

            @Override
            protected Boolean computeValue() {
                return BooleanExpression.this.getValue();
            }
        };
    }
}
