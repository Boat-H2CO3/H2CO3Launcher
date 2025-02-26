package org.koishi.launcher.h2co3.core.fakefx.beans.property;

import org.koishi.launcher.h2co3.core.fakefx.beans.binding.Bindings;
import org.koishi.launcher.h2co3.core.fakefx.beans.binding.ListExpression;
import org.koishi.launcher.h2co3.core.fakefx.collections.ObservableList;

import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public abstract class ReadOnlyListProperty<E> extends ListExpression<E>
        implements ReadOnlyProperty<ObservableList<E>> {

    /**
     * The constructor of {@code ReadOnlyListProperty}.
     */
    public ReadOnlyListProperty() {
    }

    /**
     * Creates a bidirectional content binding of the {@link ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code ObservableList}.
     * <p>
     * A bidirectional content binding ensures that the content of two {@code ObservableLists} is the
     * same. If the content of one of the lists changes, the other one will be updated automatically.
     *
     * @param list the {@code ObservableList} this property should be bound to
     * @throws NullPointerException     if {@code list} is {@code null}
     * @throws IllegalArgumentException if {@code list} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void bindContentBidirectional(ObservableList<E> list) {
        Bindings.bindContentBidirectional(this, list);
    }

    /**
     * Deletes a bidirectional content binding between the {@link ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code Object}.
     *
     * @param object the {@code Object} to which the bidirectional binding should be removed
     * @throws NullPointerException     if {@code object} is {@code null}
     * @throws IllegalArgumentException if {@code object} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void unbindContentBidirectional(Object object) {
        Bindings.unbindContentBidirectional(this, object);
    }

    /**
     * Creates a content binding between the {@link ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code ObservableList}.
     * <p>
     * A content binding ensures that the content of the wrapped {@code ObservableLists} is the
     * same as that of the other list. If the content of the other list changes, the wrapped list will be updated
     * automatically. Once the wrapped list is bound to another list, you must not change it directly.
     *
     * @param list the {@code ObservableList} this property should be bound to
     * @throws NullPointerException     if {@code list} is {@code null}
     * @throws IllegalArgumentException if {@code list} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void bindContent(ObservableList<E> list) {
        Bindings.bindContent(this, list);
    }

    /**
     * Deletes a content binding between the {@link ObservableList}, that is
     * wrapped in this {@code ReadOnlyListProperty}, and another {@code Object}.
     *
     * @param object the {@code Object} to which the binding should be removed
     * @throws NullPointerException     if {@code object} is {@code null}
     * @throws IllegalArgumentException if {@code object} is the same list that this {@code ReadOnlyListProperty} points to
     */
    public void unbindContent(Object object) {
        Bindings.unbindContent(this, object);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof List list)) {
            return false;
        }

        if (size() != list.size()) {
            return false;
        }

        ListIterator<E> e1 = listIterator();
        ListIterator e2 = list.listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            if (!(Objects.equals(o1, o2)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this)
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        return hashCode;
    }

    /**
     * Returns a string representation of this {@code ReadOnlyListProperty} object.
     *
     * @return a string representation of this {@code ReadOnlyListProperty} object.
     */
    @Override
    public String toString() {
        final Object bean = getBean();
        final String name = getName();
        final StringBuilder result = new StringBuilder(
                "ReadOnlyListProperty [");
        if (bean != null) {
            result.append("bean: ").append(bean).append(", ");
        }
        if ((name != null) && !name.equals("")) {
            result.append("name: ").append(name).append(", ");
        }
        result.append("value: ").append(get()).append("]");
        return result.toString();
    }

}
