package org.koishi.launcher.h2co3core.fakefx.reflect;

import java.lang.reflect.Constructor;

public final class ConstructorUtil {

    private ConstructorUtil() {
    }

    public static Constructor<?> getConstructor(Class<?> cls, Class<?>[] params)
        throws NoSuchMethodException {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getConstructor(params);
    }

    public static Constructor<?>[] getConstructors(Class<?> cls) {
        ReflectUtil.checkPackageAccess(cls);
        return cls.getConstructors();
    }
}
