/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.APIUtil.apiClosureRet;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_sint32;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * <h3>Type</h3>
 *
 * <pre><code>
 * int (*{@link #invoke}) (
 *     void *memory,
 *     FT_Raster *raster
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("FT_Raster_NewFunc")
public interface FT_Raster_NewFuncI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
            FFI_DEFAULT_ABI,
            ffi_type_sint32,
            ffi_type_pointer, ffi_type_pointer
    );

    @Override
    default FFICIF getCallInterface() {
        return CIF;
    }

    @Override
    default void callback(long ret, long args) {
        int __result = invoke(
                memGetAddress(memGetAddress(args)),
                memGetAddress(memGetAddress(args + POINTER_SIZE))
        );
        apiClosureRet(ret, __result);
    }

    /**
     * A function used to create a new raster object.
     */
    int invoke(@NativeType("void *") long memory, @NativeType("FT_Raster *") long raster);

}