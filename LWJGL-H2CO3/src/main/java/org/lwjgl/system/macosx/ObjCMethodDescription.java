/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.macosx;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBufferNT1;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.system.MemoryUtil.nmemAllocChecked;
import static org.lwjgl.system.MemoryUtil.nmemCallocChecked;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

/**
 * Defines a method.
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct objc_method_description {
 *     SEL {@link #name};
 *     char * {@link #types};
 * }</code></pre>
 */
@NativeType("struct objc_method_description")
public class ObjCMethodDescription extends Struct<ObjCMethodDescription> implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        NAME,
        TYPES;

    static {
        Layout layout = __struct(
            __member(POINTER_SIZE),
            __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        NAME = layout.offsetof(0);
        TYPES = layout.offsetof(1);
    }

    protected ObjCMethodDescription(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected ObjCMethodDescription create(long address, @Nullable ByteBuffer container) {
        return new ObjCMethodDescription(address, container);
    }

    /**
     * Creates a {@code ObjCMethodDescription} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public ObjCMethodDescription(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** the name of the method at runtime */
    @NativeType("SEL")
    public long name() { return nname(address()); }
    /** the types of the method arguments */
    @NativeType("char *")
    public ByteBuffer types() { return ntypes(address()); }
    /** the types of the method arguments */
    @NativeType("char *")
    public String typesString() { return ntypesString(address()); }

    // -----------------------------------

    /** Returns a new {@code ObjCMethodDescription} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static ObjCMethodDescription malloc() {
        return new ObjCMethodDescription(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code ObjCMethodDescription} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static ObjCMethodDescription calloc() {
        return new ObjCMethodDescription(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code ObjCMethodDescription} instance allocated with {@link BufferUtils}. */
    public static ObjCMethodDescription create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new ObjCMethodDescription(memAddress(container), container);
    }

    /** Returns a new {@code ObjCMethodDescription} instance for the specified memory address. */
    public static ObjCMethodDescription create(long address) {
        return new ObjCMethodDescription(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static ObjCMethodDescription createSafe(long address) {
        return address == NULL ? null : new ObjCMethodDescription(address, null);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity) {
        return new Buffer(nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity) {
        return new Buffer(nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated with {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return new Buffer(memAddress(container), container, -1, 0, capacity, capacity);
    }

    /**
     * Create a {@link Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    /** Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    // -----------------------------------

    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static ObjCMethodDescription mallocStack() { return malloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static ObjCMethodDescription callocStack() { return calloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static ObjCMethodDescription mallocStack(MemoryStack stack) { return malloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static ObjCMethodDescription callocStack(MemoryStack stack) { return calloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity) { return malloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity) { return calloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity, MemoryStack stack) { return malloc(capacity, stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity, MemoryStack stack) { return calloc(capacity, stack); }

    /**
     * Returns a new {@code ObjCMethodDescription} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static ObjCMethodDescription malloc(MemoryStack stack) {
        return new ObjCMethodDescription(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code ObjCMethodDescription} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static ObjCMethodDescription calloc(MemoryStack stack) {
        return new ObjCMethodDescription(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Buffer} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /** Unsafe version of {@link #name}. */
    public static long nname(long struct) { return memGetAddress(struct + ObjCMethodDescription.NAME); }
    /** Unsafe version of {@link #types}. */
    public static ByteBuffer ntypes(long struct) { return memByteBufferNT1(memGetAddress(struct + ObjCMethodDescription.TYPES)); }
    /** Unsafe version of {@link #typesString}. */
    public static String ntypesString(long struct) { return memUTF8(memGetAddress(struct + ObjCMethodDescription.TYPES)); }

    // -----------------------------------

    /** An array of {@link ObjCMethodDescription} structs. */
    public static class Buffer extends StructBuffer<ObjCMethodDescription, Buffer> implements NativeResource {

        private static final ObjCMethodDescription ELEMENT_FACTORY = ObjCMethodDescription.create(-1L);

        /**
         * Creates a new {@code ObjCMethodDescription.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link ObjCMethodDescription#SIZEOF}, and its mark will be undefined.</p>
         *
         * <p>The created buffer instance holds a strong reference to the container object.</p>
         */
        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected ObjCMethodDescription getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return the value of the {@link ObjCMethodDescription#name} field. */
        @NativeType("SEL")
        public long name() { return ObjCMethodDescription.nname(address()); }
        /** @return a {@link ByteBuffer} view of the null-terminated string pointed to by the {@link ObjCMethodDescription#types} field. */
        @NativeType("char *")
        public ByteBuffer types() { return ObjCMethodDescription.ntypes(address()); }
        /** @return the null-terminated string pointed to by the {@link ObjCMethodDescription#types} field. */
        @NativeType("char *")
        public String typesString() { return ObjCMethodDescription.ntypesString(address()); }

    }

}