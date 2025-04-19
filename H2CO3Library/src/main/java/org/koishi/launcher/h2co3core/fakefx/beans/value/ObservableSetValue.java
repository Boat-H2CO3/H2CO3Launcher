package org.koishi.launcher.h2co3core.fakefx.beans.value;

import org.koishi.launcher.h2co3core.fakefx.collections.ObservableSet;

/**
 * An observable reference to an {@link ObservableSet}.
 *
 * @see ObservableSet
 * @see ObservableObjectValue
 * @see ObservableValue
 *
 * @param <E> the type of the {@code Set} elements
 * @since JavaFX 2.1
 */
public interface ObservableSetValue<E> extends ObservableObjectValue<ObservableSet<E>>, ObservableSet<E> {
}
