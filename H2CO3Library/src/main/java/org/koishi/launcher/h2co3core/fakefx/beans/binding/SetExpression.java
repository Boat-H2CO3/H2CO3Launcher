package org.koishi.launcher.h2co3core.fakefx.beans.binding;

import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ReadOnlyIntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableSetValue;
import org.koishi.launcher.h2co3core.fakefx.binding.StringFormatter;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableSet;
import org.koishi.launcher.h2co3core.fakefx.collections.SetChangeListener;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class SetExpression<E> implements ObservableSetValue<E> {

    private static final ObservableSet EMPTY_SET = new EmptyObservableSet();

    /**
     * Creates a default {@code SetExpression}.
     */
    public SetExpression() {
    }

    public static <E> SetExpression<E> setExpression(final ObservableSetValue<E> value) {
        if (value == null) {
            throw new NullPointerException("Set must be specified.");
        }
        return value instanceof SetExpression ? (SetExpression<E>) value
                : new SetBinding<E>() {
            {
                super.bind(value);
            }

            @Override
            public void dispose() {
                super.unbind(value);
            }

            @Override
            protected ObservableSet<E> computeValue() {
                return value.get();
            }

            @Override
            public ObservableList<?> getDependencies() {
                return FXCollections.singletonObservableList(value);
            }
        };
    }

    @Override
    public ObservableSet<E> getValue() {
        return get();
    }

    /**
     * The size of the set
     * @return the size
     */
    public int getSize() {
        return size();
    }

    /**
     * An integer property that represents the size of the set.
     * @return the property
     */
    public abstract ReadOnlyIntegerProperty sizeProperty();

    /**
     * A boolean property that is {@code true}, if the set is empty.
     * @return the {@code ReadOnlyBooleanProperty}
     */
    public abstract ReadOnlyBooleanProperty emptyProperty();

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if this set is equal to
     * another {@link ObservableSet}.
     *
     * @param other
     *            the other {@code ObservableSet}
     * @return the new {@code BooleanBinding}
     * @throws NullPointerException
     *             if {@code other} is {@code null}
     */
    public BooleanBinding isEqualTo(final ObservableSet<?> other) {
        return Bindings.equal(this, other);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if this set is not equal to
     * another {@link ObservableSet}.
     *
     * @param other
     *            the other {@code ObservableSet}
     * @return the new {@code BooleanBinding}
     * @throws NullPointerException
     *             if {@code other} is {@code null}
     */
    public BooleanBinding isNotEqualTo(final ObservableSet<?> other) {
        return Bindings.notEqual(this, other);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if the wrapped set is {@code null}.
     *
     * @return the new {@code BooleanBinding}
     */
    public BooleanBinding isNull() {
        return Bindings.isNull(this);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if the wrapped set is not {@code null}.
     *
     * @return the new {@code BooleanBinding}
     */
    public BooleanBinding isNotNull() {
        return Bindings.isNotNull(this);
    }

    public StringBinding asString() {
        return (StringBinding) StringFormatter.convert(this);
    }

    @Override
    public int size() {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.size() : set.size();
    }

    @Override
    public boolean isEmpty() {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.isEmpty() : set.isEmpty();
    }

    @Override
    public boolean contains(Object obj) {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.contains(obj) : set.contains(obj);
    }

    @Override
    public Iterator<E> iterator() {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.iterator() : set.iterator();
    }

    @Override
    public Object[] toArray() {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.toArray() : set.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        final ObservableSet<E> set = get();
        return (set == null)? (T[]) EMPTY_SET.toArray(array) : set.toArray(array);
     }

    @Override
    public boolean add(E element) {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.add(element) : set.add(element);
    }

    @Override
    public boolean remove(Object obj) {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.remove(obj) : set.remove(obj);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.contains(objects) : set.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.addAll(elements) : set.addAll(elements);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.removeAll(objects) : set.removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        final ObservableSet<E> set = get();
        return (set == null)? EMPTY_SET.retainAll(objects) : set.retainAll(objects);
    }

    @Override
    public void clear() {
        final ObservableSet<E> set = get();
        if (set == null) {
            EMPTY_SET.clear();
        } else {
            set.clear();
        }
    }

    private static class EmptyObservableSet<E> extends AbstractSet<E> implements ObservableSet<E> {

        private static final Iterator iterator = new Iterator() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();

            }
        };

        @Override
        public Iterator<E> iterator() {
            return iterator;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void addListener(SetChangeListener<? super E> setChangeListener) {
            // no-op
        }

        @Override
        public void removeListener(SetChangeListener<? super E> setChangeListener) {
            // no-op
        }

        @Override
        public void addListener(InvalidationListener listener) {
            // no-op
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            // no-op
        }
    }

}
