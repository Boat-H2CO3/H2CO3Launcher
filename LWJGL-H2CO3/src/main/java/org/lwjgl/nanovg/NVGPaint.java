/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * MACHINE GENERATED FILE, DO NOT EDIT
 */
package org.lwjgl.nanovg;

import static org.lwjgl.system.Checks.CHECKS;
import static org.lwjgl.system.Checks.check;
import static org.lwjgl.system.Checks.checkGT;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.memFloatBuffer;
import static org.lwjgl.system.MemoryUtil.nmemAllocChecked;
import static org.lwjgl.system.MemoryUtil.nmemCallocChecked;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.annotation.Nullable;

/**
 * A NanoVG paint.
 * 
 * <h3>Layout</h3>
 * 
 * <pre><code>
 * struct NVGpaint {
 *     float {@link #xform}[6];
 *     float {@link #extent}[2];
 *     float {@link #radius};
 *     float {@link #feather};
 *     {@link NVGColor NVGcolor} {@link #innerColor};
 *     {@link NVGColor NVGcolor} {@link #outerColor};
 *     int {@link #image};
 * }</code></pre>
 */
@NativeType("struct NVGpaint")
public class NVGPaint extends Struct<NVGPaint> implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int
        XFORM,
        EXTENT,
        RADIUS,
        FEATHER,
        INNERCOLOR,
        OUTERCOLOR,
        IMAGE;

    static {
        Layout layout = __struct(
            __array(4, 6),
            __array(4, 2),
            __member(4),
            __member(4),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(4)
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        XFORM = layout.offsetof(0);
        EXTENT = layout.offsetof(1);
        RADIUS = layout.offsetof(2);
        FEATHER = layout.offsetof(3);
        INNERCOLOR = layout.offsetof(4);
        OUTERCOLOR = layout.offsetof(5);
        IMAGE = layout.offsetof(6);
    }

    protected NVGPaint(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected NVGPaint create(long address, @Nullable ByteBuffer container) {
        return new NVGPaint(address, container);
    }

    /**
     * Creates a {@code NVGPaint} instance at the current position of the specified {@link ByteBuffer} container. Changes to the buffer's content will be
     * visible to the struct instance and vice versa.
     *
     * <p>The created instance holds a strong reference to the container object.</p>
     */
    public NVGPaint(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() { return SIZEOF; }

    /** the transformation matrix */
    @NativeType("float[6]")
    public FloatBuffer xform() { return nxform(address()); }
    /** the transformation matrix */
    public float xform(int index) { return nxform(address(), index); }
    /** the extent */
    @NativeType("float[2]")
    public FloatBuffer extent() { return nextent(address()); }
    /** the extent */
    public float extent(int index) { return nextent(address(), index); }
    /** the radius */
    public float radius() { return nradius(address()); }
    /** the feather amount */
    public float feather() { return nfeather(address()); }
    /** the inner color */
    @NativeType("NVGcolor")
    public NVGColor innerColor() { return ninnerColor(address()); }
    /** the outer color */
    @NativeType("NVGcolor")
    public NVGColor outerColor() { return nouterColor(address()); }
    /** the image handle */
    public int image() { return nimage(address()); }

    /** Copies the specified {@link FloatBuffer} to the {@link #xform} field. */
    public NVGPaint xform(@NativeType("float[6]") FloatBuffer value) { nxform(address(), value); return this; }
    /** Sets the specified value at the specified index of the {@link #xform} field. */
    public NVGPaint xform(int index, float value) { nxform(address(), index, value); return this; }
    /** Copies the specified {@link FloatBuffer} to the {@link #extent} field. */
    public NVGPaint extent(@NativeType("float[2]") FloatBuffer value) { nextent(address(), value); return this; }
    /** Sets the specified value at the specified index of the {@link #extent} field. */
    public NVGPaint extent(int index, float value) { nextent(address(), index, value); return this; }
    /** Sets the specified value to the {@link #radius} field. */
    public NVGPaint radius(float value) { nradius(address(), value); return this; }
    /** Sets the specified value to the {@link #feather} field. */
    public NVGPaint feather(float value) { nfeather(address(), value); return this; }
    /** Copies the specified {@link NVGColor} to the {@link #innerColor} field. */
    public NVGPaint innerColor(@NativeType("NVGcolor") NVGColor value) { ninnerColor(address(), value); return this; }
    /** Passes the {@link #innerColor} field to the specified {@link java.util.function.Consumer Consumer}. */
    public NVGPaint innerColor(java.util.function.Consumer<NVGColor> consumer) { consumer.accept(innerColor()); return this; }
    /** Copies the specified {@link NVGColor} to the {@link #outerColor} field. */
    public NVGPaint outerColor(@NativeType("NVGcolor") NVGColor value) { nouterColor(address(), value); return this; }
    /** Passes the {@link #outerColor} field to the specified {@link java.util.function.Consumer Consumer}. */
    public NVGPaint outerColor(java.util.function.Consumer<NVGColor> consumer) { consumer.accept(outerColor()); return this; }
    /** Sets the specified value to the {@link #image} field. */
    public NVGPaint image(int value) { nimage(address(), value); return this; }

    /** Initializes this struct with the specified values. */
    public NVGPaint set(
        FloatBuffer xform,
        FloatBuffer extent,
        float radius,
        float feather,
        NVGColor innerColor,
        NVGColor outerColor,
        int image
    ) {
        xform(xform);
        extent(extent);
        radius(radius);
        feather(feather);
        innerColor(innerColor);
        outerColor(outerColor);
        image(image);

        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     *
     * @return this struct
     */
    public NVGPaint set(NVGPaint src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    // -----------------------------------

    /** Returns a new {@code NVGPaint} instance allocated with {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed. */
    public static NVGPaint malloc() {
        return new NVGPaint(nmemAllocChecked(SIZEOF), null);
    }

    /** Returns a new {@code NVGPaint} instance allocated with {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly freed. */
    public static NVGPaint calloc() {
        return new NVGPaint(nmemCallocChecked(1, SIZEOF), null);
    }

    /** Returns a new {@code NVGPaint} instance allocated with {@link BufferUtils}. */
    public static NVGPaint create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new NVGPaint(memAddress(container), container);
    }

    /** Returns a new {@code NVGPaint} instance for the specified memory address. */
    public static NVGPaint create(long address) {
        return new NVGPaint(address, null);
    }

    /** Like {@link #create(long) create}, but returns {@code null} if {@code address} is {@code NULL}. */
    @Nullable
    public static NVGPaint createSafe(long address) {
        return address == NULL ? null : new NVGPaint(address, null);
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
    @Deprecated public static NVGPaint mallocStack() { return malloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static NVGPaint callocStack() { return calloc(stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead. */
    @Deprecated public static NVGPaint mallocStack(MemoryStack stack) { return malloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead. */
    @Deprecated public static NVGPaint callocStack(MemoryStack stack) { return calloc(stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity) { return malloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity) { return calloc(capacity, stackGet()); }
    /** Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer mallocStack(int capacity, MemoryStack stack) { return malloc(capacity, stack); }
    /** Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)} instead. */
    @Deprecated public static Buffer callocStack(int capacity, MemoryStack stack) { return calloc(capacity, stack); }

    /**
     * Returns a new {@code NVGPaint} instance allocated on the specified {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static NVGPaint malloc(MemoryStack stack) {
        return new NVGPaint(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    /**
     * Returns a new {@code NVGPaint} instance allocated on the specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static NVGPaint calloc(MemoryStack stack) {
        return new NVGPaint(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    /** Unsafe version of {@link #xform}. */
    public static FloatBuffer nxform(long struct) { return memFloatBuffer(struct + NVGPaint.XFORM, 6); }
    /** Unsafe version of {@link #xform(int) xform}. */
    public static float nxform(long struct, int index) {
        return UNSAFE.getFloat(null, struct + NVGPaint.XFORM + check(index, 6) * 4);
    }
    /** Unsafe version of {@link #extent}. */
    public static FloatBuffer nextent(long struct) { return memFloatBuffer(struct + NVGPaint.EXTENT, 2); }
    /** Unsafe version of {@link #extent(int) extent}. */
    public static float nextent(long struct, int index) {
        return UNSAFE.getFloat(null, struct + NVGPaint.EXTENT + check(index, 2) * 4);
    }
    /** Unsafe version of {@link #radius}. */
    public static float nradius(long struct) { return UNSAFE.getFloat(null, struct + NVGPaint.RADIUS); }
    /** Unsafe version of {@link #feather}. */
    public static float nfeather(long struct) { return UNSAFE.getFloat(null, struct + NVGPaint.FEATHER); }
    /** Unsafe version of {@link #innerColor}. */
    public static NVGColor ninnerColor(long struct) { return NVGColor.create(struct + NVGPaint.INNERCOLOR); }
    /** Unsafe version of {@link #outerColor}. */
    public static NVGColor nouterColor(long struct) { return NVGColor.create(struct + NVGPaint.OUTERCOLOR); }
    /** Unsafe version of {@link #image}. */
    public static int nimage(long struct) { return UNSAFE.getInt(null, struct + NVGPaint.IMAGE); }

    /** Unsafe version of {@link #xform(FloatBuffer) xform}. */
    public static void nxform(long struct, FloatBuffer value) {
        if (CHECKS) { checkGT(value, 6); }
        memCopy(memAddress(value), struct + NVGPaint.XFORM, value.remaining() * 4);
    }
    /** Unsafe version of {@link #xform(int, float) xform}. */
    public static void nxform(long struct, int index, float value) {
        UNSAFE.putFloat(null, struct + NVGPaint.XFORM + check(index, 6) * 4, value);
    }
    /** Unsafe version of {@link #extent(FloatBuffer) extent}. */
    public static void nextent(long struct, FloatBuffer value) {
        if (CHECKS) { checkGT(value, 2); }
        memCopy(memAddress(value), struct + NVGPaint.EXTENT, value.remaining() * 4);
    }
    /** Unsafe version of {@link #extent(int, float) extent}. */
    public static void nextent(long struct, int index, float value) {
        UNSAFE.putFloat(null, struct + NVGPaint.EXTENT + check(index, 2) * 4, value);
    }
    /** Unsafe version of {@link #radius(float) radius}. */
    public static void nradius(long struct, float value) { UNSAFE.putFloat(null, struct + NVGPaint.RADIUS, value); }
    /** Unsafe version of {@link #feather(float) feather}. */
    public static void nfeather(long struct, float value) { UNSAFE.putFloat(null, struct + NVGPaint.FEATHER, value); }
    /** Unsafe version of {@link #innerColor(NVGColor) innerColor}. */
    public static void ninnerColor(long struct, NVGColor value) { memCopy(value.address(), struct + NVGPaint.INNERCOLOR, NVGColor.SIZEOF); }
    /** Unsafe version of {@link #outerColor(NVGColor) outerColor}. */
    public static void nouterColor(long struct, NVGColor value) { memCopy(value.address(), struct + NVGPaint.OUTERCOLOR, NVGColor.SIZEOF); }
    /** Unsafe version of {@link #image(int) image}. */
    public static void nimage(long struct, int value) { UNSAFE.putInt(null, struct + NVGPaint.IMAGE, value); }

    // -----------------------------------

    /** An array of {@link NVGPaint} structs. */
    public static class Buffer extends StructBuffer<NVGPaint, Buffer> implements NativeResource {

        private static final NVGPaint ELEMENT_FACTORY = NVGPaint.create(-1L);

        /**
         * Creates a new {@code NVGPaint.Buffer} instance backed by the specified container.
         *
         * <p>Changes to the container's content will be visible to the struct buffer instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and its limit will be the number of bytes remaining in this buffer divided
         * by {@link NVGPaint#SIZEOF}, and its mark will be undefined.</p>
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
        protected NVGPaint getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /** @return a {@link FloatBuffer} view of the {@link NVGPaint#xform} field. */
        @NativeType("float[6]")
        public FloatBuffer xform() { return NVGPaint.nxform(address()); }
        /** @return the value at the specified index of the {@link NVGPaint#xform} field. */
        public float xform(int index) { return NVGPaint.nxform(address(), index); }
        /** @return a {@link FloatBuffer} view of the {@link NVGPaint#extent} field. */
        @NativeType("float[2]")
        public FloatBuffer extent() { return NVGPaint.nextent(address()); }
        /** @return the value at the specified index of the {@link NVGPaint#extent} field. */
        public float extent(int index) { return NVGPaint.nextent(address(), index); }
        /** @return the value of the {@link NVGPaint#radius} field. */
        public float radius() { return NVGPaint.nradius(address()); }
        /** @return the value of the {@link NVGPaint#feather} field. */
        public float feather() { return NVGPaint.nfeather(address()); }
        /** @return a {@link NVGColor} view of the {@link NVGPaint#innerColor} field. */
        @NativeType("NVGcolor")
        public NVGColor innerColor() { return NVGPaint.ninnerColor(address()); }
        /** @return a {@link NVGColor} view of the {@link NVGPaint#outerColor} field. */
        @NativeType("NVGcolor")
        public NVGColor outerColor() { return NVGPaint.nouterColor(address()); }
        /** @return the value of the {@link NVGPaint#image} field. */
        public int image() { return NVGPaint.nimage(address()); }

        /** Copies the specified {@link FloatBuffer} to the {@link NVGPaint#xform} field. */
        public Buffer xform(@NativeType("float[6]") FloatBuffer value) { NVGPaint.nxform(address(), value); return this; }
        /** Sets the specified value at the specified index of the {@link NVGPaint#xform} field. */
        public Buffer xform(int index, float value) { NVGPaint.nxform(address(), index, value); return this; }
        /** Copies the specified {@link FloatBuffer} to the {@link NVGPaint#extent} field. */
        public Buffer extent(@NativeType("float[2]") FloatBuffer value) { NVGPaint.nextent(address(), value); return this; }
        /** Sets the specified value at the specified index of the {@link NVGPaint#extent} field. */
        public Buffer extent(int index, float value) { NVGPaint.nextent(address(), index, value); return this; }
        /** Sets the specified value to the {@link NVGPaint#radius} field. */
        public Buffer radius(float value) { NVGPaint.nradius(address(), value); return this; }
        /** Sets the specified value to the {@link NVGPaint#feather} field. */
        public Buffer feather(float value) { NVGPaint.nfeather(address(), value); return this; }
        /** Copies the specified {@link NVGColor} to the {@link NVGPaint#innerColor} field. */
        public Buffer innerColor(@NativeType("NVGcolor") NVGColor value) { NVGPaint.ninnerColor(address(), value); return this; }
        /** Passes the {@link NVGPaint#innerColor} field to the specified {@link java.util.function.Consumer Consumer}. */
        public Buffer innerColor(java.util.function.Consumer<NVGColor> consumer) { consumer.accept(innerColor()); return this; }
        /** Copies the specified {@link NVGColor} to the {@link NVGPaint#outerColor} field. */
        public Buffer outerColor(@NativeType("NVGcolor") NVGColor value) { NVGPaint.nouterColor(address(), value); return this; }
        /** Passes the {@link NVGPaint#outerColor} field to the specified {@link java.util.function.Consumer Consumer}. */
        public Buffer outerColor(java.util.function.Consumer<NVGColor> consumer) { consumer.accept(outerColor()); return this; }
        /** Sets the specified value to the {@link NVGPaint#image} field. */
        public Buffer image(int value) { NVGPaint.nimage(address(), value); return this; }

    }

}