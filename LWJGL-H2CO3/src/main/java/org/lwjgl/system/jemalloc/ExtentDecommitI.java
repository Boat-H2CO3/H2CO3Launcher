/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.jemalloc;

import static org.lwjgl.system.APIUtil.apiClosureRet;
import static org.lwjgl.system.APIUtil.apiCreateCIF;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memGetInt;
import static org.lwjgl.system.libffi.LibFFI.FFI_DEFAULT_ABI;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_pointer;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_uint32;
import static org.lwjgl.system.libffi.LibFFI.ffi_type_uint8;

import org.lwjgl.system.CallbackI;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;

/**
 * Instances of this interface may be set to the {@link ExtentHooks} struct.
 * 
 * <h3>Type</h3>
 * 
 * <pre><code>
 * bool (*{@link #invoke}) (
 *     extent_hooks_t *extent_hooks,
 *     void *addr,
 *     size_t size,
 *     size_t offset,
 *     size_t length,
 *     unsigned int arena_ind
 * )</code></pre>
 */
@FunctionalInterface
@NativeType("extent_decommit_t")
public interface ExtentDecommitI extends CallbackI {

    FFICIF CIF = apiCreateCIF(
        FFI_DEFAULT_ABI,
        ffi_type_uint8,
        ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_pointer, ffi_type_uint32
    );

    @Override
    default FFICIF getCallInterface() { return CIF; }

    @Override
    default void callback(long ret, long args) {
        boolean __result = invoke(
                memGetAddress(memGetAddress(args)),
                memGetAddress(memGetAddress(args + POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 2L * POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 3L * POINTER_SIZE)),
                memGetAddress(memGetAddress(args + 4L * POINTER_SIZE)),
                memGetInt(memGetAddress(args + 5L * POINTER_SIZE))
        );
        apiClosureRet(ret, __result);
    }

    /**
     * Extent decommit hook.
     * 
     * <p>An extent decommit function conforms to the {@code extent_decommit_t} type and decommits any physical memory that is backing pages within an extent at
     * given {@code addr} and {@code size} at {@code offset} bytes, extending for {@code length} on behalf of arena {@code arena_ind}, returning false upon
     * success, in which case the pages will be committed via the extent commit function before being reused.  If the function returns true, this indicates
     * opt-out from decommit; the memory remains committed and available for future use, in which case it will be automatically retained for later reuse.</p>
     */
    @NativeType("bool") boolean invoke(@NativeType("extent_hooks_t *") long extent_hooks, @NativeType("void *") long addr, @NativeType("size_t") long size, @NativeType("size_t") long offset, @NativeType("size_t") long length, @NativeType("unsigned int") int arena_ind);

}