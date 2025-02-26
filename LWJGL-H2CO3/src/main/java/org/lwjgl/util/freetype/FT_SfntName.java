/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBuffer;
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
 * <h3>Layout</h3>
 *
 * <pre><code>
 * struct FT_SfntName {
 *     FT_UShort platform_id;
 *     FT_UShort encoding_id;
 *     FT_UShort language_id;
 *     FT_UShort name_id;
 *     FT_Byte * string;
 *     FT_UInt string_len;
 * }</code></pre>
 */
public class FT_SfntName extends Struct<FT_SfntName> implements NativeResource {

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
            PLATFORM_ID,
            ENCODING_ID,
            LANGUAGE_ID,
            NAME_ID,
            STRING,
            STRING_LEN;

    static {
        Layout layout = __struct(
                __member(2),
                __member(2),
                __member(2),
                __member(2),
                __member(POINTER_SIZE),
                __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        PLATFORM_ID = layout.offsetof(0);
        ENCODING_ID = layout.offsetof(1);
        LANGUAGE_ID = layout.offsetof(2);
        NAME_ID = layout.offsetof(3);
        STRING = layout.offsetof(4);
        STRING_LEN = layout.offsetof(5);
    }

    protected FT_SfntName(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    /**
     * Creates a {@code FT_SfntName} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public FT_SfntName(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    /**
     * Returns a new {@code FT_SfntName} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     */
    public static FT_SfntName malloc() {
        return new FT_SfntName(nmemAllocChecked(SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_SfntName} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     */
    public static FT_SfntName calloc() {
        return new FT_SfntName(nmemCallocChecked(1, SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_SfntName} instance allocated with {@link BufferUtils}.
     */
    public static FT_SfntName create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_SfntName(memAddress(container), container);
    }

    /**
     * Returns a new {@code FT_SfntName} instance for the specified memory address.
     */
    public static FT_SfntName create(long address) {
        return new FT_SfntName(address, null);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}.
     */
    @Nullable
    public static FT_SfntName createSafe(long address) {
        return address == NULL ? null : new FT_SfntName(address, null);
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

    // -----------------------------------

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
     * Returns a new {@code FT_SfntName} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static FT_SfntName malloc(MemoryStack stack) {
        return new FT_SfntName(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_SfntName} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static FT_SfntName calloc(MemoryStack stack) {
        return new FT_SfntName(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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
     * Unsafe version of {@link #platform_id}.
     */
    public static short nplatform_id(long struct) {
        return UNSAFE.getShort(null, struct + FT_SfntName.PLATFORM_ID);
    }

    /**
     * Unsafe version of {@link #encoding_id}.
     */
    public static short nencoding_id(long struct) {
        return UNSAFE.getShort(null, struct + FT_SfntName.ENCODING_ID);
    }

    /**
     * Unsafe version of {@link #language_id}.
     */
    public static short nlanguage_id(long struct) {
        return UNSAFE.getShort(null, struct + FT_SfntName.LANGUAGE_ID);
    }

    /**
     * Unsafe version of {@link #name_id}.
     */
    public static short nname_id(long struct) {
        return UNSAFE.getShort(null, struct + FT_SfntName.NAME_ID);
    }

    /**
     * Unsafe version of {@link #string() string}.
     */
    public static ByteBuffer nstring(long struct) {
        return memByteBuffer(memGetAddress(struct + FT_SfntName.STRING), nstring_len(struct));
    }

    /**
     * Unsafe version of {@link #string_len}.
     */
    public static int nstring_len(long struct) {
        return UNSAFE.getInt(null, struct + FT_SfntName.STRING_LEN);
    }

    @Override
    protected FT_SfntName create(long address, @Nullable ByteBuffer container) {
        return new FT_SfntName(address, container);
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    // -----------------------------------

    /**
     * @return the value of the {@code platform_id} field.
     */
    @NativeType("FT_UShort")
    public short platform_id() {
        return nplatform_id(address());
    }

    /**
     * @return the value of the {@code encoding_id} field.
     */
    @NativeType("FT_UShort")
    public short encoding_id() {
        return nencoding_id(address());
    }

    /**
     * @return the value of the {@code language_id} field.
     */
    @NativeType("FT_UShort")
    public short language_id() {
        return nlanguage_id(address());
    }

    /**
     * @return the value of the {@code name_id} field.
     */
    @NativeType("FT_UShort")
    public short name_id() {
        return nname_id(address());
    }

    /**
     * @return a {@link ByteBuffer} view of the data pointed to by the {@code string} field.
     */
    @NativeType("FT_Byte *")
    public ByteBuffer string() {
        return nstring(address());
    }

    /**
     * @return the value of the {@code string_len} field.
     */
    @NativeType("FT_UInt")
    public int string_len() {
        return nstring_len(address());
    }

    // -----------------------------------

    /**
     * An array of {@link FT_SfntName} structs.
     */
    public static class Buffer extends StructBuffer<FT_SfntName, Buffer> implements NativeResource {

        private static final FT_SfntName ELEMENT_FACTORY = FT_SfntName.create(-1L);

        /**
         * Creates a new {@code FT_SfntName.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link FT_SfntName#SIZEOF}, and its mark will be undefined.</p>
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
        protected FT_SfntName getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /**
         * @return the value of the {@code platform_id} field.
         */
        @NativeType("FT_UShort")
        public short platform_id() {
            return FT_SfntName.nplatform_id(address());
        }

        /**
         * @return the value of the {@code encoding_id} field.
         */
        @NativeType("FT_UShort")
        public short encoding_id() {
            return FT_SfntName.nencoding_id(address());
        }

        /**
         * @return the value of the {@code language_id} field.
         */
        @NativeType("FT_UShort")
        public short language_id() {
            return FT_SfntName.nlanguage_id(address());
        }

        /**
         * @return the value of the {@code name_id} field.
         */
        @NativeType("FT_UShort")
        public short name_id() {
            return FT_SfntName.nname_id(address());
        }

        /**
         * @return a {@link ByteBuffer} view of the data pointed to by the {@code string} field.
         */
        @NativeType("FT_Byte *")
        public ByteBuffer string() {
            return FT_SfntName.nstring(address());
        }

        /**
         * @return the value of the {@code string_len} field.
         */
        @NativeType("FT_UInt")
        public int string_len() {
            return FT_SfntName.nstring_len(address());
        }

    }

}