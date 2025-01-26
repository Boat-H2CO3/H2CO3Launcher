/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.koishi.launcher.h2co3.util;

import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.beans.WeakInvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.property.Property;
import org.koishi.launcher.h2co3core.fakefx.beans.value.ChangeListener;
import org.koishi.launcher.h2co3core.fakefx.beans.value.ObservableValue;
import org.koishi.launcher.h2co3core.fakefx.beans.value.WeakChangeListener;
import org.koishi.launcher.h2co3core.fakefx.util.StringConverter;
import org.koishi.launcher.h2co3core.util.fakefx.SafeStringConverter;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherCheckBox;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherEditText;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSpinner;
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherSwitch;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FXUtils {

    public static InvalidationListener onInvalidating(Runnable action) {
        return arg -> action.run();
    }

    public static <T> void onChangeAndOperate(ObservableValue<T> value, Consumer<T> consumer) {
        consumer.accept(value.getValue());
        onChange(value, consumer);
    }

    public static <T> ChangeListener<T> onWeakChangeAndOperate(ObservableValue<T> value, Consumer<T> consumer) {
        consumer.accept(value.getValue());
        return onWeakChange(value, consumer);
    }

    public static <T> void onChange(ObservableValue<T> value, Consumer<T> consumer) {
        value.addListener((a, b, c) -> consumer.accept(c));
    }

    public static <T> ChangeListener<T> onWeakChange(ObservableValue<T> value, Consumer<T> consumer) {
        ChangeListener<T> listener = (a, b, c) -> consumer.accept(c);
        value.addListener(new WeakChangeListener<>(listener));
        return listener;
    }

    public static InvalidationListener observeWeak(Runnable runnable, Observable... observables) {
        InvalidationListener originalListener = observable -> runnable.run();
        WeakInvalidationListener listener = new WeakInvalidationListener(originalListener);
        for (Observable observable : observables) {
            observable.addListener(listener);
        }
        runnable.run();
        return originalListener;
    }

    public static <T> StringConverter<T> stringConverter(Function<T, String> func) {
        return new StringConverter<T>() {

            @Override
            public String toString(T object) {
                return object == null ? "" : func.apply(object);
            }

            @Override
            public T fromString(String string) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> void bind(H2CO3LauncherEditText editText, Property<T> property, StringConverter<T> converter) {
        editText.setText(converter == null ? (String) property.getValue() : converter.toString(property.getValue()));
        EditTextBindingListener<T> listener = new EditTextBindingListener<>(editText, property, converter);
        editText.stringProperty().addListener((ChangeListener<String>) listener);
        property.addListener(listener);
    }

    public static void bindInt(H2CO3LauncherEditText textField, Property<Number> property) {
        bind(textField, property, SafeStringConverter.fromInteger());
    }

    public static void bindString(H2CO3LauncherEditText editText, Property<String> property) {
        bind(editText, property, null);
    }

    public static void unbind(H2CO3LauncherEditText editText, Property<?> property) {
        EditTextBindingListener<?> listener = new EditTextBindingListener<>(editText, property, null);
        editText.stringProperty().removeListener((ChangeListener<String>) listener);
        property.removeListener(listener);
    }

    public static void bindBoolean(H2CO3LauncherSwitch h2co3LauncherSwitch, Property<Boolean> property) {
        h2co3LauncherSwitch.addCheckedChangeListener();
        h2co3LauncherSwitch.checkProperty().bindBidirectional(property);
    }

    public static void unbindBoolean(H2CO3LauncherSwitch h2co3LauncherSwitch, Property<Boolean> property) {
        h2co3LauncherSwitch.checkProperty().unbindBidirectional(property);
    }

    public static void bindBoolean(H2CO3LauncherCheckBox checkBox, Property<Boolean> property) {
        checkBox.addCheckedChangeListener();
        checkBox.checkProperty().bindBidirectional(property);
    }

    public static void unbindBoolean(H2CO3LauncherCheckBox checkBox, Property<Boolean> property) {
        checkBox.checkProperty().unbindBidirectional(property);
    }

    public static <T> void bindSelection(H2CO3LauncherSpinner<T> spinner, Property<T> property) {
        spinner.addSelectListener();
        spinner.selectedItemProperty().bindBidirectional(property);
    }

    public static <T> void unbindSelection(H2CO3LauncherSpinner<T> spinner, Property<T> property) {
        spinner.selectedItemProperty().unbindBidirectional(property);
    }

    private static final class EditTextBindingListener<T> implements ChangeListener<String>, InvalidationListener {
        private final int hashCode;
        private final WeakReference<H2CO3LauncherEditText> editTextRef;
        private final WeakReference<Property<T>> propertyRef;
        private final StringConverter<T> converter;

        EditTextBindingListener(H2CO3LauncherEditText editText, Property<T> property, StringConverter<T> converter) {
            this.editTextRef = new WeakReference<>(editText);
            this.propertyRef = new WeakReference<>(property);
            this.converter = converter;
            this.hashCode = System.identityHashCode(editText) ^ System.identityHashCode(property);
        }

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String str) { // On EditText changed
            H2CO3LauncherEditText editText = editTextRef.get();
            Property<T> property = this.propertyRef.get();

            if (editText != null && property != null) {
                String newText = editText.getText().toString();
                @SuppressWarnings("unchecked")
                T newValue = converter == null ? (T) newText : converter.fromString(newText);

                if (!Objects.equals(newValue, property.getValue()))
                    property.setValue(newValue);
            }
        }

        @Override
        public void invalidated(Observable observable) { // On property change
            H2CO3LauncherEditText editText = editTextRef.get();
            Property<T> property = this.propertyRef.get();

            if (editText != null && property != null) {
                if (!editText.fromUserOrSystem) {
                    T value = property.getValue();
                    editText.setText(converter == null ? (String) value : converter.toString(value));
                }
            }
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EditTextBindingListener))
                return false;
            EditTextBindingListener<?> other = (EditTextBindingListener<?>) obj;
            return this.hashCode == other.hashCode
                    && this.editTextRef.get() == other.editTextRef.get()
                    && this.propertyRef.get() == other.propertyRef.get();
        }
    }

}
