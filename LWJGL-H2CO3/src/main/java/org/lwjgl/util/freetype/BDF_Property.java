/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memASCIISafe;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBufferNT1Safe;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
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
 * This structure models a given BDF/PCF property.
 *
 * <h3>Layout</h3>
 *
 * <pre><code>
 * struct BDF_PropertyRec {
 *     BDF_PropertyType type;
 *     union {
 *         char const * atom;
 *         FT_Int32 integer;
 *         FT_UInt32 cardinal;
 *     } u;
 * }</code></pre>
 */
@NativeType("struct BDF_PropertyRec")
public class BDF_Property extends Struct<BDF_Property> implements NativeResource {

    /**
     * The struct size in bytes.
     */
    public static final int SIZEOF;

    /**
     * The struct alignment in bytes.
     */
    public static final int ALIGNOF;

    /**
     * The struct member offsets.
     */
    public static final int
            TYPE,
            U,
            U_ATOM,
            U_INTEGER,
            U_CARDINAL;

    static {
        Layout layout = __struct(
                __member(4),
                __union(
                        __member(POINTER_SIZE),
                        __member(4),
                        __member(4)
                )
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        TYPE = layout.offsetof(0);
        U = layout.offsetof(1);
        U_ATOM = layout.offsetof(2);
        U_INTEGER = layout.offsetof(3);
        U_CARDINAL = layout.offsetof(4);
    }

    protected BDF_Property(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    /**
     * Creates a {@code BDF_Property} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public BDF_Property(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    /**
     * Returns a new {@code BDF_Property} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     */
    public static BDF_Property malloc() {
        return new BDF_Property(nmemAllocChecked(SIZEOF), null);
    }

    /**
     * Returns a new {@code BDF_Property} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     */
    public static BDF_Property calloc() {
        return new BDF_Property(nmemCallocChecked(1, SIZEOF), null);
    }

    /**
     * Returns a new {@code BDF_Property} instance allocated with {@link BufferUtils}.
     */
    public static BDF_Property create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new BDF_Property(memAddress(container), container);
    }

    /**
     * Returns a new {@code BDF_Property} instance for the specified memory address.
     */
    public static BDF_Property create(long address) {
        return new BDF_Property(address, null);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}.
     */
    @Nullable
    public static BDF_Property createSafe(long address) {
        return address == NULL ? null : new BDF_Property(address, null);
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

    // -----------------------------------

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

    /**
     * Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}.
     */
    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    /**
     * Returns a new {@code BDF_Property} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static BDF_Property malloc(MemoryStack stack) {
        return new BDF_Property(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code BDF_Property} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static BDF_Property calloc(MemoryStack stack) {
        return new BDF_Property(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    /**
     * Unsafe version of {@link #type}.
     */
    public static int ntype(long struct) {
        return UNSAFE.getInt(null, struct + BDF_Property.TYPE);
    }

    /**
     * Unsafe version of {@link #u_atom}.
     */
    @Nullable
    public static ByteBuffer nu_atom(long struct) {
        return memByteBufferNT1Safe(memGetAddress(struct + BDF_Property.U_ATOM));
    }

    /**
     * Unsafe version of {@link #u_atomString}.
     */
    @Nullable
    public static String nu_atomString(long struct) {
        return memASCIISafe(memGetAddress(struct + BDF_Property.U_ATOM));
    }

    /**
     * Unsafe version of {@link #u_integer}.
     */
    public static int nu_integer(long struct) {
        return UNSAFE.getInt(null, struct + BDF_Property.U_INTEGER);
    }

    /**
     * Unsafe version of {@link #u_cardinal}.
     */
    public static int nu_cardinal(long struct) {
        return UNSAFE.getInt(null, struct + BDF_Property.U_CARDINAL);
    }

    @Override
    protected BDF_Property create(long address, @Nullable ByteBuffer container) {
        return new BDF_Property(address, container);
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    // -----------------------------------

    /**
     * @return the value of the {@code type} field.
     */
    @NativeType("BDF_PropertyType")
    public int type() {
        return ntype(address());
    }

    /**
     * @return a {@link ByteBuffer} view of the null-terminated string pointed to by the {@code u.atom} field.
     */
    @Nullable
    @NativeType("char const *")
    public ByteBuffer u_atom() {
        return nu_atom(address());
    }

    /**
     * @return the null-terminated string pointed to by the {@code u.atom} field.
     */
    @Nullable
    @NativeType("char const *")
    public String u_atomString() {
        return nu_atomString(address());
    }

    /**
     * @return the value of the {@code u.integer} field.
     */
    @NativeType("FT_Int32")
    public int u_integer() {
        return nu_integer(address());
    }

    /**
     * @return the value of the {@code u.cardinal} field.
     */
    @NativeType("FT_UInt32")
    public int u_cardinal() {
        return nu_cardinal(address());
    }

    // -----------------------------------

    /**
     * An array of {@link BDF_Property} structs.
     */
    public static class Buffer extends StructBuffer<BDF_Property, Buffer> implements NativeResource {

        private static final BDF_Property ELEMENT_FACTORY = BDF_Property.create(-1L);

        /**
         * Creates a new {@code BDF_Property.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link BDF_Property#SIZEOF}, and its mark will be undefined.</p>
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
        protected BDF_Property getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /**
         * @return the value of the {@code type} field.
         */
        @NativeType("BDF_PropertyType")
        public int type() {
            return BDF_Property.ntype(address());
        }

        /**
         * @return a {@link ByteBuffer} view of the null-terminated string pointed to by the {@code u.atom} field.
         */
        @Nullable
        @NativeType("char const *")
        public ByteBuffer u_atom() {
            return BDF_Property.nu_atom(address());
        }

        /**
         * @return the null-terminated string pointed to by the {@code u.atom} field.
         */
        @Nullable
        @NativeType("char const *")
        public String u_atomString() {
            return BDF_Property.nu_atomString(address());
        }

        /**
         * @return the value of the {@code u.integer} field.
         */
        @NativeType("FT_Int32")
        public int u_integer() {
            return BDF_Property.nu_integer(address());
        }

        /**
         * @return the value of the {@code u.cardinal} field.
         */
        @NativeType("FT_UInt32")
        public int u_cardinal() {
            return BDF_Property.nu_cardinal(address());
        }

    }

}