package org.koishi.launcher.h2co3core.fakefx.beans.value;

import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.binding.FlatMappedBinding;
import org.koishi.launcher.h2co3core.fakefx.binding.MappedBinding;
import org.koishi.launcher.h2co3core.fakefx.binding.OrElseBinding;

import java.util.function.Function;

public interface ObservableValue<T> extends Observable {

    void addListener(ChangeListener<? super T> listener);

    void removeListener(ChangeListener<? super T> listener);

    T getValue();

    default <U> ObservableValue<U> map(Function<? super T, ? extends U> mapper) {
        return new MappedBinding<>(this, mapper);
    }

    default ObservableValue<T> orElse(T constant) {
        return new OrElseBinding<>(this, constant);
    }

    default <U> ObservableValue<U> flatMap(Function<? super T, ? extends ObservableValue<? extends U>> mapper) {
        return new FlatMappedBinding<>(this, mapper);
    }
}
