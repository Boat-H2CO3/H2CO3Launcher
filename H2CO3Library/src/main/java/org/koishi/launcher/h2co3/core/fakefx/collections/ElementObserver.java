package org.koishi.launcher.h2co3.core.fakefx.collections;

import org.koishi.launcher.h2co3.core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3.core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3.core.fakefx.util.Callback;

import java.util.IdentityHashMap;

final class ElementObserver<E> {

    private static class ElementsMapElement {
        InvalidationListener listener;
        int counter;

        public ElementsMapElement(InvalidationListener listener) {
            this.listener = listener;
            this.counter = 1;
        }

        public void increment() {
            counter++;
        }

        public int decrement() {
            return --counter;
        }

        private InvalidationListener getListener() {
            return listener;
        }
    }

    private final Callback<E, Observable[]> extractor;
    private final Callback<E, InvalidationListener> listenerGenerator;
    private final ObservableListBase<E> list;
    private final IdentityHashMap<E, ElementsMapElement> elementsMap =
            new IdentityHashMap<E, ElementsMapElement>();

    ElementObserver(Callback<E, Observable[]> extractor, Callback<E, InvalidationListener> listenerGenerator, ObservableListBase<E> list) {
        this.extractor = extractor;
        this.listenerGenerator = listenerGenerator;
        this.list = list;
    }


    void attachListener(final E e) {
        if (elementsMap != null && e != null) {
            if (elementsMap.containsKey(e)) {
                elementsMap.get(e).increment();
            } else {
                InvalidationListener listener = listenerGenerator.call(e);
                for (Observable o : extractor.call(e)) {
                    o.addListener(listener);
                }
                elementsMap.put(e, new ElementsMapElement(listener));
            }
        }
    }

    void detachListener(E e) {
        if (elementsMap != null && e != null) {
            ElementsMapElement el = elementsMap.get(e);
            for (Observable o : extractor.call(e)) {
                o.removeListener(el.getListener());
            }
            if (el.decrement() == 0) {
                elementsMap.remove(e);
            }
        }
    }

}
