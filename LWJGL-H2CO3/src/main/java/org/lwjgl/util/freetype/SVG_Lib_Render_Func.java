/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.system.Callback;

import javax.annotation.Nullable;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * FT_Error (*{@link #invoke}) (
 *     FT_GlyphSlot slot,
 *     FT_Pointer *data_pointer
 * )</code></pre>
 */
public abstract class SVG_Lib_Render_Func extends Callback implements SVG_Lib_Render_FuncI {

    protected SVG_Lib_Render_Func() {
        super(CIF);
    }

    SVG_Lib_Render_Func(long functionPointer) {
        super(functionPointer);
    }

    /**
     * Creates a {@code SVG_Lib_Render_Func} instance from the specified function pointer.
     *
     * @return the new {@code SVG_Lib_Render_Func}
     */
    public static SVG_Lib_Render_Func create(long functionPointer) {
        SVG_Lib_Render_FuncI instance = Callback.get(functionPointer);
        return instance instanceof SVG_Lib_Render_Func
                ? (SVG_Lib_Render_Func) instance
                : new Container(functionPointer, instance);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if {@code functionPointer} is {@code NULL}.
     */
    @Nullable
    public static SVG_Lib_Render_Func createSafe(long functionPointer) {
        return functionPointer == NULL ? null : create(functionPointer);
    }

    /**
     * Creates a {@code SVG_Lib_Render_Func} instance that delegates to the specified {@code SVG_Lib_Render_FuncI} instance.
     */
    public static SVG_Lib_Render_Func create(SVG_Lib_Render_FuncI instance) {
        return instance instanceof SVG_Lib_Render_Func
                ? (SVG_Lib_Render_Func) instance
                : new Container(instance.address(), instance);
    }

    private static final class Container extends SVG_Lib_Render_Func {

        private final SVG_Lib_Render_FuncI delegate;

        Container(long functionPointer, SVG_Lib_Render_FuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long slot, long data_pointer) {
            return delegate.invoke(slot, data_pointer);
        }

    }

}