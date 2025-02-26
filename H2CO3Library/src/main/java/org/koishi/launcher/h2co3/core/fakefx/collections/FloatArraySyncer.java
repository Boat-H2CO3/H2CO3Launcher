package org.koishi.launcher.h2co3.core.fakefx.collections;

/**
 *
 */
public interface FloatArraySyncer {

    /**
     * This method is used to sync arrays on pulses. This method expects
     * the same array was synced before. The usage is similar to toArray method
     * so always use it as following: {@code dest = source.syncTo(dest);}
     *
     * @param array                previously synced array
     * @param fromAndLengthIndices an int array of 2 elements that states the
     *                             start and length of elements modified.
     * @return a synced array, which is the same or new array (depending on
     * the change).
     */
    float[] syncTo(float[] array, int[] fromAndLengthIndices);
}
