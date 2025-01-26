package org.lwjgl.glfw;

import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.system.Callback;

import javax.annotation.Nullable;

public abstract class H2CO3LauncherInjectorCallback extends Callback implements H2CO3LauncherInjectorCallbackI {

    protected H2CO3LauncherInjectorCallback() {
        super(CIF);
    }

    H2CO3LauncherInjectorCallback(long functionPointer) {
        super(functionPointer);
    }

    public static H2CO3LauncherInjectorCallback create(long functionPointer) {
        H2CO3LauncherInjectorCallbackI instance = Callback.get(functionPointer);
        return instance instanceof H2CO3LauncherInjectorCallback
                ? (H2CO3LauncherInjectorCallback)instance
                : new Container(functionPointer, instance);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}. */
    @Nullable
    public static H2CO3LauncherInjectorCallback createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /** Creates a {@code GLFWCursorEnterCallback} instance that delegates to the specified {@code GLFWCursorEnterCallbackI} instance. */
    public static H2CO3LauncherInjectorCallback create(H2CO3LauncherInjectorCallbackI instance) {
        return instance instanceof H2CO3LauncherInjectorCallback
                ? (H2CO3LauncherInjectorCallback)instance
                : new Container(instance.address(), instance);
    }

    private static final class Container extends H2CO3LauncherInjectorCallback {

        private final H2CO3LauncherInjectorCallbackI delegate;

        Container(long functionPointer, H2CO3LauncherInjectorCallbackI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke() {
            delegate.invoke();
        }

    }

}