package org.koishi.launcher.h2co3.core.fakefx.property.adapter;

import org.koishi.launcher.h2co3.core.fakefx.reflect.ReflectUtil;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 *
 */
public class ReadOnlyJavaBeanPropertyBuilderHelper {

    private static final String IS_PREFIX = "is";
    private static final String GET_PREFIX = "get";

    private String propertyName;
    private Class<?> beanClass;
    private Object bean;
    private String getterName;
    private Method getter;
    private ReadOnlyPropertyDescriptor descriptor;

    public void name(String propertyName) {
        if (!Objects.equals(propertyName, this.propertyName)) {
            this.propertyName = propertyName;
            this.descriptor = null;
        }
    }

    public void beanClass(Class<?> beanClass) {
        if (!Objects.equals(beanClass, this.beanClass)) {
            ReflectUtil.checkPackageAccess(beanClass);
            this.beanClass = beanClass;
            this.descriptor = null;
        }
    }

    public void bean(Object bean) {
        this.bean = bean;
        if (bean != null) {
            Class<?> newClass = bean.getClass();
            if ((beanClass == null) || !beanClass.isAssignableFrom(newClass)) {
                ReflectUtil.checkPackageAccess(newClass);
                this.beanClass = bean.getClass();
                this.descriptor = null;
            }
        }
    }

    public Object getBean() {
        return bean;
    }

    public void getterName(String getterName) {
        if (!Objects.equals(getterName, this.getterName)) {
            this.getterName = getterName;
            this.descriptor = null;
        }
    }

    public void getter(Method getter) {
        if (!Objects.equals(getter, this.getter)) {
            this.getter = getter;
            this.descriptor = null;
        }
    }

    public ReadOnlyPropertyDescriptor getDescriptor() throws NoSuchMethodException {
        if (descriptor == null) {
            if ((propertyName == null) || (bean == null)) {
                throw new NullPointerException("Bean and property name have to be specified");
            }
            if (propertyName.isEmpty()) {
                throw new IllegalArgumentException("Property name cannot be empty");
            }
            final String capitalizedName = ReadOnlyPropertyDescriptor.capitalizedName(propertyName);
            if (getter == null) {
                if ((getterName != null) && !getterName.isEmpty()) {
                    getter = beanClass.getMethod(getterName);
                } else {
                    try {
                        getter = beanClass.getMethod(IS_PREFIX + capitalizedName);
                    } catch (NoSuchMethodException e) {
                        getter = beanClass.getMethod(GET_PREFIX + capitalizedName);
                    }
                }
            }
            descriptor = new ReadOnlyPropertyDescriptor(propertyName, beanClass, getter);
        }
        return descriptor;
    }

}
