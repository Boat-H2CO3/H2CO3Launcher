package org.koishi.launcher.h2co3core.fakefx.property.adapter;

import org.koishi.launcher.h2co3core.fakefx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;

public final class JavaBeanQuickAccessor {

    private JavaBeanQuickAccessor() {
    }

    public static <T> ReadOnlyJavaBeanObjectProperty<T> createReadOnlyJavaBeanObjectProperty(Object bean, String name) throws NoSuchMethodException {
        return ReadOnlyJavaBeanObjectPropertyBuilder.<T>create().bean(bean).name(name).build();
    }

}
