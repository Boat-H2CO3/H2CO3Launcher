/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.system.windows;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
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
 * Contains message information from a thread's message queue.
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct MSG {
 *     HWND {@link #hwnd};
 *     UINT {@link #message};
 *     WPARAM {@link #wParam};
 *     LPARAM {@link #lParam};
 *     DWORD {@link #time};
 *     {@link POINT POINT} {@link #pt};
 * }</code></pre>
 */
public class MSG extends Struct<MSG> implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        HWND,
        MESSAGE,
        WPARAM,
        LPARAM,
        TIME,
        PT;

    static {
        Layout layout = __struct(
            __member(POINTER_SIZE),
            __member(4),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE),
            __member(4),
            __member(POINT.SIZEOF, POINT.ALIGNOF)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        HWND = layout.offsetof(0);
        MESSAGE = layout.offsetof(1);
        WPARAM = layout.offsetof(2);
        LPARAM = layout.offsetof(3);
        TIME = layout.offsetof(4);
        PT = layout.offsetof(5);
    }

    protected MSG(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected MSG create(long address, @Nullable ByteBuffer container) {
        return new MSG(address, container);
    }

    /**
     * Creates a {@code MSG} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public MSG(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** a handle to the window whose window procedure receives the message. This member is {@code NULL} when the message is a thread message. */
    @NativeType("HWND")
    public long hwnd() { return nhwnd(address()); }
    /** the message identifier. Applications can only use the low word; the high word is reserved by the system. */
    @NativeType("UINT")
    public int message() { return nmessage(address()); }
    /** additional information about the message. The exact meaning depends on the value of the message member. */
    @NativeType("WPARAM")
    public long wParam() { return nwParam(address()); }
    /** additional information about the message. The exact meaning depends on the value of the message member. */
    @NativeType("LPARAM")
    public long lParam() { return nlParam(address()); }
    /** the time at which the message was posted */
    @NativeType("DWORD")
    public int time() { return ntime(address()); }
    /** the cursor position, in screen coordinates, when the message was posted. */
    public POINT pt() { return npt(address()); }

    /** Sets the specified value to the {@link #hwnd} field. */
    public MSG hwnd(@NativeType("HWND") long value) { nhwnd(address(), value); return this; }
    /** Sets the specified value to the {@link #message} field. */
    public MSG message(@NativeType("UINT") int value) { nmessage(address(), value); return this; }
    /** Sets the specified value to the {@link #wParam} field. */
    public MSG wParam(@NativeType("WPARAM") long value) { nwParam(address(), value); return this; }
    /** Sets the specified value to the {@link #lParam} field. */
    public MSG lParam(@NativeType("LPARAM") long value) { nlParam(address(), value); return this; }
    /** Sets the specified value to the {@link #time} field. */
    public MSG time(@NativeType("DWORD") int value) { ntime(address(), value); return this; }
    /** Copies the specified {@link POINT} to the {@link #pt} field. */
    public MSG pt(POINT value) { npt(address(), value); return this; }
    /** Passes the {@link #pt} field to the specified {@link java.util.function.Consumer Consumer}. */
    public MSG pt(java.util.function.Consumer<POINT> consumer) { consumer.accept(pt()); return this; }

    /** Initializes this struct with the specified values. */
    public MSG set(
        long hwnd,
        int message,
        long wParam,
        long lParam,
        int time,
        POINT pt
    ) {
        hwnd(hwnd);
        message(message);
        wParam(wParam);
        lParam(lParam);
        time(time);
        pt(pt);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public MSG set(MSG src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code MSG} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static MSG malloc() {
        return new MSG(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code MSG} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static MSG calloc() {
        return new MSG(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code MSG} instance allocated with {@link BufferUtils}. */
    public static MSG create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new MSG(memAddress(container), container);
    }

    /** Returns a new {@code MSG} instance for the specified memory address. */
    public static MSG create(long address) {
        return new MSG(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static MSG createSafe(long address) {
        return address == NULL ? null : new MSG(address, null);
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
    @Deprecated public static MSG mallocStack() { return malloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static MSG callocStack() { return calloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static MSG mallocStack(MemoryStack stack) { return malloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static MSG callocStack(MemoryStack stack) { return calloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity) { return malloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity) { return calloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity, MemoryStack stack) { return malloc(capacity, stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity, MemoryStack stack) { return calloc(capacity, stack); }

    /**
     * Returns a new {@code MSG} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static MSG malloc(MemoryStack stack) {
        return new MSG(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code MSG} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static MSG calloc(MemoryStack stack) {
        return new MSG(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    /** Unsafe version of {@link #hwnd}. */
    public static long nhwnd(long struct) { return memGetAddress(struct + MSG.HWND); }
    /** Unsafe version of {@link #message}. */
    public static int nmessage(long struct) { return UNSAFE.getInt(null, struct + MSG.MESSAGE); }
    /** Unsafe version of {@link #wParam}. */
    public static long nwParam(long struct) { return memGetAddress(struct + MSG.WPARAM); }
    /** Unsafe version of {@link #lParam}. */
    public static long nlParam(long struct) { return memGetAddress(struct + MSG.LPARAM); }
    /** Unsafe version of {@link #time}. */
    public static int ntime(long struct) { return UNSAFE.getInt(null, struct + MSG.TIME); }
    /** Unsafe version of {@link #pt}. */
    public static POINT npt(long struct) { return POINT.create(struct + MSG.PT); }

    /** Unsafe version of {@link #hwnd(long) hwnd}. */
    public static void nhwnd(long struct, long value) { memPutAddress(struct + MSG.HWND, value); }
    /** Unsafe version of {@link #message(int) message}. */
    public static void nmessage(long struct, int value) { UNSAFE.putInt(null, struct + MSG.MESSAGE, value); }
    /** Unsafe version of {@link #wParam(long) wParam}. */
    public static void nwParam(long struct, long value) { memPutAddress(struct + MSG.WPARAM, value); }
    /** Unsafe version of {@link #lParam(long) lParam}. */
    public static void nlParam(long struct, long value) { memPutAddress(struct + MSG.LPARAM, value); }
    /** Unsafe version of {@link #time(int) time}. */
    public static void ntime(long struct, int value) { UNSAFE.putInt(null, struct + MSG.TIME, value); }
    /** Unsafe version of {@link #pt(POINT) pt}. */
    public static void npt(long struct, POINT value) { memCopy(value.address(), struct + MSG.PT, POINT.SIZEOF); }

    // -----------------------------------

    /** An array of {@link MSG} structs. */
    public static class Buffer extends StructBuffer<MSG, Buffer> implements NativeResource {

        private static final MSG ELEMENT_FACTORY = MSG.create(-1L);

        /**
         * Creates a new {@code MSG.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link MSG#SIZEOF}, and its mark will be undefined.</p>
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
        protected MSG getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return the value of the {@link MSG#hwnd} field. */
        @NativeType("HWND")
        public long hwnd() { return MSG.nhwnd(address()); }
        /** @return the value of the {@link MSG#message} field. */
        @NativeType("UINT")
        public int message() { return MSG.nmessage(address()); }
        /** @return the value of the {@link MSG#wParam} field. */
        @NativeType("WPARAM")
        public long wParam() { return MSG.nwParam(address()); }
        /** @return the value of the {@link MSG#lParam} field. */
        @NativeType("LPARAM")
        public long lParam() { return MSG.nlParam(address()); }
        /** @return the value of the {@link MSG#time} field. */
        @NativeType("DWORD")
        public int time() { return MSG.ntime(address()); }
        /** @return a {@link POINT} view of the {@link MSG#pt} field. */
        public POINT pt() { return MSG.npt(address()); }

        /** Sets the specified value to the {@link MSG#hwnd} field. */
        public Buffer hwnd(@NativeType("HWND") long value) { MSG.nhwnd(address(), value); return this; }
        /** Sets the specified value to the {@link MSG#message} field. */
        public Buffer message(@NativeType("UINT") int value) { MSG.nmessage(address(), value); return this; }
        /** Sets the specified value to the {@link MSG#wParam} field. */
        public Buffer wParam(@NativeType("WPARAM") long value) { MSG.nwParam(address(), value); return this; }
        /** Sets the specified value to the {@link MSG#lParam} field. */
        public Buffer lParam(@NativeType("LPARAM") long value) { MSG.nlParam(address(), value); return this; }
        /** Sets the specified value to the {@link MSG#time} field. */
        public Buffer time(@NativeType("DWORD") int value) { MSG.ntime(address(), value); return this; }
        /** Copies the specified {@link POINT} to the {@link MSG#pt} field. */
        public Buffer pt(POINT value) { MSG.npt(address(), value); return this; }
        /** Passes the {@link MSG#pt} field to the specified {@link java.util.function.Consumer Consumer}. */
        public Buffer pt(java.util.function.Consumer<POINT> consumer) { consumer.accept(pt()); return this; }

    }

}