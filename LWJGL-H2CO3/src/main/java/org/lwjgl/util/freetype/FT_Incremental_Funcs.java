/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.util.freetype;

import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memAddressSafe;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memGetAddress;
import static org.lwjgl.system.MemoryUtil.memPutAddress;
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
 * A table of functions for accessing fonts that load data incrementally. Used in {@link FT_Incremental_Interface}.
 *
 * <h3>Layout</h3>
 *
 * <pre><code>
 * struct FT_Incremental_FuncsRec {
 *     {@link FT_Incremental_GetGlyphDataFuncI FT_Incremental_GetGlyphDataFunc} get_glyph_data;
 *     {@link FT_Incremental_FreeGlyphDataFuncI FT_Incremental_FreeGlyphDataFunc} free_glyph_data;
 *     {@link FT_Incremental_GetGlyphMetricsFuncI FT_Incremental_GetGlyphMetricsFunc} get_glyph_metrics;
 * }</code></pre>
 */
@NativeType("struct FT_Incremental_FuncsRec")
public class FT_Incremental_Funcs extends Struct<FT_Incremental_Funcs> implements NativeResource {

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
            GET_GLYPH_DATA,
            FREE_GLYPH_DATA,
            GET_GLYPH_METRICS;

    static {
        Layout layout = __struct(
                __member(POINTER_SIZE),
                __member(POINTER_SIZE),
                __member(POINTER_SIZE)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        GET_GLYPH_DATA = layout.offsetof(0);
        FREE_GLYPH_DATA = layout.offsetof(1);
        GET_GLYPH_METRICS = layout.offsetof(2);
    }

    protected FT_Incremental_Funcs(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    /**
     * Creates a {@code FT_Incremental_Funcs} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public FT_Incremental_Funcs(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    /**
     * Returns a new {@code FT_Incremental_Funcs} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     */
    public static FT_Incremental_Funcs malloc() {
        return new FT_Incremental_Funcs(nmemAllocChecked(SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_Incremental_Funcs} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed.
     */
    public static FT_Incremental_Funcs calloc() {
        return new FT_Incremental_Funcs(nmemCallocChecked(1, SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_Incremental_Funcs} instance allocated with {@link BufferUtils}.
     */
    public static FT_Incremental_Funcs create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Incremental_Funcs(memAddress(container), container);
    }

    /**
     * Returns a new {@code FT_Incremental_Funcs} instance for the specified memory address.
     */
    public static FT_Incremental_Funcs create(long address) {
        return new FT_Incremental_Funcs(address, null);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}.
     */
    @Nullable
    public static FT_Incremental_Funcs createSafe(long address) {
        return address == NULL ? null : new FT_Incremental_Funcs(address, null);
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

    /**
     * Like {@link #create(long, int) create}, but returns {@code null} if {@code address} is {@code NULL}.
     */
    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : new Buffer(address, capacity);
    }

    // -----------------------------------

    /**
     * Returns a new {@code FT_Incremental_Funcs} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static FT_Incremental_Funcs malloc(MemoryStack stack) {
        return new FT_Incremental_Funcs(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code FT_Incremental_Funcs} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static FT_Incremental_Funcs calloc(MemoryStack stack) {
        return new FT_Incremental_Funcs(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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
     * Unsafe version of {@link #get_glyph_data}.
     */
    public static FT_Incremental_GetGlyphDataFunc nget_glyph_data(long struct) {
        return FT_Incremental_GetGlyphDataFunc.create(memGetAddress(struct + FT_Incremental_Funcs.GET_GLYPH_DATA));
    }

    /**
     * Unsafe version of {@link #free_glyph_data}.
     */
    public static FT_Incremental_FreeGlyphDataFunc nfree_glyph_data(long struct) {
        return FT_Incremental_FreeGlyphDataFunc.create(memGetAddress(struct + FT_Incremental_Funcs.FREE_GLYPH_DATA));
    }

    /**
     * Unsafe version of {@link #get_glyph_metrics}.
     */
    @Nullable
    public static FT_Incremental_GetGlyphMetricsFunc nget_glyph_metrics(long struct) {
        return FT_Incremental_GetGlyphMetricsFunc.createSafe(memGetAddress(struct + FT_Incremental_Funcs.GET_GLYPH_METRICS));
    }

    /**
     * Unsafe version of {@link #get_glyph_data(FT_Incremental_GetGlyphDataFuncI) get_glyph_data}.
     */
    public static void nget_glyph_data(long struct, FT_Incremental_GetGlyphDataFuncI value) {
        memPutAddress(struct + FT_Incremental_Funcs.GET_GLYPH_DATA, value.address());
    }

    /**
     * Unsafe version of {@link #free_glyph_data(FT_Incremental_FreeGlyphDataFuncI) free_glyph_data}.
     */
    public static void nfree_glyph_data(long struct, FT_Incremental_FreeGlyphDataFuncI value) {
        memPutAddress(struct + FT_Incremental_Funcs.FREE_GLYPH_DATA, value.address());
    }

    /**
     * Unsafe version of {@link #get_glyph_metrics(FT_Incremental_GetGlyphMetricsFuncI) get_glyph_metrics}.
     */
    public static void nget_glyph_metrics(long struct, @Nullable FT_Incremental_GetGlyphMetricsFuncI value) {
        memPutAddress(struct + FT_Incremental_Funcs.GET_GLYPH_METRICS, memAddressSafe(value));
    }

    /**
     * Validates pointer members that should not be {@code NULL}.
     *
     * @param struct the struct to validate
     */
    public static void validate(long struct) {
        check(memGetAddress(struct + FT_Incremental_Funcs.GET_GLYPH_DATA));
        check(memGetAddress(struct + FT_Incremental_Funcs.FREE_GLYPH_DATA));
    }

    @Override
    protected FT_Incremental_Funcs create(long address, @Nullable ByteBuffer container) {
        return new FT_Incremental_Funcs(address, container);
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    /**
     * @return the value of the {@code get_glyph_data} field.
     */
    public FT_Incremental_GetGlyphDataFunc get_glyph_data() {
        return nget_glyph_data(address());
    }

    // -----------------------------------

    /**
     * @return the value of the {@code free_glyph_data} field.
     */
    public FT_Incremental_FreeGlyphDataFunc free_glyph_data() {
        return nfree_glyph_data(address());
    }

    /**
     * @return the value of the {@code get_glyph_metrics} field.
     */
    @Nullable
    public FT_Incremental_GetGlyphMetricsFunc get_glyph_metrics() {
        return nget_glyph_metrics(address());
    }

    /**
     * Sets the specified value to the {@code get_glyph_data} field.
     */
    public FT_Incremental_Funcs get_glyph_data(@NativeType("FT_Incremental_GetGlyphDataFunc") FT_Incremental_GetGlyphDataFuncI value) {
        nget_glyph_data(address(), value);
        return this;
    }

    /**
     * Sets the specified value to the {@code free_glyph_data} field.
     */
    public FT_Incremental_Funcs free_glyph_data(@NativeType("FT_Incremental_FreeGlyphDataFunc") FT_Incremental_FreeGlyphDataFuncI value) {
        nfree_glyph_data(address(), value);
        return this;
    }

    /**
     * Sets the specified value to the {@code get_glyph_metrics} field.
     */
    public FT_Incremental_Funcs get_glyph_metrics(@Nullable @NativeType("FT_Incremental_GetGlyphMetricsFunc") FT_Incremental_GetGlyphMetricsFuncI value) {
        nget_glyph_metrics(address(), value);
        return this;
    }

    /**
     * Initializes this struct with the specified values.
     */
    public FT_Incremental_Funcs set(
            FT_Incremental_GetGlyphDataFuncI get_glyph_data,
            FT_Incremental_FreeGlyphDataFuncI free_glyph_data,
            FT_Incremental_GetGlyphMetricsFuncI get_glyph_metrics
    ) {
        get_glyph_data(get_glyph_data);
        free_glyph_data(free_glyph_data);
        get_glyph_metrics(get_glyph_metrics);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     * @return this struct
     */
    public FT_Incremental_Funcs set(FT_Incremental_Funcs src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /**
     * An array of {@link FT_Incremental_Funcs} structs.
     */
    public static class Buffer extends StructBuffer<FT_Incremental_Funcs, Buffer> implements NativeResource {

        private static final FT_Incremental_Funcs ELEMENT_FACTORY = FT_Incremental_Funcs.create(-1L);

        /**
         * Creates a new {@code FT_Incremental_Funcs.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link FT_Incremental_Funcs#SIZEOF}, and its mark will be undefined.</p>
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
        protected FT_Incremental_Funcs getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /**
         * @return the value of the {@code get_glyph_data} field.
         */
        public FT_Incremental_GetGlyphDataFunc get_glyph_data() {
            return FT_Incremental_Funcs.nget_glyph_data(address());
        }

        /**
         * @return the value of the {@code free_glyph_data} field.
         */
        public FT_Incremental_FreeGlyphDataFunc free_glyph_data() {
            return FT_Incremental_Funcs.nfree_glyph_data(address());
        }

        /**
         * @return the value of the {@code get_glyph_metrics} field.
         */
        @Nullable
        public FT_Incremental_GetGlyphMetricsFunc get_glyph_metrics() {
            return FT_Incremental_Funcs.nget_glyph_metrics(address());
        }

        /**
         * Sets the specified value to the {@code get_glyph_data} field.
         */
        public Buffer get_glyph_data(@NativeType("FT_Incremental_GetGlyphDataFunc") FT_Incremental_GetGlyphDataFuncI value) {
            FT_Incremental_Funcs.nget_glyph_data(address(), value);
            return this;
        }

        /**
         * Sets the specified value to the {@code free_glyph_data} field.
         */
        public Buffer free_glyph_data(@NativeType("FT_Incremental_FreeGlyphDataFunc") FT_Incremental_FreeGlyphDataFuncI value) {
            FT_Incremental_Funcs.nfree_glyph_data(address(), value);
            return this;
        }

        /**
         * Sets the specified value to the {@code get_glyph_metrics} field.
         */
        public Buffer get_glyph_metrics(@Nullable @NativeType("FT_Incremental_GetGlyphMetricsFunc") FT_Incremental_GetGlyphMetricsFuncI value) {
            FT_Incremental_Funcs.nget_glyph_metrics(address(), value);
            return this;
        }

    }

}