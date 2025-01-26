package org.koishi.launcher.h2co3core.util.png.fakefx;

import android.graphics.Bitmap;

import org.koishi.launcher.h2co3core.util.png.PNGType;
import org.koishi.launcher.h2co3core.util.png.PNGWriter;
import org.koishi.launcher.h2co3core.util.png.image.ArgbImageWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PNGFakeFXUtils {
    private PNGFakeFXUtils() {
    }

    public static ArgbImageWrapper<Bitmap> asArgbImage(Bitmap image) {
        return new ArgbImageWrapper<Bitmap>(image, (int) image.getWidth(), (int) image.getHeight()) {
            @Override
            public int getArgb(int x, int y) {
                return image.getPixel(x, y);
            }
        };
    }

    public static void writeImage(Bitmap image, Path file) throws IOException {
        writeImage(image, Files.newOutputStream(file));
    }

    public static void writeImage(Bitmap image, Path file, PNGType type) throws IOException {
        writeImage(image, Files.newOutputStream(file), type);
    }

    public static void writeImage(Bitmap image, Path file, PNGType type, int compressLevel) throws IOException {
        writeImage(image, Files.newOutputStream(file), type, compressLevel);
    }

    public static byte[] writeImageToArray(Bitmap image) {
        return writeImageToArray(image, PNGType.RGBA, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    public static byte[] writeImageToArray(Bitmap image, PNGType type) {
        return writeImageToArray(image, type, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    public static byte[] writeImageToArray(Bitmap image, PNGType type, int compressLevel) {
        int estimatedSize = (int) (
                image.getWidth() * image.getHeight()
                        * (type == PNGType.RGB ? 3 : 4)
                        + 32);

        if (compressLevel != 1) {
            estimatedSize /= 2;
        }

        try {
            ByteArrayOutputStream temp = new ByteArrayOutputStream(Integer.max(estimatedSize, 32));
            writeImage(image, temp, type, compressLevel);
            return temp.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeImage(Bitmap image, OutputStream out) throws IOException {
        writeImage(image, out, PNGType.RGBA, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    private static void writeImage(Bitmap image, OutputStream out, PNGType type) throws IOException {
        writeImage(image, out, type, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    private static void writeImage(Bitmap image, OutputStream out, PNGType type, int compressLevel) throws IOException {
        new PNGWriter(out, type, compressLevel).write(asArgbImage(image));
    }
}