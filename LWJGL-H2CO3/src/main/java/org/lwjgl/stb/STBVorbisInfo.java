/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.stb;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
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
 * Information about a Vorbis stream.
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct stb_vorbis_info {
 *     unsigned int sample_rate;
 *     int channels;
 *     unsigned int setup_memory_required;
 *     unsigned int setup_temp_memory_required;
 *     unsigned int temp_memory_required;
 *     int max_frame_size;
 * }</code></pre>
 */
@NativeType("struct stb_vorbis_info")
public class STBVorbisInfo extends Struct<STBVorbisInfo> implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        SAMPLE_RATE,
        CHANNELS,
        SETUP_MEMORY_REQUIRED,
        SETUP_TEMP_MEMORY_REQUIRED,
        TEMP_MEMORY_REQUIRED,
        MAX_FRAME_SIZE;

    static {
        Layout layout = __struct(
            __member(4),
            __member(4),
            __member(4),
            __member(4),
            __member(4),
            __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        SAMPLE_RATE = layout.offsetof(0);
        CHANNELS = layout.offsetof(1);
        SETUP_MEMORY_REQUIRED = layout.offsetof(2);
        SETUP_TEMP_MEMORY_REQUIRED = layout.offsetof(3);
        TEMP_MEMORY_REQUIRED = layout.offsetof(4);
        MAX_FRAME_SIZE = layout.offsetof(5);
    }

    protected STBVorbisInfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBVorbisInfo create(long address, @Nullable ByteBuffer container) {
        return new STBVorbisInfo(address, container);
    }

    /**
     * Creates a {@code STBVorbisInfo} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public STBVorbisInfo(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** @return the value of the {@code sample_rate} field. */
    @NativeType("unsigned int")
    public int sample_rate() { return nsample_rate(address()); }
    /** @return the value of the {@code channels} field. */
    public int channels() { return nchannels(address()); }
    /** @return the value of the {@code setup_memory_required} field. */
    @NativeType("unsigned int")
    public int setup_memory_required() { return nsetup_memory_required(address()); }
    /** @return the value of the {@code setup_temp_memory_required} field. */
    @NativeType("unsigned int")
    public int setup_temp_memory_required() { return nsetup_temp_memory_required(address()); }
    /** @return the value of the {@code temp_memory_required} field. */
    @NativeType("unsigned int")
    public int temp_memory_required() { return ntemp_memory_required(address()); }
    /** @return the value of the {@code max_frame_size} field. */
    public int max_frame_size() { return nmax_frame_size(address()); }

    // -----------------------------------

    /** Returns a new {@code STBVorbisInfo} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static STBVorbisInfo malloc() {
        return new STBVorbisInfo(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code STBVorbisInfo} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static STBVorbisInfo calloc() {
        return new STBVorbisInfo(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code STBVorbisInfo} instance allocated with {@link BufferUtils}. */
    public static STBVorbisInfo create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBVorbisInfo(memAddress(container), container);
    }

    /** Returns a new {@code STBVorbisInfo} instance for the specified memory address. */
    public static STBVorbisInfo create(long address) {
        return new STBVorbisInfo(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static STBVorbisInfo createSafe(long address) {
        return address == NULL ? null : new STBVorbisInfo(address, null);
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
    @Deprecated public static STBVorbisInfo mallocStack() { return malloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static STBVorbisInfo callocStack() { return calloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static STBVorbisInfo mallocStack(MemoryStack stack) { return malloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static STBVorbisInfo callocStack(MemoryStack stack) { return calloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity) { return malloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity) { return calloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity, MemoryStack stack) { return malloc(capacity, stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity, MemoryStack stack) { return calloc(capacity, stack); }

    /**
     * Returns a new {@code STBVorbisInfo} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static STBVorbisInfo malloc(MemoryStack stack) {
        return new STBVorbisInfo(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code STBVorbisInfo} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static STBVorbisInfo calloc(MemoryStack stack) {
        return new STBVorbisInfo(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    /** Unsafe version of {@link #sample_rate}. */
    public static int nsample_rate(long struct) { return UNSAFE.getInt(null, struct + STBVorbisInfo.SAMPLE_RATE); }
    /** Unsafe version of {@link #channels}. */
    public static int nchannels(long struct) { return UNSAFE.getInt(null, struct + STBVorbisInfo.CHANNELS); }
    /** Unsafe version of {@link #setup_memory_required}. */
    public static int nsetup_memory_required(long struct) { return UNSAFE.getInt(null, struct + STBVorbisInfo.SETUP_MEMORY_REQUIRED); }
    /** Unsafe version of {@link #setup_temp_memory_required}. */
    public static int nsetup_temp_memory_required(long struct) { return UNSAFE.getInt(null, struct + STBVorbisInfo.SETUP_TEMP_MEMORY_REQUIRED); }
    /** Unsafe version of {@link #temp_memory_required}. */
    public static int ntemp_memory_required(long struct) { return UNSAFE.getInt(null, struct + STBVorbisInfo.TEMP_MEMORY_REQUIRED); }
    /** Unsafe version of {@link #max_frame_size}. */
    public static int nmax_frame_size(long struct) { return UNSAFE.getInt(null, struct + STBVorbisInfo.MAX_FRAME_SIZE); }

    // -----------------------------------

    /** An array of {@link STBVorbisInfo} structs. */
    public static class Buffer extends StructBuffer<STBVorbisInfo, Buffer> implements NativeResource {

        private static final STBVorbisInfo ELEMENT_FACTORY = STBVorbisInfo.create(-1L);

        /**
         * Creates a new {@code STBVorbisInfo.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link STBVorbisInfo#SIZEOF}, and its mark will be undefined.</p>
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
        protected STBVorbisInfo getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return the value of the {@code sample_rate} field. */
        @NativeType("unsigned int")
        public int sample_rate() { return STBVorbisInfo.nsample_rate(address()); }
        /** @return the value of the {@code channels} field. */
        public int channels() { return STBVorbisInfo.nchannels(address()); }
        /** @return the value of the {@code setup_memory_required} field. */
        @NativeType("unsigned int")
        public int setup_memory_required() { return STBVorbisInfo.nsetup_memory_required(address()); }
        /** @return the value of the {@code setup_temp_memory_required} field. */
        @NativeType("unsigned int")
        public int setup_temp_memory_required() { return STBVorbisInfo.nsetup_temp_memory_required(address()); }
        /** @return the value of the {@code temp_memory_required} field. */
        @NativeType("unsigned int")
        public int temp_memory_required() { return STBVorbisInfo.ntemp_memory_required(address()); }
        /** @return the value of the {@code max_frame_size} field. */
        public int max_frame_size() { return STBVorbisInfo.nmax_frame_size(address()); }

    }

}