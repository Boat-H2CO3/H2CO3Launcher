package org.koishi.launcher.h2co3.core.fakefx.beans.value;

import org.koishi.launcher.h2co3.core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3.core.fakefx.binding.ExpressionHelper;

/**
 * A convenience class for creating implementations of {@link ObservableValue}.
 * It contains all of the infrastructure support for value invalidation- and
 * change event notification.
 * <p>
 * This implementation can handle adding and removing listeners while the
 * observers are being notified, but it is not thread-safe.
 *
 * @since JavaFX 2.0
 */
public abstract class ObservableValueBase<T> implements ObservableValue<T> {

    private ExpressionHelper<T> helper;

    /**
     * Creates a default {@code ObservableValueBase}.
     */
    public ObservableValueBase() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(InvalidationListener listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(ChangeListener<? super T> listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(InvalidationListener listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(ChangeListener<? super T> listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

    /**
     * Notify the currently registered observers of a value change.
     * <p>
     * This implementation will ignore all adds and removes of observers that
     * are done while a notification is processed. The changes take effect in
     * the following call to fireValueChangedEvent.
     */
    protected void fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(helper);
    }
}
